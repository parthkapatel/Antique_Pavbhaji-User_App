package com.parthkapatel.antiquepavbhaji

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_add_address.*

class Add_address : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.actionBar?.title  = "Add New Address"
        setContentView(R.layout.activity_add_address)

        addressback.setOnClickListener{
            startActivity(Intent(this,MainScreen::class.java))
        }



        btnaddaddress.setOnClickListener {
            val name = findViewById<TextInputEditText>(R.id.txtaddrname)
            val house = findViewById<TextInputEditText>(R.id.txtaddrhouse)
            val road = findViewById<TextInputEditText>(R.id.txtaddrroad)
            val city = findViewById<TextInputEditText>(R.id.txtaddrcity)
            val phone = findViewById<TextInputEditText>(R.id.txtaddrphone)

            val layname = findViewById<TextInputLayout>(R.id.layaddrname)
            val layhouse = findViewById<TextInputLayout>(R.id.layaddrhouse)
            val layroad = findViewById<TextInputLayout>(R.id.layaddrroad)
            val laycity = findViewById<TextInputLayout>(R.id.layaddrcity)
            val layphone = findViewById<TextInputLayout>(R.id.layaddrphone)


                if (name.text.toString().isEmpty())
                    layname.error = "Enter Name"
                else if (name.text.toString().isNotEmpty())
                    layname.isErrorEnabled = false

                if (house.text.toString().isEmpty())
                    layhouse.error = "Enter House no., Building name"
                else if (house.text.toString().isNotEmpty())
                    layhouse.isErrorEnabled = false

                if (road.text.toString().isEmpty())
                    layroad.error = "Enter Road name or Area name"
                else if (road.text.toString().isNotEmpty())
                    layroad.isErrorEnabled = false

                if (city.text.toString().isEmpty())
                    laycity.error = "Enter City"
                else if (city.text.toString().isNotEmpty())
                    laycity.isErrorEnabled = false

                if (phone.text.toString().isEmpty())
                    layphone.error = "Enter Phone number"
                else if (phone.length() < 10 || phone.length() > 10)
                    layphone.error = "Phone number must be in 10 digit!"
                else if (phone.text.toString().isNotEmpty())
                    layphone.isErrorEnabled = false

                if(name.text.toString().isNotEmpty() && house.text.toString().isNotEmpty() && road.text.toString().isNotEmpty() && city.text.toString().isNotEmpty() && phone.text.toString().isNotEmpty() && !(phone.length() < 10 || phone.length() > 10)){
                    val inte = intent
                    val itemName = inte.getStringExtra("itemName")
                    val itemQty = inte.getStringExtra("itemQty")
                    val itemRs = inte.getStringExtra("itemRs")

                    val intent = Intent(this, Confirm_order::class.java)
                    intent.putExtra("Name", name.text.toString())
                    intent.putExtra("house", house.text.toString())
                    intent.putExtra("road", road.text.toString())
                    intent.putExtra("city", city.text.toString())
                    intent.putExtra("phone", phone.text.toString())
                    intent.putExtra("itemName", itemName)
                    intent.putExtra("itemQty", itemQty)
                    intent.putExtra("itemRs", itemRs)
                    startActivity(intent)
                }



        }
    }



}