package com.example.cafespot.googleMap

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.cafespot.R
import com.example.cafespot.cafepage.Ranking

class FilterAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var filterList:List<Ranking> = listOf()
    var selectedList:MutableList<Int> = mutableListOf()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.filter_layout, parent, false)
        return FilterViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is FilterViewHolder -> {
                val filter = filterList[position]
                holder.filterTitle.text = filter.title
                holder.filterSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked){
                        selectedList.add(position+1)
                        println("isChecked:"+isChecked)
                    } else {
                        selectedList.remove(position+1)
                        println("isChecked:"+isChecked)
                    }
                }

                // 依照不同的title顯示不同icon
                when(filter.title){
//                    "咖啡好喝" -> holder.filterIcon.setImageResource(R.drawable.ic_baseline_emoji_food_beverage_24)
//                    "餐點好吃" -> holder.filterIcon.setImageResource(R.drawable.ic_baseline_restaurant_24)
//                    "安靜程度" -> holder.filterIcon.setImageResource(R.drawable.ic_baseline_record_voice_over_24)
//                    "裝潢美觀" -> holder.filterIcon.setImageResource(R.drawable.ic_baseline_house_siding_24)
//                    "價格便宜" -> holder.filterIcon.setImageResource(R.drawable.ic_baseline_music_note_24)
//                    "明亮程度" -> holder.filterIcon.setImageResource(R.drawable.ic_baseline_wb_incandescent_24)
//                    "wifi穩定" -> holder.filterIcon.setImageResource(R.drawable.ic_baseline_wifi_24)
//                    "插座夠多" -> holder.filterIcon.setImageResource(R.drawable.ic_baseline_settings_input_hdmi_24)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    fun refresh(filterList:List<Ranking>){
        this.filterList = filterList
        println("filterList:"+filterList.size)
        notifyDataSetChanged()
    }

    fun getSelectedL():MutableList<Int>{
        return selectedList
    }
}

class FilterViewHolder(view: View): RecyclerView.ViewHolder(view){
    val filterTitle: TextView = view.findViewById(R.id.custom_filter_title)
//    val filterIcon: ImageView = view.findViewById(R.id.custom_filter_icon)
    val filterSwitch:Switch = view.findViewById(R.id.filter_switch)
}