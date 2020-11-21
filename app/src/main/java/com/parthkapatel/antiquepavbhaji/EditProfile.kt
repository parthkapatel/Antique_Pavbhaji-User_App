package com.parthkapatel.antiquepavbhaji

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfile : AppCompatActivity() {

    private lateinit var dialog : AlertDialog
    private val myAuth =  FirebaseAuth.getInstance().currentUser
    var user = myAuth?.phoneNumber?.substring(3).toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)


        var myAuth = FirebaseAuth.getInstance().currentUser
        if (myAuth != null) {
            user = myAuth.phoneNumber?.substring(3).toString()
        }

        var CurrentUserUID : String= ""
        startLoadingDialog()
        val query2: Query =
            FirebaseDatabase.getInstance().reference.child("users/profile/$user/")
        query2.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.childrenCount > 0){
                    txtEdtName.setText(dataSnapshot.child("name").value.toString())
                    txtEdtEmail.setText( dataSnapshot.child("email").value.toString())
                    txtEdtPhone.setText(dataSnapshot.child("phone").value.toString())
                    txtEdtAddress.setText(dataSnapshot.child("address").value.toString())
                    CurrentUserUID = dataSnapshot.child("user_UID").value.toString()
                }
                dismissDialog()
            }
        })
        txtEdtPhone.setText(user)


        btnEdtUpdate.setOnClickListener {view->
            startLoadingDialog()
            var name = txtEdtName.text.toString()
            var email = txtEdtEmail.text.toString()
            var phone = txtEdtPhone.text.toString()
            var address = txtEdtAddress.text.toString()



            var myRef: DatabaseReference = FirebaseDatabase.getInstance().getReference()
            if (CurrentUserUID != "") {
                myRef.child("users").child("profile").child("$user").child("name").setValue(name)
                myRef.child("users").child("profile").child("$user").child("email").setValue(email)
                myRef.child("users").child("profile").child("$user").child("phone").setValue(phone)
                myRef.child("users").child("profile").child("$user").child("address").setValue(address)
                myRef.child("users").child("profile").child("$user").child("user_UID").setValue(CurrentUserUID)
                dismissDialog()
                Snackbar.make(
                    view,
                    "Profile Update Successfully",
                    Snackbar.LENGTH_LONG
                ).setAction("Action", null).show()
                startActivity(Intent(this,MainScreen::class.java))
            } else {
                val key = myRef.push().key
                var myRef2: DatabaseReference =
                    FirebaseDatabase.getInstance().getReference("users/profile/")
                myRef2.child("$user").setValue(key?.let {

                    add_user(it, name, address, email, user)
                }).addOnCompleteListener {
                    dismissDialog()
                    Snackbar.make(
                        view,
                        "Profile Saved Successfully",
                        Snackbar.LENGTH_LONG
                    ).setAction("Action", null).show()
                    startActivity(Intent(this,MainScreen::class.java))
                }.addOnCanceledListener {
                    dismissDialog()
                    Snackbar.make(
                        view,
                        "Something is Wrong",
                        Snackbar.LENGTH_LONG
                    ).setAction("Action", null).show()
                }
            }
        }

    }

    private fun startLoadingDialog(){
        var builder = AlertDialog.Builder(this)
        val inflater : LayoutInflater = this.layoutInflater
        builder.setView(inflater.inflate(R.layout.custom_dialog_progressbar, null))
        builder.setCancelable(false)
        dialog = builder.create()
        dialog.show()
    }

    fun dismissDialog() {
        dialog.dismiss()
    }
}