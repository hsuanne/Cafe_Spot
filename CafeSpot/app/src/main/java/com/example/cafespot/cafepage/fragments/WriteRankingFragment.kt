package com.example.cafespot.cafepage.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.cafespot.R
import com.example.cafespot.USER_ID
import com.example.cafespot.cafepage.Comment
import com.example.cafespot.cafepage.Ranking
import com.example.cafespot.cafepage.RankingAdapter
import com.example.cafespot.cafepage.WriteRankingAdapter
import com.example.cafespot.hotpage.HotViewModel
import kotlin.math.roundToInt

/**
 * A simple [Fragment] subclass.
 * Use the [WriteRankingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WriteRankingFragment : Fragment() {
    var writeRankingList = mutableListOf<Ranking>(
        Ranking("wifi穩定", 0f),
        Ranking("插座夠多", 0f),
        Ranking("安靜程度", 0f),
        Ranking("咖啡好喝", 0f),
        Ranking("餐點好吃", 0f),
        Ranking("價格便宜", 0f),
        Ranking("裝潢美觀", 0f),
        Ranking("明亮程度", 0f),
        Ranking("音樂好聽", 0f)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_write_ranking, container, false)
        val publish = view.findViewById<Button>(R.id.post_ranking_button)
        val cancel = view.findViewById<Button>(R.id.cancel_ranking_button)
        val viewModel = ViewModelProvider(requireActivity()).get(HotViewModel::class.java)

        val recyclerViewRanking: RecyclerView = view.findViewById(R.id.recyclerView_write_ranking)
        val adapter = WriteRankingAdapter()
        recyclerViewRanking.adapter = adapter
        adapter.refresh(writeRankingList)

        publish.setOnClickListener {
            //更新店家評分給後端
            val rankingResult = adapter.getWriteRankingList()

            if (isRatingNull(rankingResult)) {
                Toast.makeText(context, "評分不能為 0 !", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // create ratingPost
            val cafeId = viewModel.currentHotItem.value?.cafeId!!
            val ratingPost = rankingListToPost(cafeId, rankingResult)

            // post to back-end
            val body = viewModel.RatingToJson(ratingPost)
            viewModel.postRating(body)
        }

        viewModel.postRatingCode.observe(viewLifecycleOwner) {
            when (it) {
                1 -> {
                    // go back to rating Fragment
                    val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragment_blank2_layout, RankingFragment())
                    transaction.commit()
                }
                2 -> {
                    Toast.makeText(requireContext(), "輸入錯誤", Toast.LENGTH_SHORT).show()
                }
                3 -> {
                    Toast.makeText(requireContext(), "您已評分過此店!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        cancel.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_blank2_layout, RankingFragment())
            transaction.commit()
        }
        return view
    }

    private fun isRatingNull(rankingList: List<Ranking>): Boolean {
        for (ranking in rankingList) {
            if (ranking.star == 0f) {
                return true
            }
        }
        return false
    }

    private fun rankingListToPost(cafeId: Int, rankingList: List<Ranking>): RatingPost {
        return RatingPost(
            cafeId, USER_ID,
            rankingList[0].star.toInt(),
            rankingList[1].star.toInt(),
            rankingList[2].star.toInt(),
            rankingList[3].star.toInt(),
            rankingList[4].star.toInt(),
            rankingList[5].star.toInt(),
            rankingList[6].star.toInt(),
            rankingList[7].star.toInt(),
            rankingList[8].star.toInt()
        )
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WriteRankingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WriteRankingFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}

class RatingPost(
    var cafeId: Int,
    var userId: Int,
    var wifiStablilty: Int,
    var plugCountDegree: Int,
    var quietDegree: Int,
    var coffeeDegree: Int,
    var foodDegree: Int,
    var priceDegree: Int,
    var decorateDegree: Int,
    var brightness: Int,
    var musicDegree: Int
)