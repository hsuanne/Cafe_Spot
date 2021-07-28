package com.example.cafespot.profile

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cafespot.*
import com.example.cafespot.databinding.ActivityProfileBinding
import com.example.cafespot.hotpage.HotViewModel
import com.example.cafespot.main.MainActivity
import com.example.cafespot.ui.login.LoginActivity
import com.example.cafespot.ui.login.RegisterActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.logging.HttpLoggingInterceptor
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.lang.Exception

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private var bitmap: Bitmap? = null
    private lateinit var userImg:ImageView
    private lateinit var edit_hint:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val location = intent.extras?.getParcelable<Location>("location")!!

        val bottomNavigationBar = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationBar.selectedItemId = R.id.profile
        bottomNavigationBar.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                R.id.favorite -> {
                    var bundleLocation = Bundle().apply { putParcelable("location", location) }
                    startActivity(
                        Intent(this, FavoriteActivity::class.java)
                            .putExtras(bundleLocation)
                    )
                    finish()
                }
            }
            true
        }

        // GET
        val hotViewModel = ViewModelProvider(this).get(HotViewModel::class.java)
        hotViewModel.fetchUserProfile()

        // adapter
        val adapter = RecordAdapter(this, hotViewModel)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_profile)
        recyclerView.adapter = adapter

        // logout
        val logout: ImageView = binding.profileSetting
        val sp = SharedPref.getInstance(this)
        fun logout() {
            sp.removeUser()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // bind view
        userImg = binding.profileImg
//        val userImg = binding.profileImg
        val username = binding.profileUsername
        val scoreCount = binding.profileScoreCount
        val commentCount = binding.profileCommentCount
        var imgUrl:String = ""
        hotViewModel.profileData.observe(this) {
            if (it.userPicture == "picture/profilePic.jpg") {
                userImg.setImageResource(R.drawable.profilepic)
            } else {
                Glide.with(this).load(it.userPicture).into(userImg)
            }
            username.text = it.userName
            scoreCount.text = it.scoreCount.toString()
            commentCount.text = it.discussCount.toString()
            imgUrl = it.userPicture
            var userData = User(USER_ID, it.userName, it.userEmail, it.userPicture)
            sp.saveUser(userData)
        }

        hotViewModel.recordLive.observe(this) {
            adapter.submitList(it)
        }

        // popup setting menu
        val iv_image: ImageView = binding.profileSetting
        val edit_icon: ImageView = binding.editIcon
        val edit_cardview: CardView = binding.profileProfilepic
        edit_hint = binding.editHint
        val edit_username: EditText = binding.editUsername
        val edit_save: TextView = binding.editSave

        bitmap = null

        val popupMenu = PopupMenu(this, iv_image)
        popupMenu.inflate(R.menu.popup_menu_setting)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.pop_edit -> {
                    // 顯示編輯按鈕
                    edit_icon.visibility = View.VISIBLE
                    userImg.setImageResource(R.drawable.white_bg)
                    edit_cardview.visibility = View.VISIBLE
                    edit_hint.visibility = View.VISIBLE
                    edit_save.visibility = View.VISIBLE

                    // 按下edit_username 出現 edittext
                    edit_icon.setOnClickListener {
                        edit_icon.visibility = View.INVISIBLE
                        username.visibility = View.INVISIBLE
                        edit_username.visibility = View.VISIBLE
                    }

                    var put_username = ""
                    // 按了儲存之後post後端 + 重新fetchUserProfile
                    edit_save.setOnClickListener {
                        // 拿到使用者輸入的文字
                            val new_username = edit_username.text.toString()
                            if (edit_username.visibility == View.VISIBLE && new_username.length <= 0) {
                                Toast.makeText(this, "請輸入內容", Toast.LENGTH_SHORT).show()
                                return@setOnClickListener
                            }
                            put_username = new_username


                        // TODO: 等後端好了即可拿掉
                        if(put_username.isNotEmpty()){ username.text = put_username }
                        edit_save.visibility = View.INVISIBLE
                        edit_username.visibility = View.INVISIBLE
                        edit_icon.visibility = View.INVISIBLE
                        username.visibility = View.VISIBLE
                        edit_hint.visibility = View.INVISIBLE
                        if(bitmap == null){
                            Glide.with(this).load(imgUrl).into(userImg)
                        }
                        userImg.visibility = View.VISIBLE

                        // put給後端
                        if (put_username.isEmpty() && bitmap == null){
                            Toast.makeText(this, "資料並未被變更", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }
                        CoroutineScope(Dispatchers.Default).launch{
                            launch { hotViewModel.put(USER_ID, bitmap, put_username) }.join()
                        }
                    }

                    hotViewModel.putStatus.observe(this){
                        if (it){
                            hotViewModel.fetchUserProfile()
                        }
                    }


                    // 讓使用者挑選照片
                    edit_cardview.setOnClickListener {
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.type = "image/*"
                        startActivityForResult(intent, RegisterActivity.PICK_IMAGE)
                    }

                    true
                }
                R.id.pop_logout -> {
                    Toast.makeText(this, "下次再見!", Toast.LENGTH_SHORT)
                        .show()
                    logout()
                    true
                }
                else -> true
            }
        }

        // popup bind setting icon
        iv_image.setOnClickListener {
            try {
                val popup = PopupMenu::class.java.getDeclaredField("mPopup")
                popup.isAccessible = true
                val menu = popup.get(popupMenu)
                menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(menu, true)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                popupMenu.show()
            }
            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RegisterActivity.PICK_IMAGE && resultCode == RESULT_OK) {
            val selectedImg = data?.data

            try {
                if (Build.VERSION.SDK_INT < 29) {
                    bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImg)
                    userImg.setImageBitmap(bitmap)
                    edit_hint.visibility = View.INVISIBLE
                } else {
                    val source = ImageDecoder.createSource(this.contentResolver, selectedImg!!)
                    bitmap = ImageDecoder.decodeBitmap(source)
                    userImg.setImageBitmap(bitmap)
                    edit_hint.visibility = View.INVISIBLE
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}

suspend fun putServer(userId: Int, bitmap: Bitmap?, userName: String): Boolean {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

//    val client = OkHttpClient()

    var requestBody:RequestBody? = null
    if (bitmap!=null) {
        println("bitmap not null")

        //random filename
        val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val randomString = (1..10)
            .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")

        //Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        val bitmapdata = bos.toByteArray()
        requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("userName", userName)
            .addFormDataPart("passWord", "")
            .addFormDataPart("mail", "")
            .addFormDataPart(
                "userPicture", "$randomString.jpg",
                RequestBody.create("image/*jpg".toMediaTypeOrNull(), bitmapdata)
            ).build()
    }

    if (bitmap==null){
        println("bitmap is null")
        requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("userName", userName)
            .addFormDataPart("passWord", "")
            .addFormDataPart("mail", "")
            .addFormDataPart("userPicture", "").build()
    }

//    val requestBody = MultipartBody.Builder()
//        .setType(MultipartBody.FORM)
//        .addFormDataPart("userName", userName) //(your api property name, actual value)
//        .addFormDataPart(
//            "userPicture", "profilePic.jpg",
//            RequestBody.create("image/*jpg".toMediaTypeOrNull(), bitmapdata)
//        )
//        .build()

    val request = Request.Builder().url("http://35.221.213.180/api/User/UserInfo/$userId")
        .put(requestBody!!)
        .build()

    return suspendCancellableCoroutine {
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.stackTrace
            }

            override fun onResponse(call: Call, response: Response) {
                val httpCode = response.code
                if (response.isSuccessful) {
                    val body = response?.body?.string()
                    println("httpCode:" + httpCode + " body:" + body)
                    it.resumeWith(Result.success(true))
                } else {
                    println("response failure:" + httpCode)
                    it.resumeWith(Result.success(false))
                }
            }
        })
        it.invokeOnCancellation { //連不到的話就取消
            client.newCall(request).cancel()
        }
    }
}

