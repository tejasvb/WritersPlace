package com.example.myapplication.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R


import com.example.myapplication.model.ChatLog
import com.squareup.picasso.Picasso


class chatLogAdapter(val context: Context, private val itemList: ArrayList<ChatLog>): RecyclerView.Adapter<chatLogAdapter.ChatLogviewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatLogviewHolder {
        val view= LayoutInflater.from(parent.context).inflate(
            R.layout.single_chat_row,
            parent,
            false
        )
        return ChatLogviewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ChatLogviewHolder, position: Int) {
        val chatLog=itemList[position]
        holder.username.text=chatLog.sender
        holder.message.text=chatLog.message
        Picasso.get().load(chatLog.senderProflie).into(holder.profileImage)


//        holder.llcontent.setOnClickListener {
//            FirebaseAuth.getInstance().uid ?: ""
//            val intent= Intent(context, ArticleMakingActivity::class.java)
//            intent.putExtra("uid",note.uid)
//            intent.putExtra("title",note.title)
//            intent.putExtra("notes",note.notes)
//            intent.putExtra("image",note.articalImageUrl)
//            intent.putExtra("key",note.key)
//            intent.putExtra("filename",note.filename)
//            context.startActivity(intent)
//
//
//        }
//     holder.replay.setOnClickListener {
//       val intent= Intent(context,ReplyActivity()::class.java)
//         intent.putExtra("username", chatLog.sender)
//         intent.putExtra("message", chatLog.message)
//         intent.putExtra("profile", chatLog.senderProflie)
//        context.startActivity(intent)
//
//     }


    }
    class ChatLogviewHolder(view: View): RecyclerView.ViewHolder(view){
        val profileImage: ImageView =view.findViewById(R.id.profile_of_chat_log)
        val username: TextView =view.findViewById(R.id.username_for_chat_log)
        val message: TextView =view.findViewById(R.id.comment_for_chat_log)
//        val replay: LinearLayout=view.findViewById(R.id.layout_for_replay)
//        val noOfReplay:TextView=view.findViewById(R.id.replay_to_the_comment_number)

    }



}