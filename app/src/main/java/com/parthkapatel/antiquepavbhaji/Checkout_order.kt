package com.parthkapatel.antiquepavbhaji

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class Checkout_order : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        this.actionBar?.title  = "Checkout Order"
        this.actionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.activity_checkout_order)

    }
}