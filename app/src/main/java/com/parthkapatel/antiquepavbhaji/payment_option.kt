package com.parthkapatel.antiquepavbhaji

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.widget.RadioButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_payment_option.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class payment_option : AppCompatActivity() {

    private lateinit var dialog : AlertDialog
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_option)


        payment_back.setOnClickListener{
            super.onBackPressed()
        }

        btnorderplace.setOnClickListener { view ->
            startLoadingDialog()
            val intent = intent
            val name = intent.getStringExtra("name")
            val address = intent.getStringExtra("address")
            val itemArray = intent.getStringExtra("itemArray")
            val qtyArray = intent.getStringExtra("itemQty")
            val rsArray = intent.getStringExtra("itemRs")
            val total = intent.getStringExtra("total")
            val checkedID = rdbtngroup.checkedRadioButtonId
            val radiobutton = findViewById<RadioButton>(checkedID)
            val selectedRadio = radiobutton.text.toString()
            val current = LocalDateTime.now()
            val dateFormate = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val timeFormate = DateTimeFormatter.ofPattern("HH:mm")
            val date = current.format(dateFormate)
            val time = current.format(timeFormate)

           val database = FirebaseDatabase.getInstance()
            val myRef = database.getReference("users/order/")
            val sharedPref: SharedPreferences =
                this.getSharedPreferences("Login", Context.MODE_PRIVATE)
            val useruid = sharedPref.getString("User_id", "").toString()
            val key = myRef.push().key



            myRef.child("$key").setValue(key?.let { add_place_order(it,useruid,selectedRadio,name,address,itemArray,qtyArray,rsArray,total,"Pending",date,time) }).addOnCompleteListener{
                dismissDialog()
                Snackbar.make(
                    view,
                    "Order Placed Successfully",
                    Snackbar.LENGTH_LONG
                ).setAction("Action", null).show()
                val handler = Handler()
                handler.postDelayed({

                    startActivity(Intent(this,MainScreen::class.java))
                }, 1000)

            }.addOnFailureListener{
                Snackbar.make(
                    view,
                    "Something Wrong",
                    Snackbar.LENGTH_LONG
                ).setAction("Action", null).show()
                dismissDialog()
            }

        }
    }

    private fun startLoadingDialog(){
        val builder = AlertDialog.Builder(this)
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