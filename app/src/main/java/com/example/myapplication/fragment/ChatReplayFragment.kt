package com.example.myapplication.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.myapplication.R
import com.example.myapplication.activity.HomeActivity
import com.example.myapplication.adapter.EndlessRecyclerOnScrollListener
import com.example.myapplication.adapter.chatLogAdapter
import com.example.myapplication.model.ChatLog
import com.example.myapplication.util.ConnectionManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_chat_replay.*


@Suppress("DEPRECATION")
class ChatReplayFragment : Fragment() {
    var username:String?=null
    private var profile:String?=null
    var uid:String?=null
    private var currentUid:String?=null
    private var key1:String?=null
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    var allMessages = mutableListOf<ChatLog>()
    var isLoading = false
    private  lateinit var progressBar:ProgressBar
    private  lateinit var progressBar1:ProgressBar
    private var currentPage = 0
    private lateinit var  refreshForChat: SwipeRefreshLayout
private lateinit var chatLogAdapter:chatLogAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View= inflater.inflate(R.layout.fragment_chat_replay, container, false)
        username= HomeActivity.currentUser?.username
        profile= HomeActivity.currentUser?.profileImageUrl
        currentUid= HomeActivity.currentUser?.uid
        recyclerView=view.findViewById(R.id.recycler_view_for_of_comment)
       val submitButton=view.findViewById<Button>(R.id.comment_add_button)
        layoutManager= LinearLayoutManager(activity)
        val bundle = arguments
        uid=bundle?.getString("uid")
        key1=bundle?.getString("key")
        if (activity != null) {
            chatLogAdapter = chatLogAdapter(
                activity as Context,
                allMessages as ArrayList<ChatLog>)
            recyclerView.adapter = chatLogAdapter
            recyclerView.layoutManager = layoutManager
            recyclerView.setOnScrollListener(object :
                EndlessRecyclerOnScrollListener(layoutManager as LinearLayoutManager) {
                override fun onLoadMore(current_page: Int) {
                   loadMoreData()
                }
            })
        }
        refreshForChat = view.findViewById(R.id.refresh_layout_for_chat)
        progressBar=view.findViewById(R.id.progress_bar_for_chat_log)
        progressBar1=view.findViewById(R.id.progressBar)
        progressBar1.visibility=View.GONE
        progressBar.visibility=View.VISIBLE
        extractComment()
        initScrollListener()
        refreshForChat.setOnRefreshListener {
            refreshForChat.isRefreshing = false
            extractComment()
            initScrollListener()
            }
        refreshForChat.setColorSchemeColors(Color.BLACK)
        submitButton.setOnClickListener {
            val comment=edit_text_comment_making.text.toString()
            val ref= FirebaseDatabase.getInstance().getReference("article/$uid/$key1/comment").push()
            val chatLog=
                ChatLog(currentUid.toString(),username.toString(),profile.toString(), comment)
            ref.setValue(chatLog)
                .addOnFailureListener {
                    Toast.makeText(activity,"${it.message}", Toast.LENGTH_SHORT).show()
                }
            edit_text_comment_making.setText("")

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
        extractComment()
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        refreshForChat.setOnRefreshListener {
            extractComment()                    // refresh your list contents somehow
            refreshForChat.isRefreshing = false   // reset the SwipeRefreshLayout (stop the loading spinner)
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
                        allMessages.size - 1) {
                        //bottom of list!
                        loadMoreData()
                        isLoading = true
                    }
                }
            }
        })
    }
    private fun extractComment() {
        allMessages.clear()
        recyclerView.adapter?.notifyDataSetChanged()
        val ref=FirebaseDatabase.getInstance().getReference("article/$uid/$key1/comment")
        ref.limitToFirst(AllArticleFragment.TOTAL_ITEM_EACH_LOAD)
            .startAt(currentPage * AllArticleFragment.TOTAL_ITEM_EACH_LOAD.toDouble())
        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message=snapshot.getValue(ChatLog::class.java)
                if (!snapshot.hasChildren()) {
                    currentPage--
                }
                if(message!=null){
                    allMessages.add(message)
                    chatLogAdapter.notifyDataSetChanged()
                }
                if(allMessages.isNotEmpty()&&(activity!=null)){
                    chatLogAdapter = chatLogAdapter(activity as Context, allMessages as ArrayList<ChatLog>)
                    recyclerView.adapter=chatLogAdapter
                    recyclerView.layoutManager=layoutManager


                }
                progressBar.visibility=View.GONE
                progressBar1.visibility = RecyclerView.GONE

            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onCancelled(error: DatabaseError) {
            }
        }
        )


    }
    private fun loadMoreData() {
        currentPage++
        extractComment()
        progressBar1.visibility = RecyclerView.VISIBLE
    }
}