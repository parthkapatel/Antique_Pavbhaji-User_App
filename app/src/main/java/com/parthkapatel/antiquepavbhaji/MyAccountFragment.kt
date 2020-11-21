package com.parthkapatel.antiquepavbhaji

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.replace
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_my_account.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MyAccountFragment : Fragment() {

    private lateinit var dialog : AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }

        //Hide Cart Image And CartBadge
        val cartimage = this.activity?.findViewById<ImageView>(R.id.cartImage)
        val cartimagebadge = this.activity?.findViewById<TextView>(R.id.counterbadgeCart)
        if (cartimage != null) {
            cartimage.visibility = View.INVISIBLE
            if (cartimagebadge != null) {
                cartimagebadge.visibility = View.INVISIBLE
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_account, container, false)
    }
    var user = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        startLoadingDialog()

                var myAuth = FirebaseAuth.getInstance().currentUser
                if (myAuth != null) {
                    user = myAuth.phoneNumber?.substring(3).toString()
                    val query2: Query =
                        FirebaseDatabase.getInstance().reference.child("users/profile/$user/")


                    query2.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {

                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            MyAcclblname.text = dataSnapshot.child("name").value.toString()
                            MyAcclblemail.text = dataSnapshot.child("email").value.toString()
                            MyAcclblphone.text = dataSnapshot.child("phone").value.toString()
                            MyAcclbladdress.text = dataSnapshot.child("address").value.toString()
                            dismissDialog()
                        }
                    })

                    val sharedPref: SharedPreferences =
                        activity?.getSharedPreferences("Login", Context.MODE_PRIVATE) ?: return
                    val useruid = sharedPref.getString("User_id", "").toString()
                    val query: Query = FirebaseDatabase.getInstance().getReference("users/order/").orderByChild("order_user_id").equalTo(useruid)

                    query.addValueEventListener(object : ValueEventListener {
                        var expense = 0
                        var order = 0
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (ds in dataSnapshot.children) {
                                expense += Integer.parseInt(ds.child("total").value.toString())
                               order += 1

                            }
                            txtmyAccExpense.text = expense.toString()
                            txtmyAccOrder.text = order.toString()
                            dismissDialog()
                        }
                        override fun onCancelled(error: DatabaseError) {
                        }

                    })
                }

        btnMyAccedtpr.setOnClickListener {

            startActivity(Intent(context,EditProfile::class.java))

        }
        BtnMyAccedtRmv.setOnClickListener {it->
            //startLoadingDialog()
            var user = FirebaseAuth.getInstance().currentUser
            /* val sharedPref: SharedPreferences =
                 this.context?.getSharedPreferences("Login", Context.MODE_PRIVATE) ?: return@setOnClickListener
             val useruid = sharedPref.getString("User_id", "").toString()
             val myRef: Query = FirebaseDatabase.getInstance().getReference("users/addcart/").orderByChild("cart_User_Uid").equalTo(useruid)
             val myRefCart = FirebaseDatabase.getInstance().getReference("users/addcart/")
             myRef.addValueEventListener(object : ValueEventListener {
                 override fun onDataChange(dataSnapshot: DataSnapshot) {
                     for (ds in dataSnapshot.children) {
                         var cart_id = ds.key.toString()
                         for (dsIn in dataSnapshot.child(cart_id).children){
                             if(ds.child("cart_User_Uid").value.toString() == useruid)
                             {

                                 val cartuid = ds.child("cart_Uid").value.toString()
                                 myRefCart.child(cartuid).removeValue().addOnCompleteListener {
                                     Snackbar.make(
                                         view,
                                         " removed Data from your cart",
                                         Snackbar.LENGTH_LONG
                                     ).show()
                                 }
                             }
                         }
                         //if(ds.child("cart_User_Uid").value.toString() == userid)

                     }
                 }
                 override fun onCancelled(error: DatabaseError) {
                 }
             })*/

            user?.delete()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this.context,Login::class.java))
                    activity?.finish()
                    Snackbar.make(
                        view,
                        "Account Deleted",
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