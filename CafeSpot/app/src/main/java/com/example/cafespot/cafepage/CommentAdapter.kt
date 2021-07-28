package com.example.cafespot.cafepage.fragments

import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cafespot.R
import com.example.cafespot.cafepage.Comment

class CommentAdapter:RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var CommentList:List<Comment> = listOf()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.comment_layout, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is CommentViewHolder -> {
                val comment = CommentList[position]
                holder.commentUser.text = comment.userName
                holder.commentWord.text = comment.discussContent
                holder.commentWord.movementMethod = ScrollingMovementMethod.getInstance()
                holder.commentDate.text = comment.discussTime
                Glide.with(holder.itemView.context).load(comment.userPicture).into(holder.commentPic)
                holder.commentWord.setOnTouchListener { v, event ->
                    v.parent.requestDisallowInterceptTouchEvent(true)
                    false
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return CommentList.size
    }

    fun refresh(CommentList:List<Comment>){
        this.CommentList = CommentList
        println("CommentList:"+CommentList.size)
        notifyDataSetChanged()
    }
}

class CommentViewHolder(view: View):RecyclerView.ViewHolder(view){
    val commentUser:TextView = view.findViewById(R.id.comment_user)
    val commentWord:TextView = view.findViewById(R.id.comment_textview)
    val commentPic:ImageView = view.findViewById(R.id.comment_profile_image)
    val commentDate:TextView = view.findViewById(R.id.comment_date)
    val commentScroll:ScrollView = view.findViewById(R.id.comment_scroll)
}