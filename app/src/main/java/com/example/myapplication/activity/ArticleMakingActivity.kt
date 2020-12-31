package com.example.myapplication.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ProgressBar

import android.widget.Toast
import com.example.myapplication.R
import com.example.myapplication.activity.HomeActivity.Companion.currentUser
import com.example.myapplication.model.UserNote
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_note_making.*
import java.util.*

@Suppress("DEPRECATION")
class ArticleMakingActivity : AppCompatActivity() {
    var note:String?=null
    var uid:String?=null
    var title:String?=null
    private var images:String?=null
    private var filename:String?=null
    private lateinit var progressBar: ProgressBar
    var key:String?=null
    private var selectedPhotoUri: Uri? = null
    private var fileName:String? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_making)
        progressBar=findViewById(R.id.progressBar_for_home)
        filename=intent.getStringExtra("filename")
        key=intent.getStringExtra("key")
        fileName = if(filename!=null){
            filename.toString()
        } else{
            UUID.randomUUID().toString()
        }
        checkWhatherNullornot()
        button_for_selecting_Image.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
        submit_notes.setOnClickListener {
            uploadImageToDatabase()
            progressBar.visibility= View.VISIBLE
        }
    }

    private fun checkWhatherNullornot() {
        note=intent.getStringExtra("notes")
        uid=intent.getStringExtra("uid")
        title=intent.getStringExtra("title")
        images=intent.getStringExtra("image")
        key=intent.getStringExtra("key")
        title_of_artical.setText(title)
        edit_text_add_notes.setText(note)
        Picasso.get().load(images).into(image_view_for_article)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
           image_view_for_article.setImageBitmap(bitmap)
          button_for_selecting_Image.alpha = 0f

        }
    }

    private fun uploadImageToDatabase(){
        if(selectedPhotoUri==null){
            currentUser?.profileImageUrl?.let { saveData(it) }
        }
        else{
            val ref=FirebaseStorage.getInstance().getReference("/images/article/$fileName")
            ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener {
                        saveData(it.toString())
                    }
                }
                .addOnFailureListener {

                }
            val ref1=
                currentUser?.profileImageUrl?.let { FirebaseStorage.getInstance().getReferenceFromUrl(it) }
            ref1?.delete()
        }




    }
    private fun saveData(Photouri: String) {
        progressBar.visibility= View.GONE
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Save File")
        builder.setMessage("To Upload this article")
        builder.setIcon(R.drawable.ic_save_foreground)

        //performing positive action
        builder.setPositiveButton("Yes"){ _, _ ->
            val ref:DatabaseReference
            ref = if(key!=null){
                FirebaseDatabase.getInstance().getReference("/article/$uid/$key")
            } else{
                val uid=FirebaseAuth.getInstance().uid?:""
                FirebaseDatabase.getInstance().getReference("/article/$uid").push()
            }

            val user= currentUser ?: return@setPositiveButton

            val usernote = fileName?.let {
                UserNote(user.uid,
                    user.username,
                    user.profileImageUrl,title_of_artical.text.toString(),edit_text_add_notes.text.toString(), Photouri, ref.key.toString(),
                    it)
            }

            ref.setValue(usernote)
                .addOnSuccessListener {
                    val intent= Intent(this, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Toast.makeText(this,"${it.message}",Toast.LENGTH_SHORT).show()
                }
        }
        builder.setNegativeButton("No"){ _, _ ->
    builder.setCancelable(true)
        }
        if(!isFinishing){
            progressBar.visibility= View.VISIBLE
            val alertDialog: AlertDialog = builder.create()
            // Set other dialog properties
            alertDialog.setCancelable(false)
            alertDialog.show()
            progressBar.visibility= View.GONE
        }
    }

}