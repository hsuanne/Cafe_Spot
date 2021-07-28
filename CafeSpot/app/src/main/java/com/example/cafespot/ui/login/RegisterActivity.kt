package com.example.cafespot.ui.login

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns
import android.text.style.TtsSpan
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.cafespot.*
import com.example.cafespot.databinding.ActivityRegisterBinding
import com.example.cafespot.hotpage.HotViewModel
import com.example.cafespot.main.MainActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.logging.HttpLoggingInterceptor
import java.io.*


class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var image: ImageView
    private lateinit var bitmap: Bitmap
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = binding.registerName
        val username = binding.registerUser
        val password = binding.registerPassword
        val registerButton = binding.registerButton
        val login = binding.loginWithAccountTextview
        image = binding.registerImg
        val uploadImg = binding.uploadImg
        bitmap = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.profilepic)

        val registerViewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)
        val hotViewModel = HotViewModel()

        registerViewModel.loginFormState.observe(this, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.nameError != null) {
                name.error = getString(loginState.nameError)
            }
            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this, Observer {
            val loginResult = it ?: return@Observer

//            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                val sp = SharedPref.getInstance(this)
                    .saveUser(User(USER_ID, "username", USER_NAME, "url"))
                updateUiWithUser(loginResult.success)
                startActivityForResult(Intent(this, MainActivity::class.java), RESULT_OK)
                finish()
            }
//            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
            finish()
        })

        username.afterTextChanged {
            registerViewModel.loginDataChanged(
                name.text.toString(),
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                registerViewModel.loginDataChanged(
                    name.text.toString(),
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        registerViewModel.register(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }
        }

        registerButton.setOnClickListener {
            //post info to back-end
            loginViewModel.upload(bitmap, name.text.toString(), password.text.toString(), username.text.toString())
        }

        loginViewModel.isUpload.observe(this){
            if (it == true){
                loginViewModel.userId.value = 0
                loginViewModel.fetchLogin(
                    username.text.toString(),
                    password.text.toString()
                ) //set userId livedata
            }
        }

        loginViewModel.userId.observe(this) {
            println("userId:" + it)
            if (it != 0) {
                loginViewModel.login(username.text.toString(), password.text.toString(), it)
            }
        }

        login.setOnClickListener {
            startActivity(Intent(this, EmailLoginActivity::class.java))
            finish()
        }

        uploadImg.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_OK) {
            startActivity(Intent(this, GoogleActivity::class.java))
        }

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            val selectedImg = data?.data

            try {
                if (Build.VERSION.SDK_INT < 29) {
                    bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImg)
                    image.setImageBitmap(bitmap)

                } else {
                    val source = ImageDecoder.createSource(this.contentResolver, selectedImg!!)
                    bitmap = ImageDecoder.decodeBitmap(source)
                    image.setImageBitmap(bitmap)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
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

    companion object {
        const val PICK_IMAGE = 1
    }
}


fun bitmapToFile(bitmap: Bitmap, fileNameToSave: String): File? { // File name like "image.png"
    //create a file to write bitmap data
    var file: File? = null
    return try {
        file = File(
            Environment.getExternalStorageDirectory().toString() + File.separator + fileNameToSave
        )
        file.createNewFile()

        //Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos) // YOU can also save it in JPEG
        val bitmapdata = bos.toByteArray()

        //write the bytes in file
        val fos = FileOutputStream(file)
        fos.write(bitmapdata)
        fos.flush()
        fos.close()
        file
    } catch (e: Exception) {
        e.printStackTrace()
        file // it will return null
    }
}