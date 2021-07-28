package com.example.cafespot.mode

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cafespot.R
import com.example.cafespot.hotpage.HotViewModel
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView

class ModeAdapter(val activity: ModeActivity, val hotViewModel: HotViewModel): ListAdapter<ModeItem, RecyclerView.ViewHolder>(ModeDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mode_layout, parent, false)
        return ModeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is ModeViewHolder -> {
                val modeItem = getItem(position)
                holder.apply {
                    title.text = modeItem.title
                    description.text = modeItem.description
                }
                if (modeItem.title.equals("讀書")){
                    try {
                        val gifDrawable = GifDrawable(holder.itemView.resources, R.drawable.book)
                        holder.gif.setImageDrawable(gifDrawable)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                if (modeItem.title.equals("工作")){
                    try {
                        val gifDrawable = GifDrawable(holder.itemView.resources, R.drawable.work)
                        holder.gif.setImageDrawable(gifDrawable)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                if (modeItem.title.equals("開會")){
                    try {
                        val gifDrawable = GifDrawable(holder.itemView.resources, R.drawable.meeting)
                        holder.gif.setImageDrawable(gifDrawable)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                if (modeItem.title.equals("寵物友善")){
                    try {
                        val gifDrawable = GifDrawable(holder.itemView.resources, R.drawable.dog)
                        holder.gif.setImageDrawable(gifDrawable)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                if (modeItem.title.equals("深夜咖啡")){
                    try {
                        val gifDrawable = GifDrawable(holder.itemView.resources, R.drawable.night_sky)
                        holder.gif.setImageDrawable(gifDrawable)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                if (modeItem.title.equals("放鬆")){
                    try {
                        val gifDrawable = GifDrawable(holder.itemView.resources, R.drawable.relax)
                        holder.gif.setImageDrawable(gifDrawable)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                holder.cardview.setOnClickListener {
                    val context = holder.cardview.context
                    var bundleLocation: Bundle = Bundle()
                    hotViewModel.currentLocation.observe(activity){
                        val location = it
                        bundleLocation = Bundle().apply { putParcelable("location", location) }
                        println("sliderAdapter")
                    }
                    context.startActivity(
                        Intent(context, ModeActivity2::class.java)
                            .putExtra("mode",modeItem.title)
                            .putExtras(bundleLocation))
                }
            }
        }
    }
}

class ModeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val title:TextView = view.findViewById(R.id.mode_name_textview)
    val description:TextView = view.findViewById(R.id.mode_description_textview)
    val cardview: CardView = view.findViewById(R.id.mode_cardview)
    val gif:GifImageView = view.findViewById(R.id.mode_GIF)
}

class ModeDiffCallback: DiffUtil.ItemCallback<ModeItem>(){
    override fun areItemsTheSame(oldItem: ModeItem, newItem: ModeItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ModeItem, newItem: ModeItem): Boolean {
        return oldItem.title== newItem.title
    }
}