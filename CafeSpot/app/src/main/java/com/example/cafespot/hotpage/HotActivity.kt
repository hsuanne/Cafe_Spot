package com.example.cafespot.hotpage

import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.cafespot.FavoriteActivity
import com.example.cafespot.R
import com.example.cafespot.cafepage.Comment
import com.example.cafespot.cafepage.Ranking
import com.example.cafespot.databinding.ActivityHotBinding
import com.example.cafespot.main.rand

private var PAGE_INDEX = 0

class HotActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHotBinding
    private lateinit var viewPager2: ViewPager2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val location = intent.extras?.getParcelable<Location>("location")!!
        println("hotActivity_location: " + location?.latitude + " ; " + location.longitude)
//        val hotViewModel = HotViewModel()
        val hotViewModel = ViewModelProvider(this).get(HotViewModel::class.java)
        hotViewModel.setCurrentLocation(location)

        viewPager2 = binding.viewPagerImageSlider

        hotViewModel.fetchHotItemList("hot", PAGE_INDEX, location.latitude, location.longitude)


        val hotItems:MutableList<HotItem> = mutableListOf()
        val profilePic = R.drawable.profilepic
        val comment = "Some Interesting Comments that I have problem showing"
        var rankingList:MutableList<Ranking> = mutableListOf(
            Ranking("餐點好吃", 5f),
            Ranking("咖啡好喝", 4f),
            Ranking("安靜程度", 3.5f),
            Ranking("價格便宜", 1.5f),
            Ranking("明亮程度", 2f),
            Ranking("wifi穩定", 3f),
            Ranking("插座夠多", 4.5f)
        )


        val adapter = SliderAdapter(this, hotViewModel)
        viewPager2.adapter = adapter
//        hotViewModel.HotItemList.value = hotItem

        val region:TextView = binding.locationTextview
        val refreshButton = binding.refreshButton
        val customButton = binding.hotCustomButton

        var hotItemList = listOf<HotItem>()
        hotViewModel.HotItemList.observe(this){
            if (it.size!=0) {
                hotItemList = it
                println("HotActivity:" + it[0].cafeName)
                region.text = it[0].regionName
                adapter.submitList(it)
            } else {
                Toast.makeText(this, "資料已到底部", Toast.LENGTH_SHORT).show()
                // reset page index
                PAGE_INDEX = 0
                var bundleLocation = Bundle().apply { putParcelable("location", location) }
                startActivity(Intent(this, HotActivity::class.java)
                    .putExtras(bundleLocation))
                finish()
            }
        }

        hotViewModel.cafePicList.observe(this) {
            val mainBackground = binding.hotBg
            // 根據page position換背景
            var cb = object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    when (position) {
                        0 -> {
                            if (it.size>=1) {
                                if (it[0].isNotEmpty()) {
                                    Glide.with(this@HotActivity)
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
                            if (it.size>=2) {
                                if (it[1].isNotEmpty()) {
                                    Glide.with(this@HotActivity)
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
                            if (it.size>=3) {
                                if (it[2].isNotEmpty()) {
                                    Glide.with(this@HotActivity).load(it[2])
                                        .into(mainBackground)
                                } else {
                                    mainBackground.setImageResource(R.drawable.email_login_bg)
                                }
                            } else {
                                mainBackground.setImageResource(R.drawable.email_login_bg)
                            }
                        }
                        3 -> {
                            if (it.size>=4) {
                                if (it[3].isNotEmpty()) {
                                    Glide.with(this@HotActivity).load(it[3])
                                        .into(mainBackground)
                                } else {
                                    mainBackground.setImageResource(R.drawable.email_login_bg)
                                }
                            } else {
                                mainBackground.setImageResource(R.drawable.email_login_bg)
                            }
                        }
                        4 -> {
                            if (it.size>=5) {
                                if (it[4].isNotEmpty()) {
                                    Glide.with(this@HotActivity).load(it[4])
                                        .into(mainBackground)
                                } else {
                                    mainBackground.setImageResource(R.drawable.email_login_bg)
                                }
                            }  else {
                                mainBackground.setImageResource(R.drawable.email_login_bg)
                            }
                        }
                    }

                    refreshButton.visibility = View.INVISIBLE
                    customButton.visibility = View.INVISIBLE
                    if (position == (hotItemList.size-1)){
                        if (hotItemList.size < 5) {
                            Toast.makeText(this@HotActivity, "附近已無符合的咖啡廳", Toast.LENGTH_SHORT)
                                .show()
                            customButton.translationY = 300f
                            customButton.alpha = 0.1f
                            customButton.animate().translationY(0f).alpha(1f)
                                .setDuration(800).setStartDelay(2000).start()
                            customButton.visibility = View.VISIBLE
                        } else {
                            val toast =
                                Toast.makeText(this@HotActivity, "沒有找到喜歡的嗎~?", Toast.LENGTH_SHORT)
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
            }
            if (it.isNotEmpty()) {
                viewPager2.registerOnPageChangeCallback(cb)
            } else {
                mainBackground.setImageResource(R.drawable.email_login_bg)
            }
        }

        refreshButton.setOnClickListener {
            PAGE_INDEX += 1
            refreshButton.visibility = View.INVISIBLE
//            hotViewModel.fetchHotItemList("hot", PAGE_INDEX, location.latitude, location.longitude)
            var bundleLocation = Bundle().apply { putParcelable("location", location) }
            startActivity(Intent(this, HotActivity::class.java)
                .putExtras(bundleLocation))
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

        val compositePageTransformer:CompositePageTransformer = CompositePageTransformer()
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

class CustomPageTransformer:ViewPager2.PageTransformer{
    override fun transformPage(page: View, position: Float) {
        val r:Float = 1 - Math.abs(position)
        page.scaleY = 0.85f + r*0.15f
        page.alpha = 0.25f + r
    }
}

class HotData(
    var cafeId:Int,
    var image: Int,
    var cafeName:String?,
    var commentList: MutableList<Comment>
)