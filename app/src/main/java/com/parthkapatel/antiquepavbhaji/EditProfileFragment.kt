package com.parthkapatel.antiquepavbhaji

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.PointerIcon
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.nav_header_main.*


class EditProfileFragment : Fragment(){


    private lateinit var dialog : AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }
    private val myAuth =  FirebaseAuth.getInstance().currentUser
    var user = myAuth?.phoneNumber?.substring(3).toString()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //Hide Cart Image And CartBadge
        var cartimage = activity?.findViewById<ImageView>(R.id.cartImage)
        var cartimagebadge = activity?.findViewById<TextView>(R.id.counterbadgeCart)
        if (cartimage != null) {
            cartimage.visibility = View.INVISIBLE
            if (cartimagebadge != null) {
                cartimagebadge.visibility = View.INVISIBLE
            }
        }

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

        btnEdtUpdate.setOnClickListener {
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
               startActivity(Intent(context,MainScreen::class.java))
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
                   val manager: FragmentManager = (context as AppCompatActivity).supportFragmentManager
                    val fragmentTransaction = manager.beginTransaction()
                    fragmentTransaction.replace(R.id.homeframe, MainScreenFragment()).addToBackStack(null)
                    fragmentTransaction.commit()
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
        var builder = AlertDialog.Builder(context)
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