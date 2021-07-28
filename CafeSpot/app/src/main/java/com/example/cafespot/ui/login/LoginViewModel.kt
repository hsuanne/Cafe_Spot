package com.example.cafespot.ui.login

import android.graphics.Bitmap
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cafespot.data.LoginRepository
import com.example.cafespot.data.Result
import com.example.cafespot.R
import com.example.cafespot.USER_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.coroutines.resume


class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    var userId = MutableLiveData<Int>()
    var isUpload = MutableLiveData<Boolean>()

    fun login(username: String, password: String, userId:Int) {
        // can be launched in a separate asynchronous job
        val result = loginRepository.login(username, password, userId)

        if (result is Result.Success) {
            _loginResult.value =
                LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
        } else {
            _loginResult.value = LoginResult(error = R.string.login_failed)
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    fun fetchLogin(email: String, password: String){
        CoroutineScope(Dispatchers.Default).launch {
            viewModelScope.launch {
                userId.value = checkLogin(email, password)!!
                println("fetchLogin:" + userId.value)
            }
        }
    }

    fun upload(bitmap: Bitmap, userName: String, passWord: String, email: String){
        CoroutineScope(Dispatchers.Default).launch {
            viewModelScope.launch {
                isUpload.value = uploadServer(bitmap, userName, passWord, email)!!
                println("isUpload:" + isUpload.value)
            }
        }
    }
}

suspend fun checkLogin(email: String, password: String): Int {
    println("Attempting to Check Login")

    val url =
        "http://35.221.213.180/api/User/$email/$password"
    val request = Request.Builder().url(url).build()

    val client = OkHttpClient()
    val call = client.newCall(request)
    //requestCall: 非同步
    return suspendCancellableCoroutine {
        call.enqueue(object : Callback { //callback:做完之後再回傳結果
            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute Request")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val body = response?.body?.string()
                    println("body:"+ body)

                    val gson = GsonBuilder().create()
                    val result =
                        gson.fromJson<Int>(body, object : TypeToken<Int>() {}.type)
                    USER_ID = result
                    println("USER_ID:" + USER_ID)
                    it.resumeWith(kotlin.Result.success(result))
                } else {
                    println("server problem")
                }
            }
        })
        it.invokeOnCancellation { //連不到的話就取消
            call.cancel()
        }
    }
}


suspend fun uploadServer(bitmap: Bitmap, userName: String, passWord: String, email: String) :Boolean {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

//    val client = OkHttpClient()

    //random filename
    val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    val randomString = (1..10)
        .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
        .map(charPool::get)
        .joinToString("")

    //Convert bitmap to byte array
    val bos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
    val bitmapdata = bos.toByteArray()

    val requestBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("userName", userName) //(your api property name, actual value)
        .addFormDataPart("passWord", passWord)
        .addFormDataPart(
            "userPicture", "$randomString.jpg",
            RequestBody.create("image/*jpg".toMediaTypeOrNull(), bitmapdata)
        )
        .addFormDataPart("mail", email)
        .build()

    val request = Request.Builder().url("http://35.221.213.180/api/User/AddUser")
        .post(requestBody)
        .build()

    return suspendCancellableCoroutine{
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.stackTrace
            }

            override fun onResponse(call: Call, response: Response) {
                val httpCode = response.code
                if (response.isSuccessful) {
                    val body = response?.body?.string()
                    println("httpCode:" + httpCode + " body:" + body)
                    it.resumeWith(kotlin.Result.success(true))
                } else {
                    println("response failure:" + httpCode)
                    it.resumeWith(kotlin.Result.success(false))
                }
            }
        })
        it.invokeOnCancellation { //連不到的話就取消
            client.newCall(request).cancel()
        }
    }
}