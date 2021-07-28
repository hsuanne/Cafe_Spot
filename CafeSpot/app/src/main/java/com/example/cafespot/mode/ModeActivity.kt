package com.example.cafespot.mode

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.cafespot.databinding.ActivityModeBinding
import com.example.cafespot.hotpage.CustomPageTransformer
import com.example.cafespot.hotpage.HotViewModel
import com.example.cafespot.main.rand

class ModeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityModeBinding
    private lateinit var viewPager2: ViewPager2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val location = intent.extras?.getParcelable<Location>("location")!!
        println("modeActivity_location: " + location?.latitude)
        val hotViewModel = HotViewModel()
        hotViewModel.setCurrentLocation(location)

        viewPager2 = binding.viewPagerModeSlider

        //list of items, can get it from api
        val modeItems:MutableList<ModeItem> = mutableListOf()
        modeItems.add(ModeItem("讀書","愜意享受悠閒時光"))
        modeItems.add(ModeItem("工作","專心處理事務"))
        modeItems.add(ModeItem("開會","多人洽談商務"))
        modeItems.add(ModeItem("寵物友善","安心帶上您的毛小孩"))
        modeItems.add(ModeItem("深夜咖啡","夜深人靜、品杯咖啡"))
        modeItems.add(ModeItem("放鬆","療癒身心靈之旅"))

        val adapter = ModeAdapter(this, hotViewModel)
        viewPager2.adapter = adapter
        adapter.submitList(modeItems)

        hotViewModel.currentLocation.observe(this) {
            hotViewModel.fetchHotItemList("around", 0, location.latitude, location.longitude)
        }
        hotViewModel.cafePicList.observe(this) {
            var cleanList = it.toMutableList()

            if (cleanList.isNotEmpty()) {
                var iterator = cleanList.iterator()
                while (iterator.hasNext()){
                    var str = iterator.next()
                    if (str == ""){
                        iterator.remove()
                    }
                }
                // 根據page position換背景
                val mainBackground = binding.modeBg

                    viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        when (position) {
                            0 -> {
                                var num = rand(0, cleanList.size-1)
                                Glide.with(this@ModeActivity)
                                    .load(cleanList[num])
                                    .into(mainBackground)
                            }
                            1 -> {
                                var num = rand(0, cleanList.size-1)
                                Glide.with(this@ModeActivity)
                                    .load(cleanList[num])
                                    .into(mainBackground)
                                binding.modeRoot.setBackgroundResource(0)
                            }
                            2 -> {
                                var num = rand(0, cleanList.size-1)
                                Glide.with(this@ModeActivity).load(cleanList[num])
                                    .into(mainBackground)
                            }
                            3 -> {
                                var num = rand(0, cleanList.size-1)
                                Glide.with(this@ModeActivity).load(cleanList[num])
                                    .into(mainBackground)
                            }
                            4 -> {
                                var num = rand(0, cleanList.size-1)
                                Glide.with(this@ModeActivity).load(cleanList[num])
                                    .into(mainBackground)
                            }
                            5 -> {
                                var num = rand(0, cleanList.size-1)
                                Glide.with(this@ModeActivity).load(cleanList[num])
                                    .into(mainBackground)
                            }
                        }
                        super.onPageSelected(position)
                    }
                })
            }
        }

        viewPager2.apply {
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 3
            getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }

        val compositePageTransformer: CompositePageTransformer = CompositePageTransformer()
        compositePageTransformer.apply {
            addTransformer(MarginPageTransformer(40)) //圖跟圖之間的間距
            addTransformer(CustomPageTransformer())
        }
        viewPager2.setPageTransformer(compositePageTransformer)
    }
}