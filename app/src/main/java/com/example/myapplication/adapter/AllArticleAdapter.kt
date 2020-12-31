package com.example.myapplication.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.activity.ShowArticalActivity
import com.example.myapplication.activity.ShowProflieOfOthersActivity
import com.example.myapplication.activity.UserProflie
import com.example.myapplication.model.UserNote
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class AllArticleAdapter(val context: Context, private val itemList: ArrayList<UserNote>): RecyclerView.Adapter<AllArticleAdapter.ArticleShowingViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleShowingViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.sigle_row_text_for_all,parent,false)
        return ArticleShowingViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ArticleShowingViewHolder, position: Int) {
        val note=itemList[position]
        holder.txtUserName.text=note.username
        holder.txtTitle.text=note.title
        holder.content.text=note.notes
        Picasso.get().load(note.profileImageUrl).error(android.R.drawable.ic_dialog_alert).fit().centerInside().into(holder.profileOfImages)
        Picasso.get().load(note.articalImageUrl).error(android.R.drawable.ic_dialog_alert).fit().centerInside().into(holder.imageViewArticle)
        Log.d("hi", "$note.notes")
        holder.llContent.setOnClickListener {
            val intent= Intent(context, ShowArticalActivity::class.java)
            intent.putExtra("title",note.title)
            intent.putExtra("notes",note.notes)
            intent.putExtra("image",note.articalImageUrl)
            intent.putExtra("userName",note.username)
            intent.putExtra("profile",note.profileImageUrl)
            intent.putExtra("uid",note.uid)
            intent.putExtra("key",note.key)
            context.startActivity(intent)
        }
        holder.profileOfImages.setOnClickListener {
            if(note.uid== FirebaseAuth.getInstance().uid ?: ""){
                val intent= Intent(context, UserProflie::class.java)
                context.startActivity(intent)
            }
          else{
                val intent= Intent(context, ShowProflieOfOthersActivity::class.java)
                intent.putExtra("uid",note.uid)
                context.startActivity(intent)
            }

        }
        holder.txtUserName.setOnClickListener {
            if(note.uid== FirebaseAuth.getInstance().uid ?: ""){
                val intent= Intent(context, UserProflie::class.java)
                context.startActivity(intent)
            }
            else{
                val intent= Intent(context, ShowProflieOfOthersActivity::class.java)
                intent.putExtra("uid",note.uid)
                context.startActivity(intent)
            }
        }


    }
    class ArticleShowingViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtUserName: TextView =view.findViewById(R.id.single_row_textView_for_user_name)
    val llContent: ConstraintLayout =view.findViewById(R.id.llContent_for_all)
        val txtTitle: TextView =view.findViewById(R.id.single_row_textView_for_title_all)
        val  profileOfImages:ImageView=view.findViewById(R.id.profile_of_image_for_all)
        val imageViewArticle:ImageView=view.findViewById(R.id.image_view_for_article)
        val content:TextView=view.findViewById(R.id.single_row_textView_for_content_all)
    }




}