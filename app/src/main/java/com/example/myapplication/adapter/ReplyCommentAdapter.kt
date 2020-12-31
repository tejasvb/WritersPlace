package com.example.myapplication.adapter

import android.content.Context

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.ChatLog
import com.squareup.picasso.Picasso

class ReplyCommentAdapter(val context: Context, private val itemList: ArrayList<ChatLog>): RecyclerView.Adapter<ReplyCommentAdapter.ReplyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(
            R.layout.comment_repl_single,
            parent,
            false
        )
        return ReplyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
    override fun onBindViewHolder(holder: ReplyViewHolder, position: Int) {
        val chatLog=itemList[position]
        holder.username.text=chatLog.sender
        holder.message.text=chatLog.message
        Picasso.get().load(chatLog.senderProflie).into(holder.profileImage)
    }

    class ReplyViewHolder(view: View): RecyclerView.ViewHolder(view){
        val profileImage: ImageView =view.findViewById(R.id.profile_of_chat_log)
        val username: TextView =view.findViewById(R.id.username_for_chat_log)
        val message: TextView =view.findViewById(R.id.comment_for_chat_log)


    }




}