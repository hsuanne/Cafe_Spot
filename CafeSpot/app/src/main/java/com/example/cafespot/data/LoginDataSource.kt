package com.example.cafespot.data

import com.example.cafespot.USER_ID
import com.example.cafespot.USER_NAME
import com.example.cafespot.data.model.LoggedInUser
import com.example.cafespot.hotpage.HotItem
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import okhttp3.*
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {
    fun login(username: String, password: String, userId:Int): Result<LoggedInUser> {
        try {
            // TODO: handle loggedInUser authentication
            val admin = LoggedInUser("admin", "Admin")

            //TODO: post to back-end, if result!=null, save userId
//            return fetchLogin(username, password)
            if (userId!=-1){
                USER_ID = userId
                USER_NAME = username
                println("loginData_USER_ID:" + USER_ID)
                val user = LoggedInUser(USER_ID.toString(), USER_NAME)
                return Result.Success(user)
            } else {
                return Result.Error(IOException("username or password mistake"))
            }

//            if (username == "admin123" && password == "admin123") {
//                return Result.Success(admin)
//            } else {
//                return Result.Error(IOException("username or password mistake"))
//            }
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }

    fun fetchLogin(email: String, password: String):Result<LoggedInUser>{
        CoroutineScope(Dispatchers.Default).launch {
            val job = async {
               checkLogin(email,password)
            }
            USER_ID = job.await()
        }
        if (USER_ID!=-1){
            println("loginData_USER_ID:" + USER_ID)
            val user = LoggedInUser(USER_ID.toString(), USER_NAME)
            return Result.Success(user)
        } else {
            return Result.Error(IOException("username or password mistake"))
        }
//        return USER_ID
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
                    println(body)

                    val gson = GsonBuilder().create()
                    val result =
                        gson.fromJson<Int>(body, object : TypeToken<Int>() {}.type)
                        USER_ID = result
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
