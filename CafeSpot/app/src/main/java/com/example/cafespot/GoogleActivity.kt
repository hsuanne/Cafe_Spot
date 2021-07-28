package com.example.cafespot

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.cafespot.databinding.ActivityGoogleBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class GoogleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGoogleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGoogleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = binding.nameTextView
        val email = binding.emailTextview
        val id = binding.idTextview
        val pic = binding.pictureImageView
        val googleLogout = binding.logoutButton

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(this!!, gso)

        googleLogout.setOnClickListener {
            mGoogleSignInClient.signOut()
                .addOnCompleteListener{
                    Toast.makeText(this, "登出", Toast.LENGTH_SHORT).show()
                    finish()
                }
        }

        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account!=null){
            name.text = account.displayName
            email.text = account.email
            id.text = account.id
            Glide.with(this).load(account.photoUrl).into(pic)
        }
    }
}