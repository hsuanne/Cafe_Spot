package com.example.cafespot.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.example.cafespot.*
import com.example.cafespot.databinding.ActivityLoginBinding
import com.example.cafespot.hotpage.HotActivity
import com.example.cafespot.main.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import pl.droidsonroids.gif.GifDrawable


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val TAG = "LoginActivity"
    val RC_SIGN_IN:Int = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //TODO: 先判斷 USERNAME == sp.getUser，true就直接跳轉到首頁
        val sp = SharedPref.getInstance(this)
        println("SP:" + sp.getUser()?.userId)
        if(sp.getUser()!=null){
            USER_ID = sp.getUser()!!.userId
            println("USERID:$USER_ID")
            USER_NAME = sp.getUser()!!.username
            println("USERNAME:$USER_NAME")
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(this!!, gso)

        val gif = binding.testGIF
//        val googleLogin = binding.googleButton
        val emailLogin = binding.login
        val serverClientId = getString(R.string.server_client_id)

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val account = GoogleSignIn.getLastSignedInAccount(this)
//        googleLogin.setOnClickListener {
//            val signInIntent = mGoogleSignInClient.getSignInIntent()
//            startActivityForResult(signInIntent, RC_SIGN_IN)
//        }

        emailLogin.setOnClickListener {
            println("email login")
            val emailIntent = Intent(this, EmailLoginActivity::class.java)
            startActivityForResult(emailIntent, RC_SIGN_IN)
        }
        //GIF
        try {
            val gifDrawable = GifDrawable(resources, R.drawable.app_video)
            gif.setImageDrawable(gifDrawable)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode==RC_SIGN_IN){
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            handleSignInResult(task)
//        }
//    }

    private fun handleSignInResult(completedTask:Task<GoogleSignInAccount>){
        try {
            val accountG = completedTask.getResult(ApiException::class.java)
            val account = GoogleSignIn.getLastSignedInAccount(this)
            if (account!=null){
                val personName = account.displayName
                val personGiveName = account.givenName
                val personFamilyName = account.familyName
                val personEmail = account.email
                val personId = account.id
                val personPhoto = account.photoUrl
            }
            startActivity(Intent(this, HotActivity::class.java))
        } catch (e:ApiException){
            e.printStackTrace()
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
