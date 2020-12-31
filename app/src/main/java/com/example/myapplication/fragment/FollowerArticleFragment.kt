package com.example.myapplication.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.myapplication.R
import com.example.myapplication.adapter.EndlessRecyclerOnScrollListener
import com.example.myapplication.adapter.AllArticleAdapter
import com.example.myapplication.model.UserNote
import com.example.myapplication.util.ConnectionManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase


@Suppress("NAME_SHADOWING", "DEPRECATION")
class FollowerArticleFragment : Fragment() {
    var isLoading = false
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var  refreshForAll: SwipeRefreshLayout
    var allUsersArticle = mutableListOf<UserNote>()
    lateinit var  progressBar:ProgressBar
    lateinit var  progressBar1:ProgressBar
    private var currentPage = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_follower_article, container, false)
        recyclerView = view.findViewById(R.id.recycler_view_for_follower)
        layoutManager= LinearLayoutManager(activity)
        val  recycleAdapter = AllArticleAdapter(
            activity as Context,
            allUsersArticle as ArrayList<UserNote>
        )
        recyclerView.adapter=recycleAdapter
        recyclerView.layoutManager=layoutManager
        recyclerView.itemAnimator=DefaultItemAnimator()
            recyclerView.setOnScrollListener(object : EndlessRecyclerOnScrollListener(layoutManager as LinearLayoutManager) {
                override fun onLoadMore(current_page: Int) {
                    loadMoreData()
                }
            })
         progressBar=view.findViewById(R.id.progress_bar_for_follower_article)
        progressBar1=view.findViewById(R.id.progressBar)
        fetchFollowerData()
        initScrollListener()
        refreshForAll = view.findViewById(R.id.refresh_layout_for_follower)
        refreshForAll.setOnRefreshListener {
            refreshForAll.isRefreshing = false
            fetchFollowerData()
            initScrollListener()
        }
        refreshForAll.setColorSchemeColors(Color.BLACK)
        if(allUsersArticle.isEmpty()){
            progressBar.visibility=View.GONE
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
        if(allUsersArticle.isEmpty()){
            progressBar.visibility=View.GONE
        }
        return view
    }
    private fun fetchFollowerData() {
        allUsersArticle.clear()
      val uid=  FirebaseAuth.getInstance().uid ?: return
        val refUser = FirebaseDatabase.getInstance().getReference("/users/$uid/following")
        refUser.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val uid = snapshot.key
                fetchUserData(uid.toString())
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
    private fun fetchUserData(UserUid: String) {
        if(UserUid.isEmpty())return
        val ref = FirebaseDatabase.getInstance().getReference("/article/$UserUid")
      ref.limitToFirst(AllArticleFragment.TOTAL_ITEM_EACH_LOAD)
            .startAt(currentPage * AllArticleFragment.TOTAL_ITEM_EACH_LOAD.toDouble())
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val note = snapshot.getValue(UserNote::class.java)
                if (note != null) {
                    allUsersArticle.add(note)
                }
                if (activity != null) {
                    val recycleAdapter = AllArticleAdapter(
                        activity as Context,
                        allUsersArticle as ArrayList<UserNote>
                    )
                    recyclerView.adapter = recycleAdapter
                    recyclerView.layoutManager = layoutManager
                     progressBar.visibility=View.GONE
                    progressBar1.visibility=View.GONE
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
                Log.w("Error FAF", "loadPost:onCancelled", databaseError.toException())
                // ...
            }


        })


    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        refreshForAll.setOnRefreshListener {
            fetchFollowerData()                    // refresh your list contents somehow
            refreshForAll.isRefreshing = false   // reset the SwipeRefreshLayout (stop the loading spinner)
        }

    }
    private fun initScrollListener() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null &&
                        linearLayoutManager.findLastCompletelyVisibleItemPosition() ==
                        allUsersArticle.size - 1) {
                        //bottom of list!
                        loadMoreData()
                        isLoading = true
                    }
                }
            }
        })
    }
    private fun loadMoreData() {
        currentPage++
        fetchFollowerData()
      //  progressBar1.visibility = RecyclerView.VISIBLE
    }
}