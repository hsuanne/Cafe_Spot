package com.example.cafespot.cafepage.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.cafespot.R
import com.example.cafespot.cafepage.Ranking
import com.example.cafespot.cafepage.RankingAdapter
import com.example.cafespot.hotpage.HotItem
import com.example.cafespot.hotpage.HotViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RankingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RankingFragment : Fragment() {
    private var hotItem: HotItem? = null
    private lateinit var cafeName: TextView
    private lateinit var rankingAverage: TextView
    private lateinit var rankingCount: TextView
    private lateinit var button:Button
    val adapter = RankingAdapter()

    private var rankingList:MutableList<Ranking> = mutableListOf(
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
//        rankingList = mutableListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view:View = inflater.inflate(R.layout.fragment_ranking, container, false)
        cafeName = view.findViewById(R.id.ranking_cafe_name)
        rankingAverage = view.findViewById(R.id.average_ranking)
        rankingCount = view.findViewById(R.id.ranking_count)
        button = view.findViewById(R.id.write_ranking_button)

        val recyclerViewRanking:RecyclerView = view.findViewById(R.id.recyclerView_ranking)
        recyclerViewRanking.adapter = adapter

        val viewModel = ViewModelProvider(requireActivity()).get(HotViewModel::class.java)
        viewModel.fetchCafeScore()
        viewModel.currentHotItem.observe(viewLifecycleOwner){
            hotItem = it
            cafeName.text = it.cafeName

//            println("rankingFrag_wifiStability:"+it.cafeScore.wifiStablilty)
                rankingAverage.text =
                    String.format(getResources().getString(R.string.average_ranking), it.averageScore)
                rankingCount.text =
                    String.format(getResources().getString(R.string.ranking_count), it.allScorePeople)
//                adapter.refresh(rankingList)
        }

//        viewModel.RankingList.observe(viewLifecycleOwner){
//            adapter.refresh(it)
//        }

//            rankingList.add(Ranking("wifi穩定", hotItem?.cafeScore.wifiStablilty))
//            rankingList.add(Ranking("插座夠多", hotItem.cafeScore.plugCountDegree))
//            rankingList.add(Ranking("安靜程度", hotItem.cafeScore.quietDegree))
//            rankingList.add(Ranking("咖啡好喝", hotItem.cafeScore.coffeeDegree))
//            rankingList.add(Ranking("餐點好吃", hotItem.cafeScore.foodDegree))
//            rankingList.add(Ranking("價格便宜", hotItem.cafeScore.priceDegree))
//            rankingList.add(Ranking("裝潢美觀", hotItem.cafeScore.decorateDegree))
//            rankingList.add(Ranking("明亮程度", hotItem.cafeScore.brightness))
//            rankingList.add(Ranking("音樂好聽", hotItem.cafeScore.musicDegree))


        button.setOnClickListener {
            val writeRankingFragment = WriteRankingFragment()
            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_blank2_layout, writeRankingFragment)
            transaction.commit()
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        val viewModel = ViewModelProvider(requireActivity()).get(HotViewModel::class.java)
        viewModel.fetchCafeScore()
        viewModel.rankingData.observe(viewLifecycleOwner){
            rankingAverage.text =
                String.format(getResources().getString(R.string.average_ranking), it.averageScore)
            rankingCount.text =
                String.format(getResources().getString(R.string.ranking_count), it.allScorePeople)
        }
        viewModel.RankingList.observe(viewLifecycleOwner){
            adapter.refresh(it)
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RankingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            RankingFragment().apply {
                arguments = Bundle().apply {
//                    putParcelable("hot", hotItem)
                }
            }
    }
}