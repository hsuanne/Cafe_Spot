package com.example.cafespot.mode

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cafespot.R
import com.example.cafespot.cafepage.CafeActivity
import com.example.cafespot.hotpage.DiffCallback
import com.example.cafespot.hotpage.HotItem
import com.example.cafespot.hotpage.HotViewModel

class ModeAdapter2(val activity: ModeActivity2, val hotViewModel: HotViewModel) :
    ListAdapter<HotItem, RecyclerView.ViewHolder>(
        DiffCallback()
    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mode2_layout, parent, false)
        return ModeViewHolder2(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ModeViewHolder2 -> {
                val hotItem = getItem(position) as HotItem
                holder.apply {
                    if(hotItem.cafePicture.size!=0) {
                        Glide.with(activity.applicationContext).load(hotItem.cafePicture[0])
                            .into(imageView)
                    }
                    cafeName.text = hotItem.cafeName
                    if (hotItem.cafeDiscuss.size == 1) {
                        username1.text = hotItem.cafeDiscuss[0].userName
                        Glide.with(activity.applicationContext).load(hotItem.cafeDiscuss[0].userPicture).into(profilePic1)
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
                holder.cardview.setOnClickListener {
                    //用viewModel observe current cafeshop
//                    hotViewModel.setCurrentHotItem(hotItem)
//                    println("hotviewmodel:" + (hotViewModel.currentHotItem.value as HotItem).shop)
                    //開新Activity
                    val context = holder.cardview.context
//                    val bundle = Bundle().apply { putParcelable("hot", hotItem) }
                    var bundleLocation: Bundle = Bundle()
                    var cafeIdBundle = Bundle().apply { putInt("cafeId", hotItem.cafeId) }
                    hotViewModel.currentLocation.observe(activity) {
                        val location = it
                        bundleLocation = Bundle().apply { putParcelable("location", location) }
                        println("sliderAdapter")
                    }
                    context.startActivity(
                        Intent(context, CafeActivity::class.java)
                            .putExtras(bundleLocation)
                            .putExtras(cafeIdBundle)
                    )
                }
            }
        }
    }
}

class ModeViewHolder2(view: View) : RecyclerView.ViewHolder(view) {
    val imageView: ImageView = view.findViewById(R.id.mode2_cafespot_image)
    val cafeName: TextView = view.findViewById(R.id.mode2_cafe_name_textview)
    val username1:TextView = view.findViewById(R.id.mode2_user1)
    val profilePic1: ImageView = view.findViewById(R.id.mode2_profile_image1)
    val comment1: TextView = view.findViewById(R.id.mode2_comment_textview1)
    val username2:TextView = view.findViewById(R.id.mode2_user2)
    val profilePic2: ImageView = view.findViewById(R.id.mode2_profile_image2)
    val comment2: TextView = view.findViewById(R.id.mode2_comment_textview2)
    val username3:TextView = view.findViewById(R.id.mode2_user3)
    val profilePic3: ImageView = view.findViewById(R.id.mode2_profile_image3)
    val comment3: TextView = view.findViewById(R.id.mode2_comment_textview3)
    val cardview: CardView = view.findViewById(R.id.mode2_cardview)
}
