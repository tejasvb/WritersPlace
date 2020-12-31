package com.example.myapplication.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapter.AllArticleAdapter
import com.example.myapplication.adapter.ShowUserAdapter
import com.example.myapplication.model.User
import com.example.myapplication.model.UserNote
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class SearchAuthorFragment : Fragment() {

    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerView1: RecyclerView
    var allUsersArticle = mutableListOf<User>()
    private var query: String? = null
var searchFull= mutableListOf<UserNote>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       val view= inflater.inflate(R.layout.fragment_search_authorragment, container, false)
        val bundle = arguments
        query=bundle?.getString("message")
        query?.let {
            performSubmitArticle(it)
            performSubmitForUser(it)

        }
        val  allUser=view.findViewById<TextView>(R.id.all_user)
        allUser.setOnClickListener {
            if (savedInstanceState == null) {
                val fragment = AllUserAndArticleragment()
                val fragmentManager: FragmentManager? = fragmentManager
                val fragmentTransaction = fragmentManager?.beginTransaction()!!
                val frame = Bundle()
                frame.putString("message", query)
                fragment.arguments = frame
                frame.putString("bit", "user")
                fragmentTransaction.replace(R.id.frame_layout_for_searchView, fragment).commit()

            }
    }
        val  allArticle=view.findViewById<TextView>(R.id.all_article)
        allArticle.setOnClickListener {
            if (savedInstanceState == null) {
                val fragment = AllUserAndArticleragment()
                val fragmentManager: FragmentManager? = fragmentManager
                val fragmentTransaction = fragmentManager?.beginTransaction()!!
                val frame = Bundle()
                frame.putString("message", query)
                frame.putString("bit", "article")
                fragment.arguments = frame
                fragmentTransaction.replace(R.id.frame_layout_for_searchView, fragment).commit()

            }
        }
     recyclerView = view.findViewById(R.id.recycler_view_for_of_search_author)
        recyclerView1= view.findViewById(R.id.recycler_view_for_of_search_article)
        return view
    }


    private fun performSubmitForUser(search: String) {
        var flag=true
        allUsersArticle.clear()
        FirebaseAuth.getInstance().uid ?: return
        val refUser = FirebaseDatabase.getInstance().getReference("/users")
        refUser.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val note = snapshot.getValue(User::class.java)
                if (note?.username?.contains(search, ignoreCase = true)!!) {
                    if (flag) {
                        allUsersArticle.add(note)
                    }

                    if (allUsersArticle.size == 3)
                        flag = false
                }
                if(activity!=null){
                    val recycleAdapter = ShowUserAdapter(
                        activity as Context,
                        allUsersArticle as ArrayList<User>

                    )
                    recyclerView.adapter = recycleAdapter
                    layoutManager = LinearLayoutManager(activity)
                    recyclerView.layoutManager = layoutManager

                }


            }


            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })




    }
    private fun performSubmitArticle(search: String) {
        searchFull.clear()
        FirebaseAuth.getInstance().uid ?: return
        val refUser = FirebaseDatabase.getInstance().getReference("/article")
        refUser.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val uid = snapshot.key
                fetchUserData(uid.toString(), search)
            }


            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })




    }
    private fun fetchUserData(UserUid: String, search: String) {
        if(UserUid.isEmpty())return
var flag=true
        val ref = FirebaseDatabase.getInstance().getReference("/article/$UserUid")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val note = snapshot.getValue(UserNote::class.java)
                if (note?.title?.contains(search, ignoreCase = true)!!) {
                    if (flag) {
                        searchFull.add(note)
                    }
                    if (searchFull.size == 3) {
                        flag = false
                    }
                }

                if(activity!=null){
                    val recycleAdapter = AllArticleAdapter(
                        activity as Context,
                        searchFull as ArrayList<UserNote>
                    )

                    recyclerView1.adapter = recycleAdapter
                    layoutManager = LinearLayoutManager(activity)

                    recyclerView1.layoutManager = LinearLayoutManager(
                        activity,
                        LinearLayoutManager.HORIZONTAL,
                        true
                    )
                }


            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("hi", "loadPost:onCancelled", databaseError.toException())
                // ...
            }


        })


    }


}