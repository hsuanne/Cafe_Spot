package com.example.cafespot.mode

import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.cafespot.FavoriteActivity
import com.example.cafespot.R
import com.example.cafespot.cafepage.Ranking
import com.example.cafespot.databinding.ActivityMode2Binding
import com.example.cafespot.hotpage.*

private var PAGE_INDEX = 0

class ModeActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityMode2Binding
    private lateinit var viewPager2: ViewPager2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMode2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val location = intent.extras?.getParcelable<Location>("location")!!
        val mode = intent.extras?.getString("mode")
        println("modeActivity2_location: " + location?.latitude)
        val hotViewModel = HotViewModel()
        hotViewModel.setCurrentLocation(location)

        val title = binding.mode2Title
        title.text = mode

        viewPager2 = binding.viewPagerModeSlider2

        //list of items, can get it from api
//        val hotItems:MutableList<HotItem> = mutableListOf()
//        val profilePic = R.drawable.profilepic
//        val comment = "Some Interesting Comments that I have problem showing"
        var rankingList: MutableList<Ranking> = mutableListOf()
        rankingList.add(Ranking("餐點好吃", 5f))
        rankingList.add(Ranking("咖啡好喝", 4f))
        rankingList.add(Ranking("安靜程度", 3.5f))
        rankingList.add(Ranking("裝潢美觀", 2f))
        rankingList.add(Ranking("價格便宜", 1.5f))
        rankingList.add(Ranking("明亮程度", 2f))
        rankingList.add(Ranking("wifi穩定", 3f))
        rankingList.add(Ranking("插座夠多", 4.5f))

        when (mode) {
            "讀書" -> hotViewModel.fetchHotItemList(
                "study",
                PAGE_INDEX,
                location.latitude,
                location.longitude
            )
            "工作" -> hotViewModel.fetchHotItemList(
                "work",
                PAGE_INDEX,
                location.latitude,
                location.longitude
            )
            "開會" -> hotViewModel.fetchHotItemList(
                "meeting",
                PAGE_INDEX,
                location.latitude,
                location.longitude
            )
            "寵物友善" -> hotViewModel.fetchHotItemList(
                "petFriendly",
                PAGE_INDEX,
                location.latitude,
                location.longitude
            )
            "深夜咖啡" -> hotViewModel.fetchHotItemList(
                "midnightCafe",
                PAGE_INDEX,
                location.latitude,
                location.longitude
            )
            "放鬆" -> hotViewModel.fetchHotItemList(
                "relax",
                PAGE_INDEX,
                location.latitude,
                location.longitude
            )
        }

        val adapter = ModeAdapter2(this, hotViewModel)
        viewPager2.adapter = adapter


        val refreshButton = binding.modeRefreshButton
        val customButton = binding.modeCustomButton

        var hotItemList = listOf<HotItem>()
        hotViewModel.HotItemList.observe(this) {
            if (it.size != 0) {
                hotItemList = it
                println("ModeActivity:" + it[0].cafeName)
//                region.text = it[0].regionName
                adapter.submitList(it)
            } else {
                Toast.makeText(this, "資料已到底部", Toast.LENGTH_SHORT).show()
                // reset page index
                PAGE_INDEX = 0
//                var bundleLocation = Bundle().apply { putParcelable("location", location) }
//                var bundleMode = Bundle().apply { putString("mode", mode) }
//                startActivity(
//                    Intent(this, ModeActivity2::class.java)
//                        .putExtras(bundleLocation)
//                        .putExtras(bundleMode)
//                )
//                finish()
                customButton.visibility = View.VISIBLE
            }
        }

        hotViewModel.cafePicList.observe(this) {
            val mainBackground = binding.mode2Bg
            // 根據page position換背景
            if (it.isNotEmpty()) {
                viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        when (position) {
                            0 -> {
                                if (it.size >= 1) {
                                    if (it[0].isNotEmpty()) {
                                        Glide.with(this@ModeActivity2)
                                            .load(it[0])
                                            .into(mainBackground)
                                    } else {
                                        mainBackground.setImageResource(R.drawable.email_login_bg)
                                    }
                                } else {
                                    mainBackground.setImageResource(R.drawable.email_login_bg)
                                }
                            }
                            1 -> {
                                if (it.size >= 2) {
                                    if (it[1].isNotEmpty()) {
                                        Glide.with(this@ModeActivity2)
                                            .load(it[1])
                                            .into(mainBackground)
                                    } else {
                                        mainBackground.setImageResource(R.drawable.email_login_bg)
                                    }
                                } else {
                                    mainBackground.setImageResource(R.drawable.email_login_bg)
                                }
                            }
                            2 -> {
                                if (it.size >= 3) {
                                    if (it[2].isNotEmpty()) {
                                        Glide.with(this@ModeActivity2).load(it[2])
                                            .into(mainBackground)
                                    } else {
                                        mainBackground.setImageResource(R.drawable.email_login_bg)
                                    }
                                } else {
                                    mainBackground.setImageResource(R.drawable.email_login_bg)
                                }
                            }
                            3 -> {
                                if (it.size >= 4) {
                                    if (it[3].isNotEmpty()) {
                                        Glide.with(this@ModeActivity2).load(it[3])
                                            .into(mainBackground)
                                    } else {
                                        mainBackground.setImageResource(R.drawable.email_login_bg)
                                    }
                                } else {
                                    mainBackground.setImageResource(R.drawable.email_login_bg)
                                }
                            }
                            4 -> {
                                if (it.size >= 5) {
                                    if (it[4].isNotEmpty()) {
                                        Glide.with(this@ModeActivity2).load(it[4])
                                            .into(mainBackground)
                                    } else {
                                        mainBackground.setImageResource(R.drawable.email_login_bg)
                                    }
                                } else {
                                    mainBackground.setImageResource(R.drawable.email_login_bg)
                                }
                            }
                        }

                        if (position == (hotItemList.size - 1)) {
                            if (hotItemList.size < 5) {
                                Toast.makeText(this@ModeActivity2, "附近已無符合的咖啡廳", Toast.LENGTH_SHORT)
                                    .show()
                                customButton.translationY = 300f
                                customButton.alpha = 0.1f
                                customButton.animate().translationY(0f).alpha(1f)
                                    .setDuration(800).setStartDelay(2000).start()
                                customButton.visibility = View.VISIBLE
                            } else {
                                val toast =
                                    Toast.makeText(
                                        this@ModeActivity2,
                                        "沒有找到喜歡的嗎~?",
                                        Toast.LENGTH_SHORT
                                    )
                                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                                toast.show()
                                refreshButton.translationY = 300f
                                refreshButton.alpha = 0.1f
                                refreshButton.animate().translationY(0f).alpha(1f)
                                    .setDuration(800).setStartDelay(2000).start()
                                refreshButton.visibility = View.VISIBLE
                            }
                        }
                        super.onPageSelected(position)
                    }
                })
            }
        }

        refreshButton.setOnClickListener {
            PAGE_INDEX += 1
            refreshButton.visibility = View.INVISIBLE
            var bundleLocation = Bundle().apply { putParcelable("location", location) }
            var bundleMode = Bundle().apply { putString("mode", mode) }
            startActivity(
                Intent(this, ModeActivity2::class.java)
                    .putExtras(bundleLocation)
                    .putExtras(bundleMode)
            )
            finish()
        }

        customButton.setOnClickListener {
            PAGE_INDEX = 0
            var bundleLocation = Bundle().apply { putParcelable("location", location) }
            startActivity(
                Intent(this, FavoriteActivity::class.java)
                    .putExtras(bundleLocation)
            )
            finish()
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
    override fun onBackPressed() {
        PAGE_INDEX = 0
        super.onBackPressed()
    }
}