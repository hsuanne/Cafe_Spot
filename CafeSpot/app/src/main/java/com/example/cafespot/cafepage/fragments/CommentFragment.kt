package com.example.cafespot.cafepage.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.cafespot.R
import com.example.cafespot.cafepage.Comment
import com.example.cafespot.hotpage.HotItem
import com.example.cafespot.hotpage.HotViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [CommentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CommentFragment : Fragment() {
    private var hotItem: HotItem? = null
    private var comment: Comment? = null
    private lateinit var cafeName: TextView
    private lateinit var button: Button
    private lateinit var commentCount: TextView
    val adapter = CommentAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        hotItem = arguments?.getParcelable<HotItem>("hot")
//        comment = arguments?.getParcelable("comment")
        println("comment fragment oncreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_comment, container, false)
        cafeName = view.findViewById(R.id.comment_cafe_name)
        button = view.findViewById(R.id.write_comment_button)
        commentCount = view.findViewById(R.id.comment_count)

        val viewModel = ViewModelProvider(requireActivity()).get(HotViewModel::class.java)
        viewModel.currentHotItem.observe(viewLifecycleOwner) { currentHotItem ->
            cafeName.text = currentHotItem.cafeName
        }

        button.setOnClickListener {
            val writeCommentFragment = WriteCommentFragment()
            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_blank_layout, writeCommentFragment)
            transaction.commit()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerViewComment: RecyclerView = view.findViewById(R.id.recyclerView_comment)
//        recyclerViewRanking.layoutManager = GridLayoutManager(activity,2)
        recyclerViewComment.adapter = adapter
        recyclerViewComment.setOnTouchListener { v, event ->
            v.findViewById<ScrollView>(R.id.comment_scroll).parent.requestDisallowInterceptTouchEvent(false)
            false
        }
//        adapter.refresh(hotItem?.commentList!!)
//        adapter.refresh(commentList)
    }

    override fun onResume() {
        super.onResume()
        println("comment fragment onResume")
        val viewModel = ViewModelProvider(requireActivity()).get(HotViewModel::class.java)
        CoroutineScope(Dispatchers.Default).launch {
            launch { viewModel.fetchCafeComment() }.join()
        }
        viewModel.commentData.observe(viewLifecycleOwner) {
            commentCount.text =
                String.format(getResources().getString(R.string.comment_count), it)
        }
        viewModel.CommentList.observe(viewLifecycleOwner) {
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
         * @return A new instance of fragment CommentFragment.
        //         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            CommentFragment().apply {
                arguments = Bundle().apply {
//                    putParcelable("hot", hotItem)
                }
            }
    }

    fun addComment() {
        println("addComment")
    }
}