package com.example.cafespot.googleMap

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.cafespot.R
import com.example.cafespot.databinding.ActivityMapBinding
import com.example.cafespot.hotpage.HotItem
import com.example.cafespot.hotpage.HotViewModel

class MapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMapBinding
    private lateinit var hotViewModel: HotViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 拆Bundle
        val intent = intent
//        val currentHotItem = intent.extras?.getParcelable<HotItem>("hot")!!
//        val location = intent.extras?.getParcelable<Location>("location")!!
//        println("MapActivity_location: " + location.latitude)

        hotViewModel = ViewModelProvider(this).get(HotViewModel::class.java)
//        hotViewModel.setCurrentHotItem(currentHotItem)
//        hotViewModel.setCurrentLocation(location)
        hotViewModel.currentLocation.observe(this){
            println("MapActivity_currentLocation_observe: " + it.latitude)
        }

//        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
//        transaction.replace(R.id.activity_map_fragment, MapFragment.newInstance(location))
//        transaction.commit()
    }
}