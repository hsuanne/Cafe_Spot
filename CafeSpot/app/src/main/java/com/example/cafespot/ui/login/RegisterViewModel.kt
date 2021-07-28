package com.example.cafespot.ui.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cafespot.R
import com.example.cafespot.USER_ID
import com.example.cafespot.data.LoginRepository
import com.example.cafespot.data.Result
import com.example.cafespot.data.model.LoggedInUser
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.*
import java.io.IOException
import java.util.*

class RegisterViewModel() : ViewModel() {

    private val _loginForm = MutableLiveData<RegisterFormState>()
    val loginFormState: LiveData<RegisterFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun register(username: String, password: String) {
        // can be launched in a separate asynchronous job
        val result = registerCheck(username, password)

        if (result is Result.Success) {
            _loginResult.value =
                LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
        } else {
            _loginResult.value = LoginResult(error = R.string.login_failed)
        }
    }

    fun loginDataChanged(name: String, username: String, password: String) {
        if (!isNameValid(name)) {
            _loginForm.value = RegisterFormState(nameError = R.string.invalid_username)
        } else if (!isUserNameValid(username)) {
            _loginForm.value = RegisterFormState(usernameError = R.string.invalid_email)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = RegisterFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = RegisterFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isNameValid(name:String):Boolean{
        return name.isNotBlank() && name.length<=10
    }

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

    fun registerCheck (username: String, password: String): Result<LoggedInUser> {
        try {
            // TODO: handle loggedInUser authentication
            val fakeUser = LoggedInUser(UUID.randomUUID().toString(), "Jane Doe")
            val admin = LoggedInUser("admin", "Admin")
            if (username == "admin123" && password == "admin123") {
                return Result.Success(admin)
            } else {
                return Result.Error(IOException("username or password mistake"))
            }
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }
}
