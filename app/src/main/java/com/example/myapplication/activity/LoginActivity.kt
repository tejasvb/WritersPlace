package com.example.myapplication.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    val  emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        progressBar=findViewById(R.id.progress_bar_for_login)
        sign_in_button_for_login.setOnClickListener {
            progressBar.visibility= View.VISIBLE
            closekeyboard()
            log_in()
        }
      dont_have_an_accoount.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
//forgot_password.setOnClickListener {
//    when {
//        email_for_login.text.toString().isEmpty() -> {
//            Toast.makeText(this, "please Enter email", Toast.LENGTH_SHORT).show()
//            progressBar.visibility= View.GONE
//            return
//        }
//        password_for_login.text.toString().isEmpty() -> {
//            Toast.makeText(this, "please enter password", Toast.LENGTH_SHORT).show()
//            progressBar.visibility= View.GONE
//            return
//        }
//        !email_for_login.text.toString().trim().matches(emailPattern.toRegex()) -> {
//            Toast.makeText(this, "Email is badly formatted ", Toast.LENGTH_SHORT).show()
//            progressBar.visibility= View.GONE
//            return
//        }
//        else-{
//
//            FirebaseAuth.getInstance().sendPasswordResetEmail("user@example.com")
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        Toast.makeText(this,"Email")
//                    }
//                }
//        }
//    }
//}
    }

    private fun closekeyboard() {
         val view=this.currentFocus
        if(view!=null){
            val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun log_in(){
        when {
            email_for_login.text.toString().isEmpty() -> {
                Toast.makeText(this, "please Enter email", Toast.LENGTH_SHORT).show()
                progressBar.visibility= View.GONE
                return
            }
            password_for_login.text.toString().isEmpty() -> {
                Toast.makeText(this, "please enter password", Toast.LENGTH_SHORT).show()
                progressBar.visibility= View.GONE
                return
            }
            !email_for_login.text.toString().trim().matches(emailPattern.toRegex()) -> {
                Toast.makeText(this, "Email is badly formatted ", Toast.LENGTH_SHORT).show()
                progressBar.visibility= View.GONE
                return
            }
        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email_for_login.text.toString(),password_for_login.text.toString())
            .addOnSuccessListener {  progressBar.visibility= View.GONE
                Toast.makeText(this,"login successfully", Toast.LENGTH_SHORT).show()
                val intent= Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish() }
            .addOnFailureListener {
                Toast.makeText(this,"${it.message}", Toast.LENGTH_SHORT).show()
                progressBar.visibility= View.GONE
            }

    }
}