package com.example.cafespot.cafepage


import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.cafespot.R
import com.example.cafespot.databinding.ActivityCafeBinding
import com.example.cafespot.hotpage.HotItem
import com.example.cafespot.hotpage.HotViewModel
import com.google.android.material.tabs.TabLayoutMediator

class CafeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCafeBinding
    private lateinit var hotViewModel: HotViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCafeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // make new view holder object
        val hotViewModel = ViewModelProvider(this).get(HotViewModel::class.java)
//        hotViewModel = HotViewModel()

        // 拆Bundle
        val intent = intent
//        val currentHotItem = intent.extras?.getParcelable<HotItem>("hot")!!
        val currentLocation = intent.extras?.getParcelable<Location>("location")!!
        val cafeId = intent.extras?.getInt("cafeId")
//        val cafeImg = binding.cafeImageview
        var shopLat: Double = 0.0
        var shopLong: Double = 0.0

        // cafeImg ViewPager2
        val cafeImgViewPager2: ViewPager2 = binding.cafeImgViewpager2
        val imgAdapter = CafeImgAdapter()
        cafeImgViewPager2.adapter = imgAdapter
//        val dotsLayout: LinearLayout = findViewById(R.id.dots_container)
//        val dots = listOf<TextView>()
//        for (i in 0 .. 4) {
//            dots[i].setText(Html.fromHtml("&#9679"))
//            dots[i].setTextSize(18F)
//            dotsLayout.addView(dots[i])
//        }

        val indicator = binding.indicator
        indicator.setViewPager(cafeImgViewPager2)

//        cafeImgViewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
//            override fun onPageSelected(position: Int) {
//                for (i in 0 .. 4){
//                    if (i==position){
//                        dots[i].setTextColor(ContextCompat.getColor(this@CafeActivity,R.color.brown_500))
//                    } else {
//                        dots[i].setTextColor(ContextCompat.getColor(this@CafeActivity,R.color.grey))
//                    }
//                }
//                super.onPageSelected(position)
//            }
//        })

            // fetch json
            if (cafeId != null) {
                print("CafeActivity:" + cafeId)
                hotViewModel.fetchCafeInfo(cafeId)
            } else {
                Toast.makeText(this, "cafeId is null!", Toast.LENGTH_SHORT).show()
            }
            hotViewModel.currentHotItem.observe(this) {
                println("CafeActivity_currentHotItem")
                shopLat = it.latitude
                shopLong = it.longitude
                println(it.cafeName)
                imgAdapter.submitList(it.cafePicture)
                if(it.cafePicture.size>5) {
                    indicator.createIndicators(5, 0)
                } else {
                    indicator.createIndicators(it.cafePicture.size, 0)
                }
            }
//        hotViewModel.setCurrentHotItem(currentHotItem)
            hotViewModel.setCurrentLocation(currentLocation)
            hotViewModel.currentLocation.observe(this) {
                println("CafeActivity:" + it.latitude)
            }

            hotViewModel.currentShopLocation.observe(this) {
                println("CafeActivity_currentShopLocation:" + it[0])
            }

            val tablayout = binding.cafeTablayout
            val viewPager2 = binding.cafeViewpager2

            val adapter = CafeAdapter(
                supportFragmentManager, lifecycle, currentLocation
            )
//        val adapter = CafeAdapter(this, currentHotItem, hotViewModel)

            viewPager2.adapter = adapter
            viewPager2.isUserInputEnabled = false


            TabLayoutMediator(tablayout, viewPager2) { tab, position ->
                when (position) {
                    0 -> {
                        tab.text = "店家資訊"
                        tab.setIcon(R.drawable.ic_baseline_local_cafe_24)
                    }
                    1 -> {
                        tab.text = "查看評分"
                        tab.setIcon(R.drawable.ic_baseline_star_24)
                    }
                    2 -> {
                        tab.text = "查看評論"
                        tab.setIcon(R.drawable.ic_baseline_comment_24)
                    }
                }
            }.attach()

            // 拆Bundle
//        val intent = intent
//        val currentHotItem = intent.extras?.getParcelable<HotItem>("hot")
//        val cafeImg = binding.cafeImageview
//        if (currentHotItem != null) {
//            println("CafeActivity:" + currentHotItem.shop)
//            cafeImg.setImageResource(currentHotItem.image)
////            hotViewModel.setCurrentHotItem(currentHotItem)
//        }


//        val nameObserver = Observer<HotItem> { hotItem ->
//            cafeImg.setImageResource(hotItem.image)
//        }
//
//        hotViewModel.currentHotItem.observe(this, nameObserver)

            //若有需要在這裡切換前後店家就可能會用到
//        hotViewModel.currentHotItem.observe(this){ currentHotItem ->
//            println("hotviewmodel:" + currentHotItem.shop)
//            if (currentHotItem!=null) {
//                cafeImg.setImageResource(currentHotItem.image)
//            }
//        }

    }
}