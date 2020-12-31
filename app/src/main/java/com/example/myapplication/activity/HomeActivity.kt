package com.example.myapplication.activity


import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.fragment.AllArticleFragment
import com.example.myapplication.fragment.FavouriteArticleFragment
import com.example.myapplication.fragment.FollowerArticleFragment
import com.example.myapplication.fragment.UserArticalFragment
import com.example.myapplication.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_home.*


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class HomeActivity : AppCompatActivity(){
    companion object {
        var currentUser: User? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        fetchCurrentUser()
        setSupportActionBar(toolbar_for_home)
        verifyUserIsLoggedIn()

        bottom_navigation_view.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.follower -> {
                    followerFragmentOpen()
                    return@setOnNavigationItemSelectedListener true

                }
                R.id.world -> {
                    val fragment = AllArticleFragment()
                    supportActionBar?.title = "Home"
                    supportFragmentManager.beginTransaction().replace(R.id.container, fragment)
                        .commit()
                    return@setOnNavigationItemSelectedListener true

                }
                R.id.user_article -> {
                    val fragment = UserArticalFragment()
                    supportActionBar?.title = currentUser?.username
                    supportFragmentManager.beginTransaction().replace(R.id.container, fragment)
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.add_article -> {
                    startActivity(Intent(this, ArticleMakingActivity::class.java))
                    return@setOnNavigationItemSelectedListener true

                }
                R.id.user_fav -> {
                    val fragment = FavouriteArticleFragment()
                    supportActionBar?.title = "Favourite"
                    supportFragmentManager.beginTransaction().replace(R.id.container, fragment)
                        .commit()
                    return@setOnNavigationItemSelectedListener true

                }
                else -> {
                    val fragment = FollowerArticleFragment()
                    supportActionBar?.title ="Follower"
                    supportFragmentManager.beginTransaction().replace(R.id.container, fragment)
                        .commit()
                    return@setOnNavigationItemSelectedListener  true

                }
            }

        }
        if (savedInstanceState == null) {
            followerFragmentOpen()
        }
        }

    private fun followerFragmentOpen() {
        val fragment = FollowerArticleFragment()
        supportActionBar?.title ="Your Feed"
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment)
            .commit()
        

    }
    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)
                currentUser = user

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
    private  fun verifyUserIsLoggedIn() {
      val uid = FirebaseAuth.getInstance().uid
      if (uid == null) {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_for_main, menu)

        return true
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.search -> {
                startActivity(Intent(this, SearchActivity::class.java))
            }
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            R.id.proflie -> {
                val intent = Intent(this, UserProflie::class.java)
                startActivity(intent)
            }


        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        when(supportFragmentManager.findFragmentById(R.id.container)){
            !is FollowerArticleFragment -> followerFragmentOpen()
            else->super.onBackPressed()
        }
    }



}
