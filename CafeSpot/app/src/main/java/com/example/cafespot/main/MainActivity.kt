package com.example.cafespot.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.cafespot.FavoriteActivity
import com.example.cafespot.R
import com.example.cafespot.databinding.ActivityMainBinding
import com.example.cafespot.hotpage.CustomPageTransformer
import com.example.cafespot.hotpage.HotViewModel
import com.example.cafespot.profile.ProfileActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    companion object{
        var locationPermissionGranted = false
        val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    }
    private var location: Location? = null
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewPager2: ViewPager2
    lateinit var hotViewModel: HotViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //map
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getLocationPermission() //向用戶存取權限
        getDeviceLocation()

        hotViewModel = HotViewModel()

        //viewPager2
        viewPager2 = binding.viewPagerMain
        val mainItems:MutableList<MainItem> = mutableListOf()
        mainItems.add(MainItem("地圖模式","自訂位置與條件搜尋咖啡廳"))
        mainItems.add(MainItem("熱門推薦","在您附近的高人氣咖啡廳"))
        mainItems.add(MainItem("情境模式","找到符合您需求的咖啡廳"))

        val adapter = MainAdapter(this, hotViewModel)
        viewPager2.adapter = adapter
        adapter.submitList(mainItems)

        // get pictures
        hotViewModel.currentLocation.observe(this){
            hotViewModel.fetchHotItemList("around", 0, it.latitude, it.longitude)
        }

        val mainBackground = binding.mainBg

        hotViewModel.cafePicList.observe(this) {
            var cleanList = it.toMutableList()

            // 根據page position換背景
            val mainBackground = binding.mainBg
            if (cleanList.isNotEmpty()) {
                var iterator = cleanList.iterator()
                while (iterator.hasNext()){
                    var str = iterator.next()
                    if (str == ""){
                        iterator.remove()
                    }
                }

                viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        when (position) {
                            0 -> {
                                var num = rand(0, cleanList.size-1)
                                Glide.with(this@MainActivity)
                                    .load(cleanList[num])
                                    .into(mainBackground)
                            }
                            1 -> {
                                var num = rand(0, cleanList.size-1)
                                Glide.with(this@MainActivity)
                                    .load(cleanList[num])
                                    .into(mainBackground)
                                binding.mainRoot.setBackgroundResource(0)
                            }
                            2 -> {
                                var num = rand(0, cleanList.size-1)
                                Glide.with(this@MainActivity).load(cleanList[num])
                                    .into(mainBackground)
                            }
                        }

                        super.onPageSelected(position)
                    }
                })
            }
        }

        viewPager2.apply {
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 3
            currentItem = 1
            getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }

        val compositePageTransformer: CompositePageTransformer = CompositePageTransformer()
        compositePageTransformer.apply {
            addTransformer(MarginPageTransformer(40)) //圖跟圖之間的間距
            addTransformer(CustomPageTransformer())
        }
        viewPager2.setPageTransformer(compositePageTransformer)

        val bottomNavigationBar = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationBar.selectedItemId = R.id.home
        bottomNavigationBar.setOnNavigationItemSelectedListener{ menuItem ->
            when(menuItem.itemId){
                R.id.favorite -> {
                    var bundleLocation = Bundle().apply { putParcelable("location", location) }
                    startActivity(Intent(this, FavoriteActivity::class.java)
                        .putExtras(bundleLocation))
                    finish()
                }
                R.id.profile -> {
                    var bundleLocation = Bundle().apply { putParcelable("location", location) }
                    startActivity(Intent(this, ProfileActivity::class.java)
                        .putExtras(bundleLocation))
                    finish()
                }
            }
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionGranted = false
        when(requestCode){
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                    println("onRequestPermissionsResult=true")
                    getDeviceLocation()
                }
            }
        }
    }

    fun getLocationPermission(){
        if (ActivityCompat.checkSelfPermission(this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
            println("getLocationPermission_granted")
        } else {
            println("getLocationPermission_requesting")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                println("getLastKnownLocation_granted")
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) {
                    if (it.isSuccessful) {
                        location = it.result
                        println("location:" + location?.latitude)
                        if(location!=null) {
                            println("lat: " + location!!.latitude + " long: " + location!!.longitude)
                            hotViewModel.setCurrentLocation(location!!)
                        } else {println("it.result=null")}
                    } else {
                        Log.e("CafeActivity", "Exception: %s", it.exception)
                    }
                }
            } else {
                println("no location permissionA")
                Toast.makeText(this, "no location permission", Toast.LENGTH_SHORT)
            }
        } catch (e:SecurityException){
            Log.e("Exception: %s", e.message, e)
        }
    }
}

fun rand(start: Int, end: Int): Int {
    require(start <= end) { "Illegal Argument" }
    return (Math.random() * (end - start + 1)).toInt() + start
}