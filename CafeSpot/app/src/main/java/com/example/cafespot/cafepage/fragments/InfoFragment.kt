package com.example.cafespot.cafepage.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cafespot.R
import com.example.cafespot.hotpage.HotItem
import com.example.cafespot.hotpage.HotViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "hot"
private const val ARG_PARAM2 = "param2"
private const val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
private const val MAPS_API_KEY = "AIzaSyDUOy0M7YgHYOV72Mpe9WixT8Aq52a6544"

/**
 * A simple [Fragment] subclass.
 * Use the [InfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InfoFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mapView: MapView
    private lateinit var cafeName: TextView
    private lateinit var address: TextView
    private var hotItem: HotItem? = null
    private var location: Location? = null
    private var shopLat:Double = 0.0
    private var shopLong:Double = 0.0

    //map
    private var lastKnownLocation: Location? = null
    private var locationPermissionGranted = true
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    private var map: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        location = arguments?.getParcelable("location")
//        println("infoFrag_location: " + location?.latitude)
//        hotItem = arguments?.getParcelable("hot")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_info, container, false)
        mapView = view.findViewById(R.id.mapView)
        cafeName = view.findViewById(R.id.info_cafe_name)
        address = view.findViewById(R.id.cafe_address_textview)
        val mrt:TextView = view.findViewById(R.id.mrt_stop_textview)
        val bus:TextView = view.findViewById(R.id.bus_stop_textview)
        val longsit:TextView = view.findViewById(R.id.longsit_answer_textview)
        val meal:TextView = view.findViewById(R.id.meal_answer_textview)
        val dessert:TextView = view.findViewById(R.id.dessert_answer_textview)
        val rating:RatingBar = view.findViewById(R.id.rating_bar)
        val rankingAverage:TextView = view.findViewById(R.id.ranking_average)
        val website:ImageView = view.findViewById(R.id.facebook_logo)
        val viewModel = ViewModelProvider(requireActivity()).get(HotViewModel::class.java)

        viewModel.currentHotItem.observe(viewLifecycleOwner) { it ->
            println("infoFrag_currentItem:" + it.cafeName)
            hotItem = it
            cafeName.text = it.cafeName
            address.text = it.address
            rating.rating = it.averageScore
            rankingAverage.text = it.averageScore.toString()
            shopLat = it.latitude
            shopLat = it.longitude
            mrt.text = when(it?.mrtStationName){
                null -> "-"
                else -> it?.mrtStationName
            }
            bus.text = when(it?.busStationName){
                null -> "-"
                else -> it?.busStationName
            }
            longsit.text = when(it?.hasLimitedTime){
                "No" -> "Yes"
                "Yes" -> "No"
                else -> "-"
            }
            meal.text = when(it?.hasFood){
                "Yes" -> "Yes"
                "No" -> "No"
                else -> "-"
            }
            dessert.text = when(it?.hasDessert){
                "Yes" -> "Yes"
                "No" -> "No"
                else -> "-"
            }
            if (longsit.text == "Yes"){
               longsit.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_macaron))
            } else {
                longsit.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_macaron))
            }
            if (meal.text == "Yes"){
                meal.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_macaron))
            } else {
                meal.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_macaron))
            }

            if (dessert.text == "Yes"){
                dessert.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_macaron))
            } else {
                dessert.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_macaron))
            }

            val webUrl = it.officeWebsite
            website.setOnClickListener {
                val uri = Uri.parse(webUrl) // missing 'http://' will cause crashed
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        }
//        viewModel.setCurrentLocation(location!!)
        viewModel.currentLocation.observe(viewLifecycleOwner){
            println("infoFrag_currentLocation:" + it.latitude)
            location = it
        }
        viewModel.currentShopLocation.observe(viewLifecycleOwner){
            println("infoFrag_currentShopLocation:" + it[0])
            shopLat = it[1]
            shopLong = it[0]
            val shopLocation = LatLng(shopLong, shopLat)
            map?.addMarker(MarkerOptions().position(shopLocation).title(hotItem?.cafeName))
        }
        initGoogleMap(savedInstanceState)
        return view
    }


    private fun initGoogleMap(savedInstanceState: Bundle?) {
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }
        mapView.onCreate(mapViewBundle)
        mapView.getMapAsync(this)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onMapReady(map: GoogleMap) {
        println("onMapReady")
                this.map = map
                updateLocationUI()
                getLocation() //取得當前位置
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    private fun getLocation() {
        println("getLocation")
        if (location != null) {
            println("lat: " + location!!.latitude + " long: " + location!!.longitude)
            val myLocation = LatLng(location!!.latitude, location!!.longitude)
//            val shopLocation = LatLng(121.446700500000000, 25.034500600000000)
                val shopLocation = LatLng(shopLat, shopLong)
                map?.addMarker(MarkerOptions().position(shopLocation).title(hotItem?.cafeName))
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 13f))
        }
    }

    private fun getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity().applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
            println("getLocationPermission_granted")
        } else {
            println("getLocationPermission_requesting")
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted = true
                    println("onRequestPermissionsResult=true")
                }
            }
        }
        updateLocationUI()
    }

    private fun updateLocationUI() {
        println("updateLocationUI")
        if (map == null) {
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
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RankingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(location: Location, shopLocation: Location) =
            InfoFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("location", location)
                }
            }
    }
}