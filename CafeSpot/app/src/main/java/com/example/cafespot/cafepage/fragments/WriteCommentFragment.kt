package com.example.cafespot.cafepage.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.cafespot.R
import com.example.cafespot.SharedPref
import com.example.cafespot.USER_ID
import com.example.cafespot.USER_NAME
import com.example.cafespot.cafepage.Comment
import com.example.cafespot.hotpage.CommentPost
import com.example.cafespot.hotpage.HotViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 * Use the [WriteCommentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WriteCommentFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_write_comment, container, false)
        val publish = view.findViewById<Button>(R.id.post_comment_button)
        val cancel = view.findViewById<Button>(R.id.cancel_comment_button)
        val editText = view.findViewById<EditText>(R.id.write_comment_edittext)
        val viewModel = ViewModelProvider(requireActivity()).get(HotViewModel::class.java)

        publish.setOnClickListener {
            val words = editText.text.toString()
            if (words.isEmpty()) {
                Toast.makeText(requireContext(), "必須輸入內容", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val comment = Comment("", USER_ID, USER_NAME, words, "url")
            viewModel.currentHotItem.value?.cafeDiscuss?.add(comment)
//            val manager = parentFragmentManager
//            manager.beginTransaction().remove(this).commit()

//            val userId = SharedPref.getInstance(requireContext()).getUser()?.userId
            //轉成Json
            val cafeId = viewModel.currentHotItem.value?.cafeId!!
            //TODO:監聽當前user val userId = viewModel.currentHotItem.value?.userId!!
            val commentPost = CommentPost(cafeId, USER_ID, words)
            val body = viewModel.CommentToJson(commentPost)

            CoroutineScope(Dispatchers.Default).launch {
                launch {
                    viewModel.postComment(body)
                }.join()
            }
        }

        viewModel.postCommentCode.observe(viewLifecycleOwner) {
            when (it) {
                1 -> {
                    val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragment_blank_layout, CommentFragment())
                    transaction.commit()
                }
                2 -> {
                    Toast.makeText(requireContext(), "輸入錯誤", Toast.LENGTH_SHORT).show()
                }
                3 -> {
                    Toast.makeText(requireContext(), "您已評論過此店!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        cancel.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_blank_layout, CommentFragment())
            transaction.commit()
        }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WriteCommentFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WriteCommentFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}