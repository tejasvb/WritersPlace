package com.example.myapplication.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.fragment.UserArticalFragment
import com.example.myapplication.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_user_proflie.*
import kotlinx.android.synthetic.main.activity_user_proflie.id_Image
import kotlinx.android.synthetic.main.activity_user_proflie.show_follower
import kotlinx.android.synthetic.main.activity_user_proflie.show_to_following
import java.util.*


@Suppress("DEPRECATION")
class UserProflie : AppCompatActivity() {

    private val currentUser=HomeActivity.currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_proflie)
        Picasso.get().load(currentUser?.profileImageUrl).into(id_Profile_Image)
        Picasso.get().load(currentUser?.profileImageUrl).into(id_Profile_Image_button)
        Picasso.get().load(currentUser?.background).into(id_Image)
        Picasso.get().load(currentUser?.background).into(id_Image_done)
        id_fullName_TextView.text = currentUser?.username
        id_fullName_EditView.setText(currentUser?.username)
        id_description_TextView.text = currentUser?.description
        id_description_EditView.setText(currentUser?.description)
        val fragment = UserArticalFragment()
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout_for_current_user,
            fragment)
            .commit()
        val refForm=FirebaseDatabase.getInstance().getReference("/users/${currentUser?.uid}/following")
        refForm.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                show_to_following.text = snapshot.childrenCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        val refto=FirebaseDatabase.getInstance().getReference("/users/${currentUser?.uid}/follower")
        refto.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                show_follower.text = snapshot.childrenCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        image_view_for_edit.setOnClickListener {
            id_fullName_TextView.visibility= View.GONE
            image_view_for_edit.visibility= View.GONE
            image_view_for_edit_done.visibility=View.VISIBLE
            id_Profile_Image_button.visibility= View.VISIBLE
            id_fullName_EditView.visibility=View.VISIBLE
            id_Image_done.visibility=View.VISIBLE
            id_description_TextView.visibility=View.GONE
            id_description_EditView.visibility=View.VISIBLE
        }
        id_Profile_Image_button.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
        image_view_for_edit_done.setOnClickListener {
            performRegister()
            closeKeyboard()
            id_fullName_TextView.visibility= View.VISIBLE
            image_view_for_edit.visibility=View.VISIBLE
            image_view_for_edit_done.visibility=View.GONE
            id_Profile_Image_button.visibility= View.GONE
            id_fullName_EditView.visibility=View.GONE
            id_Image_done.visibility=View.GONE
            id_Image.visibility=View.VISIBLE
            id_description_TextView.visibility=View.VISIBLE
            id_description_EditView.visibility=View.GONE

        }
        id_Image_done.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

    }
    private fun closeKeyboard() {
        val view=this.currentFocus
        if(view!=null){
            val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
    private var selectedPhotoUri: Uri? = null
    private var selectedPhotoUri1: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            id_Profile_Image_button.visibility= View.GONE
            id_Profile_Image.setImageBitmap(bitmap)
        }
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri1 = data.data
            id_Image_done.visibility= View.GONE
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri1)
            id_Image.setImageBitmap(bitmap)
        }
    }
    private fun performRegister(){
           if(image_view_for_edit_done.toString().isEmpty()) {
               Toast.makeText(this, "please Enter username", Toast.LENGTH_SHORT).show()
               return
           }
           else {
               uploadProfileToFirebaseStorage()


            }
    }
    private fun uploadProfileToFirebaseStorage() {
        if (selectedPhotoUri == null){
            currentUser?.profileImageUrl?.let { uploadImageToFirebaseStorage(it) }
        }

        else{
            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images/users/$filename")

            ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {

                    ref.downloadUrl.addOnSuccessListener {
                        uploadImageToFirebaseStorage(it.toString())
                    }
                }
            val ref1=
                currentUser?.profileImageUrl?.let { FirebaseStorage.getInstance().getReferenceFromUrl(it) }
            ref1?.delete()

        }



    }

    private fun uploadImageToFirebaseStorage(toString: String) {
        if (selectedPhotoUri1 == null){
            currentUser?.profileImageUrl?.let { saveUserToFirebaseDatabase(toString,it) }
        }
        else{
            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images/users/$filename")

            ref.putFile(selectedPhotoUri1!!)
                .addOnSuccessListener {

                    ref.downloadUrl.addOnSuccessListener {
                        saveUserToFirebaseDatabase(toString,it.toString())
                    }
                }
            val ref1=
                currentUser?.profileImageUrl?.let { FirebaseStorage.getInstance().getReferenceFromUrl(it) }
            ref1?.delete()

        }


    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String,background:String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, id_fullName_EditView.text.toString(), profileImageUrl,background,id_description_EditView.text.toString())
        accessData(profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {

                val intent= Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)


            }
            .addOnFailureListener {
            }
    }
    private fun accessData(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/article/$uid")
        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                editUserData(snapshot.key,profileImageUrl)
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
    private fun editUserData(key: String?, profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/article/$uid/$key/profileImageUrl")
        ref.setValue(profileImageUrl)
        val ref1 = FirebaseDatabase.getInstance().getReference("/article/$uid/$key/username")
        ref1.setValue( id_fullName_EditView.text.toString())


    }
}