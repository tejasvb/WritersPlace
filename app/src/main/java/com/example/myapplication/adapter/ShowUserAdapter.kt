package com.example.myapplication.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.activity.ShowProflieOfOthersActivity
import com.example.myapplication.activity.UserProflie
import com.example.myapplication.model.User
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class ShowUserAdapter (val context: Context, private val itemList: ArrayList<User>): RecyclerView.Adapter<ShowUserAdapter.UserShowingViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserShowingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_user, parent, false)
        return UserShowingViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
    override fun onBindViewHolder(holder: UserShowingViewHolder, position: Int) {
        val note = itemList[position]
        holder.txtUserName.text = note.username
        Picasso.get().load(note.profileImageUrl).error(android.R.drawable.ic_dialog_alert)
            .into(holder.profileOfImages)
        holder.llContent.setOnClickListener {
            if (note.uid == FirebaseAuth.getInstance().uid ?: "") {
                val intent = Intent(context, UserProflie::class.java)
                context.startActivity(intent)
            } else {
                val intent = Intent(context, ShowProflieOfOthersActivity::class.java)
                intent.putExtra("uid", note.uid)
                context.startActivity(intent)
            }

        }

    }
        class UserShowingViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtUserName: TextView =view.findViewById(R.id.single_row_textView_for_title_all)
        val llContent:RelativeLayout =view.findViewById(R.id.relative_layout_single_row)
        val  profileOfImages: ImageView =view.findViewById(R.id.image_for_single_user)
    }

}


