package com.example.cafespot.hotpage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.cafespot.R
import com.example.cafespot.cafepage.CafeActivity

class SliderAdapter(val activity: HotActivity,val hotViewModel: HotViewModel) : ListAdapter<HotItem,RecyclerView.ViewHolder>(
    DiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.hot_layout, parent, false)
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SliderViewHolder -> {
                val hotItem = getItem(position) as HotItem

                holder.apply {
                    if(hotItem.cafePicture.size!=0) {
                        Glide.with(activity.applicationContext).load(hotItem.cafePicture[0]).into(imageView)
                    }
                    cafeName.text = hotItem.cafeName
                    if (hotItem.cafeDiscuss.size == 1){
                        Glide.with(activity.applicationContext).load(hotItem.cafeDiscuss[0].userPicture).into(profilePic1)
                        username1.text = hotItem.cafeDiscuss[0].userName
                        comment1.text = hotItem.cafeDiscuss[0].discussContent
                    }

                    if (hotItem.cafeDiscuss.size == 2) {
                        username1.text = hotItem.cafeDiscuss[0].userName
                        Glide.with(activity.applicationContext).load(hotItem.cafeDiscuss[0].userPicture).into(profilePic1)
                        comment1.text = hotItem.cafeDiscuss[0].discussContent
                        username2.text = hotItem.cafeDiscuss[1].userName
                        Glide.with(activity.applicationContext).load(hotItem.cafeDiscuss[1].userPicture).into(profilePic2)
                        comment2.text = hotItem.cafeDiscuss[1].discussContent
                    }
                    if (hotItem.cafeDiscuss.size >= 3) {
                        username1.text = hotItem.cafeDiscuss[0].userName
                        Glide.with(activity.applicationContext).load(hotItem.cafeDiscuss[0].userPicture).into(profilePic1)
                        comment1.text = hotItem.cafeDiscuss[0].discussContent
                        username2.text = hotItem.cafeDiscuss[1].userName
                        Glide.with(activity.applicationContext).load(hotItem.cafeDiscuss[1].userPicture).into(profilePic2)
                        comment2.text = hotItem.cafeDiscuss[1].discussContent
                        username3.text = hotItem.cafeDiscuss[2].userName
                        Glide.with(activity.applicationContext).load(hotItem.cafeDiscuss[2].userPicture).into(profilePic3)
                        comment3.text = hotItem.cafeDiscuss[2].discussContent
                    }
                }
                holder.cardview.setOnClickListener{
                    //開新Activity
                    val context = holder.cardview.context
//                    val bundle = Bundle().apply { putParcelable("hot", hotItem) }
                    var bundleLocation:Bundle = Bundle()
                    var cafeIdBundle = Bundle().apply { putInt("cafeId", hotItem.cafeId) }
                    hotViewModel.currentLocation.observe(activity){
                        val location = it
                        bundleLocation = Bundle().apply { putParcelable("location", location) }
                    }
                    println("sliderAdapter:" + hotItem.cafeId)
                    context.startActivity(Intent(context, CafeActivity::class.java)
                        .putExtras(bundleLocation)
                        .putExtras(cafeIdBundle))
                }
            }
        }
    }
}

class SliderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val imageView:ImageView = view.findViewById(R.id.cafespot_image)
    val cafeName:TextView = view.findViewById(R.id.cafe_name_textview)
    val profilePic1:ImageView = view.findViewById(R.id.profile_image1)
    val username1:TextView = view.findViewById(R.id.hot_user1)
    val comment1:TextView = view.findViewById(R.id.comment_textview1)
    val profilePic2:ImageView = view.findViewById(R.id.profile_image2)
    val username2:TextView = view.findViewById(R.id.hot_user2)
    val comment2:TextView = view.findViewById(R.id.comment_textview2)
    val profilePic3:ImageView = view.findViewById(R.id.profile_image3)
    val username3:TextView = view.findViewById(R.id.hot_user3)
    val comment3:TextView = view.findViewById(R.id.comment_textview3)
    val cardview:CardView = view.findViewById(R.id.hot_cardview)
}

class DiffCallback:DiffUtil.ItemCallback<HotItem>(){
    override fun areItemsTheSame(oldItem: HotItem, newItem: HotItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: HotItem, newItem: HotItem): Boolean {
        return oldItem.cafeName== newItem.cafeName
    }
}