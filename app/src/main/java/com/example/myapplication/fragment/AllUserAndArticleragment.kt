package com.example.myapplication.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapter.AllArticleAdapter
import com.example.myapplication.adapter.ShowUserAdapter
import com.example.myapplication.model.User
import com.example.myapplication.model.UserNote
import com.example.myapplication.util.ConnectionManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase


class AllUserAndArticleragment : Fragment() {
    private var query: String? = null
    private var choice: String? = null
    lateinit var progressBar:ProgressBar
    private lateinit var layoutManager: RecyclerView.LayoutManager
    var allUsersArticle = mutableListOf<User>()
    private lateinit var recyclerView: RecyclerView
    var searchFull= mutableListOf<UserNote>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       val view= inflater.inflate(R.layout.fragment_all_user_and_articleragment, container, false)
        val bundle = arguments
        progressBar=view.findViewById(R.id.progress_bar_for_chat_log_result)
        query=bundle?.getString("message")
        choice=bundle?.getString("bit")
        if(choice=="article"){
            query?.let {
                performSubmitArticle(it)
            }
        }

        if(choice=="user"){
            query?.let {
                performSubmitForUser(it)
            }
        }
        if(!(ConnectionManager().checkConnectivity(activity as Context))) {
            val snackBar = Snackbar.make(recyclerView, "No Internet Connection Is Available",
                Snackbar.LENGTH_LONG
            ).setAction("Action", null)
            snackBar.setActionTextColor(Color.BLUE)
            val snackBarView = snackBar.view
            snackBarView.setBackgroundColor(Color.BLACK)
            snackBar.show()
           progressBar.visibility=View.GONE
        }
        else{
           progressBar.visibility=View.VISIBLE
        }
        recyclerView = view.findViewById(R.id.recycler_view_for_of_search_author_all)
        return  view
    }
    private fun performSubmitForUser(search: String) {

        allUsersArticle.clear()
        FirebaseAuth.getInstance().uid ?: return
        val refUser = FirebaseDatabase.getInstance().getReference("/users")
        refUser.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val note = snapshot.getValue(User::class.java)
                if (note?.username?.contains(search, ignoreCase = true)!!) {

                    allUsersArticle.add(note)

                }

                val recycleAdapter = ShowUserAdapter(
                    activity as Context,
                    allUsersArticle as ArrayList<User>

                )
                recyclerView.adapter = recycleAdapter
                layoutManager = LinearLayoutManager(activity)
                recyclerView.layoutManager = layoutManager
                progressBar.visibility=View.GONE
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

                val recycleAdapter = AllArticleAdapter(
                    activity as Context,
                    searchFull as ArrayList<UserNote>
                )
                recyclerView.adapter = recycleAdapter
                layoutManager = LinearLayoutManager(activity)
                recyclerView.layoutManager = layoutManager
                progressBar.visibility=View.GONE

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