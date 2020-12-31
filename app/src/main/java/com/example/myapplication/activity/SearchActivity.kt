package com.example.myapplication.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.example.myapplication.R
import com.example.myapplication.fragment.SearchAuthorFragment
import kotlinx.android.synthetic.main.activity_search.*


class SearchActivity : AppCompatActivity() {
    var search:CharSequence?=null
    private lateinit var simpleSearchView: SearchView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setUpToolBar()


      simpleSearchView = findViewById(R.id.simpleSearchView)
        val searchViewIcon: ImageView? = simpleSearchView.findViewById(androidx.appcompat.R.id.search_mag_icon)
        searchViewIcon?.visibility=View.GONE
        searchViewIcon?.setImageDrawable(null);

        simpleSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                closeKeyBoard()
                return false

            }

            override fun onQueryTextChange(newText: String): Boolean {
                search = newText
                if (search != null) {
                    Log.d("TextSubmit", "$search")
                    val fragmentTransaction: FragmentTransaction =
                        supportFragmentManager.beginTransaction()
                    val fragment = SearchAuthorFragment()
                    val frame = Bundle()
                    frame.putString("message", search?.toString())
                    fragment.arguments = frame
                    fragmentTransaction.replace(R.id.frame_layout_for_searchView, fragment).commit()

                }

                return false
            }
        })
     }



    private fun setUpToolBar(){
        setSupportActionBar(toolbar_for_searchView)

    }
    private fun closeKeyBoard() {
        val view =this.currentFocus
        if (view != null) {
            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}