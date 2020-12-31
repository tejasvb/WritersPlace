package com.example.myapplication.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.R
import com.example.myapplication.fragment.ChatReplayFragment


class ChatReplayActivity : AppCompatActivity() {
    var username:String?=null
    var uid :String?=null
    private var key1:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_replay)
        uid=intent.getStringExtra("uid")
         key1=intent.getStringExtra("key")
        if (savedInstanceState == null) {
            val fragment = ChatReplayFragment()
            val bundle=Bundle()
            bundle.putString("uid",uid.toString())
            bundle.putString("key",key1.toString())
            fragment.arguments=bundle
            supportFragmentManager.beginTransaction().replace(R.id.frame_layout_for_chat_log, fragment)
                .commit()
        }
     }
}