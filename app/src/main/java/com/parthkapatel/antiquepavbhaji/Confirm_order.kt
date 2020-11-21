package com.parthkapatel.antiquepavbhaji

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_confirm_order.*


class Confirm_order : AppCompatActivity() {

    private lateinit var linearLayoutManager: LinearLayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_order)

        val intent = intent
        val name = intent.getStringExtra("Name")
        val house = intent.getStringExtra("house")
        val road = intent.getStringExtra("road")
        val city = intent.getStringExtra("city")
        val phone = intent.getStringExtra("phone")
        val itemName = intent.getStringExtra("itemName")
        val itemQty = intent.getStringExtra("itemQty")
        val itemRs = intent.getStringExtra("itemRs")


        txtcheck_name.text = name.toString()
        txtcheck_addr.text = "$house, $road"
        txtcheck_city.text = city.toString()
        txtcheck_number.text = phone.toString()

        /*Display Data in LinearLayout*/

        val item= itemName.split('&')
        val qty= itemQty.split('&')
        val rs= itemRs.split('&')
        val itemArray : Array<String> = item.toTypedArray()
        val qtyArray : Array<String> = qty.toTypedArray()
        val rsArray : Array<String> = rs.toTypedArray()

        val llvertical = findViewById<LinearLayout>(R.id.aco_linear_vertical)
        var total = 0
        for (i in 0..itemArray.size-1) {
            val linearLayout = LinearLayout(this)
               val textView = TextView(this)
               textView.text = itemArray[i]
               val textView2 = TextView(this)
               textView2.text = rsArray[i]+"x"+qtyArray[i]
               val textView3 = TextView(this)
               textView3.text = (qtyArray[i].toInt() * rsArray[i].toInt()).toString()
                total += (qtyArray[i].toInt() * rsArray[i].toInt())

               val params = LinearLayout.LayoutParams(
                   LinearLayout.LayoutParams.MATCH_PARENT,
                   LinearLayout.LayoutParams.WRAP_CONTENT,1F
               )
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.setPadding(10,10,10,10)
            textView.textSize = 18F
            textView2.textSize = 18F
            textView3.textSize = 18F
            textView.setTextColor(Color.parseColor("#696969"))
            textView2.setTextColor(Color.parseColor("#696969"))
            textView3.setTextColor(Color.parseColor("#696969"))
            textView2.gravity = Gravity.RIGHT
            textView3.gravity = Gravity.CENTER
               textView.layoutParams = params
               textView2.layoutParams = params
               textView3.layoutParams = params
               linearLayout.addView(textView)
               linearLayout.addView(textView2)
               linearLayout.addView(textView3)
               llvertical.addView(linearLayout)
           }
            txtlblCOtotal.text = total.toString()





        if(txtcheck_name.text.isNotEmpty() && txtcheck_addr.text.isNotEmpty() && txtcheck_city.text.isNotEmpty() && txtcheck_number.text.isNotEmpty()){
            linearlayout_address.visibility = View.VISIBLE
        }


        checkout_back.setOnClickListener{
            super.onBackPressed()
        }

        btnchangeaddr.setOnClickListener {
            super.onBackPressed()
        }

        btnconfirm_continue.setOnClickListener {
            val intent = Intent(this, payment_option::class.java)

            val dname = txtcheck_name.text.toString()
            val address = txtcheck_addr.text.toString()+"&"+txtcheck_city.text.toString()+"&"+txtcheck_number.text.toString()
            intent.putExtra("name", dname)
            intent.putExtra("address", address)
            intent.putExtra("itemArray", itemName.toString())
            intent.putExtra("itemQty", itemQty.toString())
            intent.putExtra("itemRs", itemRs.toString())
            intent.putExtra("total", txtlblCOtotal.text.toString())
            startActivity(intent)

        }






    }


}