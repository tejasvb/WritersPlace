package com.example.myapplication.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import com.example.myapplication.R
import com.example.myapplication.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

@Suppress("DEPRECATION")
class RegisterActivity : AppCompatActivity() {
  private val  emailPattern: String
      get() = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    lateinit var progressBar:ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        progressBar=findViewById(R.id.progress_bar_for_register)
        already_have_account_log_in.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        profile_button_for_registration.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
        sign_in_button_for_registration.setOnClickListener {
            progressBar.visibility= View.VISIBLE
     performRegister()
            closeKeyboard()
        }

    }
    private fun closeKeyboard() {
        val view=this.currentFocus
        if(view!=null){
            val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
    private fun performRegister(){
        when {
            username_for_register.text.toString().isEmpty() -> {
                Toast.makeText(this,"please Enter username", Toast.LENGTH_SHORT).show()
                progressBar.visibility= View.GONE
                return
            }

            email_for_registration.text.toString().isEmpty() -> {
                Toast.makeText(this,"please Enter email", Toast.LENGTH_SHORT).show()
                progressBar.visibility= View.GONE
                return
            }
            password_for_registration.text.toString().isEmpty() -> {
                Toast.makeText(this,"please enter password", Toast.LENGTH_SHORT).show()
                progressBar.visibility= View.GONE
                return
            }
            !email_for_registration.text.toString().trim().matches(emailPattern.toRegex())-> {
                Toast.makeText(this,"Email is badly formatted ", Toast.LENGTH_SHORT).show()
                progressBar.visibility= View.GONE
                return
            }

            else -> {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword( email_for_registration.text.toString(), password_for_registration.text.toString())
                    .addOnCompleteListener {
                        Toast.makeText(this,"sign in successfully", Toast.LENGTH_SHORT).show()
                        uploadImageToFirebaseStorage()

                    }
                    .addOnFailureListener {
                        Toast.makeText(this,"${it.message}", Toast.LENGTH_SHORT).show()
                        progressBar.visibility= View.GONE
                    }
            }
        }
    }
    private var selectedPhotoUri: Uri? = null
    private var selectedPhotoUri1: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            selectedPhotoUri1 =Uri.parse("android.resource://" + packageName + "/" + R.mipmap.ic_image_round)
            select_photo_image_view_register.setImageBitmap(bitmap)


        }

    }
    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) {
            progressBar.visibility= View.GONE
            return
        }
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/users/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {

                ref.downloadUrl.addOnSuccessListener {
                    uploadImageBackground(it.toString())
                }
            }
            .addOnFailureListener {
            }

    }

    private fun uploadImageBackground(profileImageUrl: String) {
        if (selectedPhotoUri1 == null) {
            Toast.makeText(this,"Some error occur",Toast.LENGTH_SHORT).show()
            progressBar.visibility= View.GONE
            return
        }
        val filename1 = UUID.randomUUID().toString()
        val ref1 = FirebaseStorage.getInstance().getReference("/images/users/$filename1")

        ref1.putFile(selectedPhotoUri1!!)
            .addOnSuccessListener {

                ref1.downloadUrl.addOnSuccessListener {
                    saveUserToFirebaseDatabase(profileImageUrl,it.toString())
                }
            }
            .addOnFailureListener {
            }

    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String,background: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, username_for_register.text.toString(), profileImageUrl,background,"Description")

        ref.setValue(user)
            .addOnSuccessListener {
                progressBar.visibility= View.GONE
                val intent= Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)


            }
            .addOnFailureListener {
            }
    }


}