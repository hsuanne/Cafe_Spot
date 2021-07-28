package com.example.cafespot.cafepage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cafespot.R
import com.example.cafespot.hotpage.DiffCallback
import com.example.cafespot.hotpage.HotItem

class CafeImgAdapter: ListAdapter<String, RecyclerView.ViewHolder>(
    StrDiffCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cafe_img_container, parent, false)
        return CafeImgViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is CafeImgViewHolder -> {
                val str = getItem(position)
                if (itemCount > 0){
                    Glide.with(holder.itemView.context).load(str).into(holder.imgView)
                } else {
                    holder.imgView.setImageResource(R.drawable.image_placeholder)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        if (super.getItemCount()>5){
            return 5
        } else {
            return super.getItemCount()
        }
    }
}

class StrDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}

class CafeImgViewHolder(view: View):RecyclerView.ViewHolder(view){
    val imgView: ImageView = view.findViewById(R.id.cafe_img_view)
}