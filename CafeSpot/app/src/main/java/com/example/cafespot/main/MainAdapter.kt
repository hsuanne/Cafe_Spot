package com.example.cafespot.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cafespot.FavoriteActivity
import com.example.cafespot.mode.ModeActivity
import com.example.cafespot.R
import com.example.cafespot.hotpage.HotActivity
import com.example.cafespot.hotpage.HotViewModel
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView

class MainAdapter(val activity: MainActivity, val hotViewModel: HotViewModel):ListAdapter<MainItem, RecyclerView.ViewHolder>(MainDiffCallback()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_layout, parent, false)
        return MainViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is MainViewHolder -> {
                val mainItem = getItem(position) as MainItem
                holder.apply {
                    name.text = mainItem.title
                    description.text = mainItem.description
                }
                if (mainItem.title.equals("熱門推薦")){
                    try {
                        val gifDrawable = GifDrawable(holder.itemView.resources, R.drawable.fire)
                        holder.gif.setImageDrawable(gifDrawable)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    holder.cardview.setOnClickListener{
                        val context = holder.cardview.context
                        if (MainActivity.locationPermissionGranted) {
                            var bundleLocation:Bundle = Bundle()
                            hotViewModel.currentLocation.observe(activity){
                                val location = it
                                bundleLocation = Bundle().apply { putParcelable("location", location) }
                                println("mainAdapter_location:" + location.latitude)
                            }
                            context.startActivity(Intent(context, HotActivity::class.java).putExtras(bundleLocation))
                        } else {
                            getLocationPermission(context) //向用戶存取權限
                            getDeviceLocation(context)
                            Toast.makeText(context, "授權定位後才能使用此功能", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                if (mainItem.title.equals("地圖模式")){
                    try {
                        val gifDrawable = GifDrawable(holder.itemView.resources, R.drawable.googlemap)
                        holder.gif.setImageDrawable(gifDrawable)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    holder.cardview.setOnClickListener{
                        val context = holder.cardview.context
                        if (MainActivity.locationPermissionGranted) {
                            var bundleLocation:Bundle = Bundle()
                            hotViewModel.currentLocation.observe(activity){
                                val location = it
                                bundleLocation = Bundle().apply { putParcelable("location", location) }
                                println("mainAdapter_location:" + location.latitude)
                            }
                            context.startActivity(Intent(context, FavoriteActivity::class.java).putExtras(bundleLocation))
                        } else {
                            getLocationPermission(context) //向用戶存取權限
                            getDeviceLocation(context)
                            Toast.makeText(context, "授權定位後才能使用此功能", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                if (mainItem.title.equals("情境模式")){
                    try {
                        val gifDrawable = GifDrawable(holder.itemView.resources, R.drawable.mode)
                        holder.gif.setImageDrawable(gifDrawable)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    holder.cardview.setOnClickListener{
                        val context = holder.cardview.context
                        if (MainActivity.locationPermissionGranted) {
                            var bundleLocation:Bundle = Bundle()
                            hotViewModel.currentLocation.observe(activity){
                                val location = it
                                bundleLocation = Bundle().apply { putParcelable("location", location) }
                                println("mainAdapter_location:" + location.latitude)
                            }
                            context.startActivity(Intent(context, ModeActivity::class.java).putExtras(bundleLocation))
                        } else {
                            getLocationPermission(context) //向用戶存取權限
                            getDeviceLocation(context)
                            Toast.makeText(context, "授權定位後才能使用此功能", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    fun getLocationPermission(context: Context){
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            MainActivity.locationPermissionGranted = true
            println("getLocationPermission_granted")
        } else {
            println("getLocationPermission_requesting")
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MainActivity.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    fun getDeviceLocation(context: Context) {
        println("getLastKnownLocationA")
        try {
            if (MainActivity.locationPermissionGranted) {
                println("getLastKnownLocationB")
                activity.fusedLocationProviderClient.lastLocation.addOnCompleteListener(activity) {
                    if (it.isSuccessful) {
                        val location = it.result
                        if(location!=null) {
                            println("lat: " + location!!.latitude + " long: " + location!!.longitude)
                            activity.hotViewModel.setCurrentLocation(location!!)
                        } else {println("it.result=null")}
                    } else {
                        Log.e("CafeActivity", "Exception: %s", it.exception)
                    }
                }
            } else {
                println("no location permissionA")
                Toast.makeText(context, "no location permission", Toast.LENGTH_SHORT)
            }
        } catch (e:SecurityException){
            Log.e("Exception: %s", e.message, e)
        }
    }
}

class MainViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val name:TextView = view.findViewById(R.id.main_name_textview)
    val description:TextView = view.findViewById(R.id.main_description_textview)
    val cardview: CardView = view.findViewById(R.id.main_cardView)
    val gif:GifImageView = view.findViewById(R.id.main_GIF)
}

class MainDiffCallback: DiffUtil.ItemCallback<MainItem>(){
    override fun areItemsTheSame(oldItem: MainItem, newItem: MainItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: MainItem, newItem: MainItem): Boolean {
        return oldItem.title == newItem.title
    }
}