package com.example.cafespot.googleMap

import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.cafespot.R
import com.example.cafespot.hotpage.HotItem
import com.example.cafespot.hotpage.HotViewModel
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.io.IOException
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

private const val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
private const val MAPS_API_KEY = "AIzaSyDUOy0M7YgHYOV72Mpe9WixT8Aq52a6544"

class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mapView: MapView
    private var location: Location? = null
    private var map: GoogleMap? = null
    private var locationPermissionGranted = true
    private var cafeList = listOf<HotItem>()

    private lateinit var searchText:EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val filterFragment = FilterFragment()
                val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.activity_map_fragment, filterFragment)
                transaction.commit()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = view.findViewById(R.id.mapView_fragment)
        initGoogleMap(savedInstanceState)
        val viewModel = ViewModelProvider(requireActivity()).get(HotViewModel::class.java)
        viewModel.currentLocation.observe(viewLifecycleOwner){
            println("MapFrag_currentLocation:" + it.latitude)
            location = it
        }

//        searchText = view.findViewById(R.id.searchbar)
//        init()
        return view
    }

    private fun init(){
        searchText.setOnEditorActionListener { _, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                || actionId == EditorInfo.IME_ACTION_DONE
                || keyEvent.action == KeyEvent.ACTION_DOWN
                || keyEvent.action == KeyEvent.KEYCODE_ENTER){
                // execute method for searching
                geoLocate()
                }
                false
            }
    }

    private fun geoLocate() {
        println("geolocating")
        val searchString = searchText.text.toString()
        val geoCoder = Geocoder(requireContext())
        var list = listOf<Address>()
        try {
            list = geoCoder.getFromLocationName(searchString, 1)
        } catch (e:IOException){
            e.printStackTrace()
        }
        if (list.size>0){
            val address = list.get(0)
            println(address)
        }
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
//        MapsInitializer.initialize(context)
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
        println("MapFragment_getLocation")
        if (location != null) {
            println("lat: " + location!!.latitude + " long: " + location!!.longitude)
            val myLocation = LatLng(location!!.latitude, location!!.longitude)
            val shopLocation1 = LatLng(25.034500604378778, 121.44670054553309)
            val shopLocation2 = LatLng(25.05065755909833, 121.44745357651915)
            val shopLocation3 = LatLng(25.049909146459317, 121.44495375771021)
            val shopLocation4 = LatLng(25.045793968113262, 121.4487806645006)
            val shopLocation5 = LatLng(25.041582759399482, 121.44478557924646)

//            for (cafe in cafeList){
            map?.addMarker(MarkerOptions().position(shopLocation1).title("BUNA Cafe"))
            map?.addMarker(MarkerOptions().position(shopLocation2).title("KARA Cafe"))
            map?.addMarker(MarkerOptions().position(shopLocation3).title("TEMP Cafe"))
//            }
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 13f))
        }
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
//                lastKnownLocation = null
//                getLocationPermission()
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
         * @return A new instance of fragment MapFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            MapFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}