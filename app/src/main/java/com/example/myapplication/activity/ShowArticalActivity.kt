package com.example.myapplication.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.R
import com.example.myapplication.model.follow
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_show_artical.*
import kotlinx.android.synthetic.main.activity_show_artical.profile_of_image_for_all



class ShowArticalActivity : AppCompatActivity() {
    var boolLike:Boolean=false
    var boolUnLike:Boolean=false
    var boolFav:Boolean=false
    var note: String? = null
    private var images: String? = null
   var username: String? = null
   private var profile: String? = null
    var title: String? = null
    var uid: String? = null
    private var key: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_artical)
       note = intent.getStringExtra("notes")
         images = intent.getStringExtra("image")
       username = intent.getStringExtra("userName")
        profile = intent.getStringExtra("profile")
         title = intent.getStringExtra("title")
        uid = intent.getStringExtra("uid")
       key = intent.getStringExtra("key")
        title_of_article_show.text = title
        edit_text_add_notes_show.text = note
        title_of_Username.text = username
        checkForLike(uid.toString(), key.toString())
        checkForUnLike(uid.toString(),key.toString())
        checkForFav(uid.toString(),key.toString())
        ll_for_others_profile.setOnClickListener {
            val intent = Intent(this, ShowProflieOfOthersActivity::class.java)
            intent.putExtra("uid", uid)
            startActivity(intent)

        }
        val refForm=FirebaseDatabase.getInstance().getReference("/article/$uid/$key/like")
        refForm.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                number_of_like.text = snapshot.childrenCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        val refTo=FirebaseDatabase.getInstance().getReference("/article/$uid/$key/unlike")
        refTo .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                number_of_unlike.text = snapshot.childrenCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
            like.setOnClickListener {
                 addOrRemoveLike(uid.toString(),key.toString())
            }
        unlike.setOnClickListener {
            addOrRemoveUnLike(uid.toString(),key.toString())
        }
        fav.setOnClickListener{
            addOrRemoveFromFav(uid.toString(),key.toString())
        }
            comment_in_all.setOnClickListener {

                val intent = Intent(this, ChatReplayActivity::class.java)
                intent.putExtra("uid", uid)
                intent.putExtra("key", key)
                startActivity(intent)
            }
            Picasso.get().load(images).error(R.drawable.blue).fit().centerInside()
                .into(image_view_for_article_show)
            Picasso.get().load(profile).error(R.drawable.blue).into(profile_of_image_for_all)
        }

    private fun addOrRemoveFromFav(uid: String, key: String) {
        val currentUid: String?=HomeActivity.currentUser?.uid
        val refto=FirebaseDatabase.getInstance().getReference("/users/$currentUid/Fav/$uid/$key")
        boolFav = if(boolFav){
            Picasso.get().load(R.drawable.ic_thume_down_foreground).resize(30,30).into(fav)

            refto.removeValue()
            false
        } else{
            Picasso.get().load(R.drawable.ic_thume_down_red_foreground).resize(30,30).into(fav)
            refto.setValue(follow(currentUid.toString()))
            true
        }
    }
    private fun addOrRemoveUnLike(uid: String, key: String) {
        val currentUid: String?=HomeActivity.currentUser?.uid
        val refto=FirebaseDatabase.getInstance().getReference("/users/$currentUid/unlike/$uid/$key")
        val refForm=FirebaseDatabase.getInstance().getReference("article/$uid/$key/unlike/$currentUid")
        val refto1=FirebaseDatabase.getInstance().getReference("/users/$currentUid/like/$uid/$key")
        val refForm1=FirebaseDatabase.getInstance().getReference("article/$uid/$key/like/$currentUid")
        boolUnLike = if(boolUnLike){
            Picasso.get().load(R.drawable.ic_thume_down_foreground).resize(30,30).into(unlike)
            refForm.removeValue()
            refto.removeValue()
            false
        } else{
            Picasso.get().load(R.drawable.ic_thume_down_red_foreground).resize(30,30).into(unlike)
            refForm.setValue(follow(uid))
            refto.setValue(follow(currentUid.toString()))
            refForm1.removeValue()
            refto1.removeValue()
            true
        }
    }
    private fun addOrRemoveLike(uid:String,key:String){
      val currentUid: String?=HomeActivity.currentUser?.uid
        val refto=FirebaseDatabase.getInstance().getReference("/users/$currentUid/like/$uid/$key")
            val refForm=FirebaseDatabase.getInstance().getReference("article/$uid/$key/like/$currentUid")
        val refto1=FirebaseDatabase.getInstance().getReference("/users/$currentUid/unlike/$uid/$key")
        val refForm1=FirebaseDatabase.getInstance().getReference("article/$uid/$key/unlike/$currentUid")
            boolLike = if(boolLike){
                Picasso.get().load(R.drawable.ic_thume_up_foreground).resize(30,30).into(like)
                refForm.removeValue()
                refto.removeValue()
                false
            } else{
                Picasso.get().load(R.drawable.ic_thume_up_red_foreground).resize(30,30).into(like)
                refForm.setValue(follow(uid))
                refto.setValue(follow(currentUid.toString()))
                refForm1.removeValue()
                refto1.removeValue()
                true
            }
        }
    private fun checkForLike(uid:String,key:String): Boolean {
    val currentUid: String?=HomeActivity.currentUser?.uid

        val ref = FirebaseDatabase.getInstance()
            .getReference("article/$uid/$key/like/$currentUid")
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()) {
                        like.setImageResource(R.drawable.ic_thume_up_red_foreground)
                      this@ShowArticalActivity.boolLike=true
                    }
                    else{
                        like.setImageResource(R.drawable.ic_thume_up_foreground)
                        this@ShowArticalActivity.boolLike=false
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        return boolLike
    }
    private fun checkForFav(uid:String,key:String): Boolean {
        val currentUid: String?=HomeActivity.currentUser?.uid

        val ref = FirebaseDatabase.getInstance()
            .getReference("/users/$currentUid/Fav/$uid/$key")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    fav.setImageResource(R.drawable.ic_heart_red_foreground)
                    this@ShowArticalActivity.boolFav=true
                }
                else{
                    fav.setImageResource(R.drawable.ic_heart_foreground)
                    this@ShowArticalActivity.boolFav=false
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        return boolLike
    }
    private fun checkForUnLike(uid:String,key:String): Boolean {
        val currentUid: String?=HomeActivity.currentUser?.uid

        val ref = FirebaseDatabase.getInstance()
            .getReference("article/$uid/$key/unlike/$currentUid")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    unlike.setImageResource(R.drawable.ic_thume_down_red_foreground)
                    this@ShowArticalActivity.boolUnLike=true
                }
                else{
                    unlike.setImageResource(R.drawable.ic_thume_down_foreground)
                    this@ShowArticalActivity.boolUnLike=false
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        return boolLike
    }

}
