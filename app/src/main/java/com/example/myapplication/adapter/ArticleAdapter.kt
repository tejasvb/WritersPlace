package com.example.myapplication.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.activity.ArticleMakingActivity
import com.example.myapplication.activity.UserProflie
import com.example.myapplication.model.UserNote
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso


class ArticleAdapter(val context: Context, private val itemList: ArrayList<UserNote>): RecyclerView.Adapter<ArticleAdapter.NoteMakingviewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteMakingviewHolder {
        val view= LayoutInflater.from(parent.context).inflate(
            com.example.myapplication.R.layout.single_row_text,
            parent,
            false
        )
        return NoteMakingviewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }


    override fun onBindViewHolder(holder: NoteMakingviewHolder, position: Int) {
        val note=itemList[position]
        holder.txtUserName.text=note.username
        holder.txtTitle.text=note.title
        holder.content.text=note.notes
        Picasso.get().load(note.profileImageUrl).error(android.R.drawable.ic_dialog_alert).fit().centerInside().into(
            holder.profileOfImages
        )
        Picasso.get().load(note.articalImageUrl).error(android.R.drawable.ic_dialog_alert).fit().centerInside().into(
            holder.imageViewArticle
        )

        holder.llcontent.setOnClickListener {
            val intent=Intent(context, ArticleMakingActivity::class.java)
            intent.putExtra("uid", note.uid)
            intent.putExtra("title", note.title)
            intent.putExtra("notes", note.notes)
            intent.putExtra("image", note.articalImageUrl)
            intent.putExtra("key", note.key)
intent.putExtra("filename", note.filename)
            context.startActivity(intent)
        }
        holder.profileOfImages.setOnClickListener {
                val intent= Intent(context, UserProflie::class.java)
                context.startActivity(intent)
        }
        holder.txtUserName.setOnClickListener {
                val intent= Intent(context, UserProflie::class.java)
                context.startActivity(intent)

        }

        holder.imageMenu.setOnClickListener {
            val popup = PopupMenu(context, holder.imageMenu)
            popup.inflate(com.example.myapplication.R.menu.menu_for_artical)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    com.example.myapplication.R.id.delete -> {
                        val ref = FirebaseDatabase.getInstance()
                            .getReference("article/${note.uid}/${note.key}")
                        val ref1=
                            FirebaseStorage.getInstance().getReferenceFromUrl(note.articalImageUrl)
                        ref1.delete().addOnSuccessListener {
                            Toast.makeText(context, "Delete", Toast.LENGTH_SHORT).show()
                        }
                            .addOnFailureListener {
                                Toast.makeText(context, "Not Able To Delete", Toast.LENGTH_SHORT).show()
                            }
                        ref.removeValue()
                    }

                }
                false
            }
            popup.show()
        }

    }
    class NoteMakingviewHolder(view: View):RecyclerView.ViewHolder(view){
        val txtUserName: TextView =view.findViewById(com.example.myapplication.R.id.single_row_textView_for_user_name)
        val llcontent: ConstraintLayout =view.findViewById(com.example.myapplication.R.id.llContent_for_all)
        val txtTitle: TextView =view.findViewById(com.example.myapplication.R.id.single_row_textView_for_title_all)
        val  profileOfImages:ImageView=view.findViewById(com.example.myapplication.R.id.profile_of_image_for_all)
        val imageViewArticle:ImageView=view.findViewById(com.example.myapplication.R.id.image_view_for_article)
val imageMenu:ImageView=view.findViewById(com.example.myapplication.R.id.menu_for_all_article)
        val content:TextView=view.findViewById(com.example.myapplication.R.id.single_row_textView_for_content_all)

    }



}