package com.example.myapplication.activity

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapter.AllArticleAdapter
import com.example.myapplication.model.User
import com.example.myapplication.model.UserNote
import com.example.myapplication.model.follow
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_show_artical.profile_of_image_for_all
import kotlinx.android.synthetic.main.activity_show_proflie_of_others.*

class ShowProflieOfOthersActivity : AppCompatActivity() {
    private var bool1: Boolean=false
    var    allNotes= mutableListOf<UserNote>()
    var recycleAdapter:AllArticleAdapter?=null
    private lateinit var layoutManager: RecyclerView.LayoutManager
    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_proflie_of_others)
        val uid=intent.getStringExtra("uid")
        layoutManager=LinearLayoutManager(this)
        fetchOtherUser(uid)
        checkForFollow(uid)
        fetchUserData(uid)
        to_follow.setOnClickListener {
      addOrDeleteFollower(uid)

        }
        val refForm=FirebaseDatabase.getInstance().getReference("/users/$uid/following")
        refForm.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                show_to_following.text = snapshot.childrenCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        val refto=FirebaseDatabase.getInstance().getReference("/users/$uid/follower")
        refto.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                show_follower.text = snapshot.childrenCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }
    private fun addOrDeleteFollower(uid: String?) {
        if(uid==null)return
        val currentUid: String?=HomeActivity.currentUser?.uid
        val refForm=FirebaseDatabase.getInstance().getReference("/users/$currentUid/following/$uid")
        val refto=FirebaseDatabase.getInstance().getReference("/users/$uid/follower/$currentUid")
        bool1 = if(bool1){
            to_follow.setTextColor(Color.parseColor("#0000FF"))

            refForm.removeValue()
            refto.removeValue()
            false
        } else{
            to_follow.setTextColor(Color.parseColor("#393648"))
            refForm.setValue(follow(uid))
            refto.setValue(follow(currentUid.toString()))
            true
        }


    }
    private fun checkForFollow(uid: String){
        val currentUid: String?=HomeActivity.currentUser?.uid
        val ref=FirebaseDatabase.getInstance().getReference("/users/$currentUid/following/$uid")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    to_follow.setTextColor(Color.parseColor("#393648"))

               this@ShowProflieOfOthersActivity.bool1 =  true
             }
                else{
                    to_follow.setTextColor(Color.parseColor("#0000FF"))
                 this@ShowProflieOfOthersActivity.bool1 = false
             }


            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
    private fun fetchOtherUser(uid: String) {
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)
                Picasso.get().load(user?.profileImageUrl).error(R.drawable.blue).into(
                    profile_of_image_for_all)
                Picasso.get().load(user?.background).error(R.drawable.blue).into(
                    id_Image)
                title_of_Username_profile.text = user?.username
                title_of_Username_profile.gravity = Gravity.CENTER_HORIZONTAL
                title_of_Username_profile_description.text=user?.description

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
    private fun fetchUserData(uid: String) {
        val ref = FirebaseDatabase.getInstance().getReference("/article/$uid")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                val note = snapshot.getValue(UserNote::class.java)
                if (note != null) {
                    allNotes.add(note)

                }

                if (allNotes.isNotEmpty()) {
                    recycleAdapter = AllArticleAdapter(this@ShowProflieOfOthersActivity,
                        allNotes as ArrayList<UserNote>)
                    recycler_view_for_other_proflie.adapter = recycleAdapter
                    recycler_view_for_other_proflie.layoutManager = layoutManager
                }

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }


            override fun onCancelled(databaseError: DatabaseError) {
            }


        })


    }



        }