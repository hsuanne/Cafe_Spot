package com.example.cafespot.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModelProvider
import com.example.cafespot.databinding.ActivityEmailLoginBinding
import androidx.lifecycle.Observer
import com.example.cafespot.*
import com.example.cafespot.main.MainActivity

class EmailLoginActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityEmailLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 先判斷 USERNAME == sp.getUser，true就直接跳轉到首頁
        val sp = SharedPref.getInstance(this)
        println("SP:" + sp.getUser()?.userId)
        if(sp.getUser()!=null){
            USER_ID = sp.getUser()!!.userId
            println("USERID:$USER_ID")
            USER_NAME = sp.getUser()!!.username
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        val username = binding.username
        val password = binding.password
        val login = binding.loginButton
        val loading = binding.loading
        val register = binding.registerTextview

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@EmailLoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@EmailLoginActivity, Observer {
            val loginResult = it ?: return@Observer

//            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                //TODO:fetchUserInfo
                val sp = SharedPref.getInstance(this).saveUser(User(USER_ID,"username",USER_NAME, "url"))
                updateUiWithUser(loginResult.success)
                startActivityForResult(Intent(this, MainActivity::class.java), RESULT_OK)
            }
//            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
            finish()
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

//            setOnEditorActionListener { _, actionId, _ ->
//                when (actionId) {
//                    EditorInfo.IME_ACTION_DONE ->
//                        loginViewModel.login(
//                            username.text.toString(),
//                            password.text.toString(),
//                            USER_ID
//                        )
//                }
//                false
//            }
        }

        login.setOnClickListener {
            loading.visibility = View.VISIBLE
            loginViewModel.userId.value = 0
            loginViewModel.fetchLogin(username.text.toString(), password.text.toString())
        }
        loginViewModel.userId.observe(this){
            println("userId:" + it)
            if (it!=0){
                loginViewModel.login(username.text.toString(), password.text.toString(), it)
            }
        }

        register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==RESULT_OK){
            startActivity(Intent(this, GoogleActivity::class.java))
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