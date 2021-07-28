package com.example.cafespot.cafepage

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cafespot.R
import org.w3c.dom.Text

class RankingAdapter:RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var rankingList:List<Ranking> = listOf()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.ranking_layout, parent, false)
        return RankViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is RankViewHolder -> {
                val ranking = rankingList[position]
                holder.rankingTitle.text = ranking.title
                holder.rankingBar.rating = ranking.star

                // 依照不同的title顯示不同icon
                when(ranking.title){
                    "咖啡好喝" -> holder.rankingIcon.setImageResource(R.drawable.ic_baseline_emoji_food_beverage_24)
                    "餐點好吃" -> holder.rankingIcon.setImageResource(R.drawable.ic_baseline_restaurant_24)
                    "安靜程度" -> holder.rankingIcon.setImageResource(R.drawable.ic_baseline_record_voice_over_24)
                    "裝潢美觀" -> holder.rankingIcon.setImageResource(R.drawable.ic_baseline_house_siding_24)
                    "價格便宜" -> holder.rankingIcon.setImageResource(R.drawable.ic_baseline_music_note_24)
                    "明亮程度" -> holder.rankingIcon.setImageResource(R.drawable.ic_baseline_wb_incandescent_24)
                    "wifi穩定" -> holder.rankingIcon.setImageResource(R.drawable.ic_baseline_wifi_24)
                    "插座夠多" -> holder.rankingIcon.setImageResource(R.drawable.ic_baseline_settings_input_hdmi_24)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return rankingList.size
    }

    fun refresh(rankingList:List<Ranking>){
        this.rankingList = rankingList
        println("rankingList:"+rankingList.size)
        notifyDataSetChanged()
    }
}

class RankViewHolder(view: View):RecyclerView.ViewHolder(view){
    val rankingTitle:TextView = view.findViewById(R.id.ranking_title)
    val rankingBar:RatingBar = view.findViewById(R.id.rating_bar)
    val rankingIcon:ImageView = view.findViewById(R.id.ranking_icon)
}