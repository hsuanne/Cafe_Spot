package com.example.cafespot

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

var USER_NAME:String = "user1"
var USER_ID:Int = 0

class SharedPref private constructor() {
    companion object {
        private val sharePref = SharedPref()
        private lateinit var sharedPreferences: SharedPreferences
        private const val KEY = "user"

        fun getInstance(context: Context): SharedPref {
            if (!::sharedPreferences.isInitialized) {
                synchronized(SharedPref::class.java) {
                    if (!::sharedPreferences.isInitialized) {
                        sharedPreferences = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
                    }
                }
            }
            return sharePref
        }
    }

    fun saveImage(str:String){
        sharedPreferences.edit()
            .putString("image", str)
            .apply()
    }

    fun getUser():User?{
        val str: String = sharedPreferences.getString(KEY, "") ?: return null
        val gson = GsonBuilder().create()
        return gson.fromJson<User>(str, object : TypeToken<User>() {}.type)
    }

    fun saveUser(user:User) {
        val str:String = Gson().toJson(user)
        sharedPreferences.edit()
            .putString(KEY, str)
            .apply()
    }

    fun updateUser(user: User){
        clearAll()
        saveUser(user)
    }

    fun removeUser() {
        Log.d("test","Logout")
        sharedPreferences.edit().remove(KEY).apply()
    }

    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
}

class User (var userId:Int, var username:String, var email:String, var profilePic:String)