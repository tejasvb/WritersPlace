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
import com.example.myapplication.adapter.ArticleAdapter
import com.example.myapplication.R
import com.example.myapplication.adapter.EndlessRecyclerOnScrollListener
import com.example.myapplication.model.UserNote
import com.example.myapplication.util.ConnectionManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*



@Suppress("DEPRECATION")
class UserArticalFragment : Fragment() {
  private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recycleAdapter: ArticleAdapter
    private lateinit var recyclerView:RecyclerView
    private lateinit var  progressBar: ProgressBar
    private lateinit var  progressBar1: ProgressBar
    private var currentPage = 0
    var isLoading = false
    private lateinit var  refreshForAll: SwipeRefreshLayout
    var    allNotes= mutableListOf<UserNote>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


   val view=inflater.inflate(R.layout.fragment_online, container, false)
         recyclerView = view.findViewById(R.id.recycler_view_for_of_online)
        layoutManager=LinearLayoutManager(activity)
        fetchUserData()
        refreshForAll = view.findViewById(R.id.refresh_layout_user_article)
        if (activity != null) {
            recycleAdapter = ArticleAdapter(
                activity as Context,
                allNotes as ArrayList<UserNote>
            )
            recyclerView.adapter = recycleAdapter
            recyclerView.layoutManager = layoutManager
            recyclerView.itemAnimator= DefaultItemAnimator()
            recyclerView.setOnScrollListener(object : EndlessRecyclerOnScrollListener(layoutManager as LinearLayoutManager) {
                override fun onLoadMore(current_page: Int) {
                    loadMoreData()
                }
            })
        }
        progressBar=view.findViewById(R.id.progress_bar_for_follower_user_article)
        progressBar1=view.findViewById(R.id.progressBar)
        progressBar1.visibility=View.GONE
        fetchUserData()
        initScrollListener()
        refreshForAll.setOnRefreshListener {
            refreshForAll.isRefreshing = false
            fetchUserData()
            initScrollListener()
        }
        refreshForAll.setColorSchemeColors(Color.BLACK)
        if(allNotes.isEmpty()){
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
        return view
    }
    private fun fetchUserData() {
        allNotes.clear()
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/article/$uid")
        ref.limitToFirst(AllArticleFragment.TOTAL_ITEM_EACH_LOAD)
            .startAt(currentPage * AllArticleFragment.TOTAL_ITEM_EACH_LOAD.toDouble())
        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val note= snapshot.getValue(UserNote::class.java)
                if (note != null) {
                    allNotes.add(note)
                }
                if(allNotes.isNotEmpty()&&(activity!=null)){
                    recycleAdapter= ArticleAdapter(activity as Context, allNotes as ArrayList<UserNote>)
                }
                recyclerView.adapter = recycleAdapter
                recyclerView.layoutManager = layoutManager
                progressBar.visibility=View.GONE
                progressBar1.visibility=View.GONE
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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        refreshForAll.setOnRefreshListener {
            fetchUserData()                    // refresh your list contents somehow
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
                        allNotes.size - 1) {
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
        fetchUserData()
        progressBar1.visibility = RecyclerView.VISIBLE
    }

}



