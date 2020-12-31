package com.example.myapplication.activity

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.myapplication.R
import com.example.myapplication.adapter.EndlessRecyclerOnScrollListener
import com.example.myapplication.adapter.ReplyCommentAdapter
import com.example.myapplication.adapter.chatLogAdapter
import com.example.myapplication.fragment.AllArticleFragment
import com.example.myapplication.model.ChatLog
import com.example.myapplication.util.ConnectionManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_reply.*
import kotlinx.android.synthetic.main.activity_user_proflie.*

//class ReplyActivity : AppCompatActivity() {
//    lateinit var recyclerView:RecyclerView
//    lateinit var progressBar:ProgressBar
//    var uid:String?=null
//    private var currentUid:String?=null
//    private var key1:String?=null
//    var isLoading = false
//    private  lateinit var progressBar1:ProgressBar
//    private var currentPage = 0
//    private lateinit var  refreshForChat: SwipeRefreshLayout
//    private lateinit var chatLogAdapter:chatLogAdapter
//    private lateinit var layoutManager: RecyclerView.LayoutManager
//    lateinit var replyCommentAdapter: ReplyCommentAdapter
//    var allMessages = mutableListOf<ChatLog>()
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_reply)
//        val username =intent.getStringExtra("username")
//        username_for_chat_log.text=username
//        val comment=intent.getStringExtra("message")
//        comment_for_chat_log.text=comment
//        val profile=intent.getStringExtra("profile")
//        Picasso.get().load(profile).into(profile_of_chat_log)
//        recyclerView=findViewById(R.id.recycler_view_for_of_reply)
//        progressBar=findViewById(R.id.progress_bar_for_chat_log)
//        if(!(ConnectionManager().checkConnectivity(this))) {
//            val snackBar = Snackbar.make(recyclerView, "No Internet Connection Is Available",
//                Snackbar.LENGTH_LONG
//            ).setAction("Action", null)
//            snackBar.setActionTextColor(Color.BLUE)
//            val snackBarView = snackBar.view
//            snackBarView.setBackgroundColor(Color.BLACK)
//            snackBar.show()
//            progressBar.visibility= View.GONE
//        }
//        else{
//            progressBar.visibility= View.VISIBLE
//        }
//
//        replyCommentAdapter = ReplyCommentAdapter(
//            this,
//            allMessages as ArrayList<ChatLog>)
//        recyclerView.adapter = replyCommentAdapter
//        recyclerView.layoutManager = layoutManager
//        recyclerView.setOnScrollListener(object :
//            EndlessRecyclerOnScrollListener(layoutManager as LinearLayoutManager) {
//            override fun onLoadMore(current_page: Int) {
//                loadMoreData()
//            }
//        })
//
//    }
//    private fun extractComment() {
//        allMessages.clear()
//        recyclerView.adapter?.notifyDataSetChanged()
//        val ref= FirebaseDatabase.getInstance().getReference("article/$uid/$key1/comment")
//        ref.limitToFirst(AllArticleFragment.TOTAL_ITEM_EACH_LOAD)
//            .startAt(currentPage * AllArticleFragment.TOTAL_ITEM_EACH_LOAD.toDouble())
//        ref.addChildEventListener(object: ChildEventListener {
//            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//                val message=snapshot.getValue(ChatLog::class.java)
//                if (!snapshot.hasChildren()) {
//                    currentPage--
//                }
//                if(message!=null){
//                    allMessages.add(message)
//                    chatLogAdapter.notifyDataSetChanged()
//                }
//                if(allMessages.isNotEmpty()&&(activity!=null)){
//                    chatLogAdapter = chatLogAdapter(activity as Context, allMessages as ArrayList<ChatLog>)
//                    recyclerView.adapter=chatLogAdapter
//                    recyclerView.layoutManager=layoutManager
//
//
//                }
//                progressBar.visibility=View.GONE
//                progressBar1.visibility = RecyclerView.GONE
//
//            }
//            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
//            }
//            override fun onChildRemoved(snapshot: DataSnapshot) {
//
//            }
//            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
//            }
//            override fun onCancelled(error: DatabaseError) {
//            }
//        }
//        )
//
//
//    }
//    private fun loadMoreData() {
//        currentPage++
//        extractComment()
//        progressBar1.visibility = RecyclerView.VISIBLE
//    }
//}