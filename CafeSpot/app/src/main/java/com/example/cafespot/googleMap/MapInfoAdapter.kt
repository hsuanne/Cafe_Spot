package com.example.cafespot.googleMap

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cafespot.FavoriteActivity
import com.example.cafespot.ImgItem
import com.example.cafespot.R
import com.example.cafespot.cafepage.CafeActivity
import com.example.cafespot.hotpage.*
import com.example.cafespot.mode.ModeActivity2
import com.makeramen.roundedimageview.RoundedImageView

class MapInfoAdapter(val activity: FavoriteActivity, val hotViewModel: HotViewModel) : ListAdapter<HotItem, RecyclerView.ViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.mapinfo_layout, parent, false)
        return MapInfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MapInfoViewHolder -> {
                val hotItem = getItem(position) as HotItem

                holder.apply {
                    if (hotItem.cafePicture.size>0) {
                        Glide.with(activity.applicationContext).load(hotItem.cafePicture[0])
                            .into(cafeImg)
                    }
                    cafeName.text = hotItem.cafeName
                    cafeRatingBar.rating = hotItem.averageScore
                    cafeRating.text = hotItem.averageScore.toString()
                }

                holder.cafeCardView.setOnClickListener {
                    //開新Activity
                    val context = holder.cafeCardView.context
                    var bundleLocation: Bundle = Bundle()
                    var cafeIdBundle = Bundle().apply { putInt("cafeId", hotItem.cafeId) }
                    hotViewModel.currentLocation.observe(activity){
                        val location = it
                        println("MapInfoAdapter:" + location.latitude + "," + location.longitude)
                        bundleLocation = Bundle().apply { putParcelable("location", location) }
                    }
                    println("MapInfoAdapter:" + hotItem.cafeId)
                    context.startActivity(
                        Intent(context, CafeActivity::class.java)
                        .putExtras(bundleLocation)
                        .putExtras(cafeIdBundle))
                }
            }
        }
    }
}

class MapInfoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val cafeImg:ImageView = view.findViewById(R.id.mapinfo_cafe_image)
    val cafeName:TextView = view.findViewById(R.id.mapinfo_cafe_name)
    val cafeRatingBar:RatingBar = view.findViewById(R.id.mapinfo_rating_bar)
    val cafeRating:TextView = view.findViewById(R.id.mapinfo_average_ranking)
    val cafeCardView:CardView = view.findViewById(R.id.mapinfo_cardview)
}