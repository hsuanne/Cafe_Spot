package com.example.cafespot.googleMap

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.cafespot.App
import com.example.cafespot.FavoriteActivity
import com.example.cafespot.R
import com.example.cafespot.cafepage.Ranking
import com.example.cafespot.databinding.FragmentFilterBinding
import com.example.cafespot.hotpage.HotViewModel
import com.google.android.gms.maps.SupportMapFragment

class FilterFragment : Fragment() {
    private lateinit var binding:FragmentFilterBinding

    val hotViewModel:HotViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFilterBinding.inflate(inflater, container, false)
        val button = binding.filterButton
//        val autoCompleteTextView = binding.levelAuto

//        val levels = resources.getStringArray(R.array.levels)
//        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, levels)
//        autoCompleteTextView.setAdapter(adapter)
//
//        autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
//            Toast.makeText(requireContext(), levels.get(position), Toast.LENGTH_SHORT).show()
//        }

        val recyclerViewRanking: RecyclerView = binding.recyclerViewFilter
        val adapter = FilterAdapter()
        recyclerViewRanking.adapter = adapter
        var filterList:List<Ranking> = listOf(
            Ranking("wifi穩定",0f),
            Ranking("插座夠多",0f),
            Ranking("環境安靜",0f),
            Ranking("咖啡好喝",0f),
            Ranking("餐點好吃",0f),
            Ranking("價格便宜",0f),
            Ranking("裝潢美觀",0f),
            Ranking("光線明亮",0f),
            Ranking("有賣甜點",0f),
            Ranking("有賣正餐",0f),
            Ranking("站立工作",0f),
            Ranking("可以久坐",0f)
        )
        adapter.refresh(filterList)

        var location: Location? = null
        hotViewModel.currentLocation.observe(viewLifecycleOwner){
            println("FilterFrag_currentLocation:" + it.latitude)
            location = it
        }
        button.setOnClickListener {
//            val mapFragment = MapFragment()
//            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
//            transaction.replace(R.id.activity_map_fragment, SupportMapFragment())
//            transaction.commit()
            App.currentUserLiveData.value = adapter.getSelectedL()
            button.visibility = View.INVISIBLE
            requireActivity().finish()
        }
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FilterFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            FilterFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}