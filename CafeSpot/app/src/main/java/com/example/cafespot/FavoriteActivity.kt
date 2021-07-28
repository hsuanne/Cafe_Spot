package com.example.cafespot

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.cafespot.googleMap.FilterFragment
import com.example.cafespot.googleMap.MapActivity
import com.example.cafespot.googleMap.MapInfoAdapter
import com.example.cafespot.hotpage.CustomPageTransformer
import com.example.cafespot.hotpage.HotViewModel
import com.example.cafespot.main.MainActivity
import com.example.cafespot.profile.ProfileActivity
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

private const val MAPS_API_KEY = "AIzaSyDUOy0M7YgHYOV72Mpe9WixT8Aq52a6544"

class FavoriteActivity : AppCompatActivity(), OnMapReadyCallback {
//    private var userLat = 25.048685
//    private var userLong = 121.756540
    private lateinit var map: GoogleMap
    private var locationPermissionGranted = true
    private lateinit var location: Location
    private lateinit var userLocation: LatLng
    private lateinit var tmpLocation:LatLng
    private var imgItemList = listOf(ImgItem("BUNA Cafe", R.drawable.cafe_shop1),
        ImgItem("KARA Cafe",R.drawable.cafe_shop2), ImgItem("TEMP Cafe",R.drawable.cafe_shop3),
        ImgItem("BU Cafe",R.drawable.cafe_shop4), ImgItem("NA Cafe",R.drawable.cafe_shop5))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        val hotViewModel = ViewModelProvider(this).get(HotViewModel::class.java)

        // post userLocation to back-end and get a list of location back
        location = intent.extras?.getParcelable<Location>("location")!!
        userLocation = LatLng(location.latitude, location.longitude)
        tmpLocation = userLocation
        hotViewModel.fetchAroundList("0", userLocation.latitude, userLocation.longitude)
        hotViewModel.currentLocation.value = location

        // adapter
        val viewPager2 = findViewById<ViewPager2>(R.id.viewPager_favorite)
        val adapter = MapInfoAdapter(this, hotViewModel)
        viewPager2.apply {
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 3
            getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }

        val compositePageTransformer: CompositePageTransformer = CompositePageTransformer()
        compositePageTransformer.apply {
            addTransformer(MarginPageTransformer(20)) //圖跟圖之間的間距
        }
        viewPager2.setPageTransformer(compositePageTransformer)

        // autoCompleteFragment
        Places.initialize(this, MAPS_API_KEY)
        val placesClient = Places.createClient(this)

        val autocompleteFragment: AutocompleteSupportFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autocompleteFragment.setTypeFilter(TypeFilter.ESTABLISHMENT)
        autocompleteFragment.setCountries("TW")
        autocompleteFragment.setPlaceFields(
            Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG
            )
        )

        // when user taps one of the predictions
        var placeName: String = ""
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                viewPager2.adapter = null
                Log.i(
                    "FavActivity", "Place: " + place.name +
                            "lat: " + place.latLng?.latitude + "long: " + place.latLng?.longitude
                )
                var userLat = place.latLng?.latitude!!
                var userLong = place.latLng?.longitude!!
                tmpLocation = LatLng(userLat, userLong)
                placeName = place.name!!

                // map ui
                map.clear()
                map?.moveCamera(CameraUpdateFactory.newLatLngZoom(tmpLocation, 13f))
                map?.addMarker(
                    MarkerOptions().position(tmpLocation).title(place.name)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                )


                // post userLocation to back-end and get a list of location back
                hotViewModel.fetchAroundList("0", userLat, userLong)
            }

            override fun onError(status: Status) {
                Log.i("FavActivity", "An error occurred: $status")
            }
        })

        // set map marker on shop location
        hotViewModel.aroundList.observe(this) { hotItemList ->
                println("set map marker on shop location:${hotItemList.size}")
                map.clear()
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(tmpLocation, 15f))
                map.addMarker(
                    MarkerOptions().position(tmpLocation).title(placeName)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                )
                for (shop in hotItemList) {
                    var shopLocation = LatLng(shop.latitude, shop.longitude)
                    map.addMarker(MarkerOptions().position(shopLocation).title(shop.cafeName))
                }

            viewPager2.adapter = adapter
            adapter.submitList(hotItemList.toMutableList())

                // marker click listener
                map.setOnMarkerClickListener {
                    it.showInfoWindow()

                    for (item in hotItemList) {
                        if (item.cafeName == it.title) {
                            viewPager2.currentItem = hotItemList.indexOf(item)
                        }
                    }
                    true
                }
        }

        // set autocomplete searchBar ui
        autocompleteFragment.view?.setBackgroundColor(Color.WHITE)

        // bind view
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // filter button
        val filterButton: Button = findViewById(R.id.map_filter_button)
        filterButton.setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
//            var filterFragment: FilterFragment = FilterFragment()
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.map, filterFragment)
//                .addToBackStack(null)
//                .commit()
//            filterButton.visibility = View.INVISIBLE
        }

        // bottomNavigationBar
        val bottomNavigationBar = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationBar.selectedItemId = R.id.favorite
        bottomNavigationBar.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    hotViewModel.aroundList.value = listOf()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
//                    overridePendingTransition(0,0)
                }
                R.id.profile -> {
                    hotViewModel.aroundList.value = listOf()
                    var bundleLocation = Bundle().apply { putParcelable("location", location) }
                    startActivity(
                        Intent(this, ProfileActivity::class.java)
                            .putExtras(bundleLocation)
                    )
                    finish()
                }
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        val hotViewModel = ViewModelProvider(this).get(HotViewModel::class.java)

        if (App.currentUserLiveData.value != null) {
            if (App.currentUserLiveData.value?.size!! > 1) {
                var selectedL = App.currentUserLiveData.value?.joinToString(",")!!
                println(selectedL)
                hotViewModel.fetchAroundList(selectedL, tmpLocation.latitude, tmpLocation.longitude)
            } else if (App.currentUserLiveData.value?.size!! == 1) {
                var selectedL = App.currentUserLiveData.value!!.joinToString()
                hotViewModel.fetchAroundList(selectedL, tmpLocation.latitude, tmpLocation.longitude)
            } else if(App.currentUserLiveData.value?.size!! == 0){
                Toast.makeText(this, "此地點附近無符合條件的店家", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        updateLocationUI()

        Toast.makeText(this, "藍點: 您的定位 ; 紅點: 店家位置", Toast.LENGTH_LONG).show()
        // set user location as default
        location = intent.extras?.getParcelable<Location>("location")!!
        userLocation = LatLng(location.latitude, location.longitude)
        tmpLocation = userLocation
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 13f))
    }

    private fun updateLocationUI() {
        println("updateLocationUI")
        if (map == null) {
            return
            println("map==null")
        }
        try {
            if (locationPermissionGranted) {
                println("updateLocationUI:granted")
                map?.isMyLocationEnabled = true
                map?.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                println("updateLocationUI:requesting")
                map?.isMyLocationEnabled = false
                map?.uiSettings?.isMyLocationButtonEnabled = false
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
}

class ImgItem(var title:String, var img:Int){
}
