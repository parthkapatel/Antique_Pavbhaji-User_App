package com.parthkapatel.antiquepavbhaji

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {


    lateinit var myAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        myAuth = FirebaseAuth.getInstance()
       /* Handler().postDelayed(
            {
                startActivity(Intent(this,Login::class.java))
                finish()
            },3000)*/

    }

    override fun onStart() {
        super.onStart()
        if (myAuth.currentUser == null) {
            Handler().postDelayed(
                {
                    startActivity(Intent(this,Login::class.java))
                    finish()
                },3000)
        }else {
            Handler().postDelayed(
                {
                    startActivity(Intent(this,MainScreen::class.java))
                    finish()
                },3000)
        }
    }


}