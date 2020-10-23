package com.parthkapatel.antiquepavbhaji

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*
import java.util.concurrent.TimeUnit


class Login : AppCompatActivity() {

    lateinit var callbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var myAuth : FirebaseAuth
    var verificationId = ""
    private lateinit var activity : Activity
    private lateinit var dialog : AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)



        myAuth = FirebaseAuth.getInstance()

        btnlogin.setOnClickListener {

            if(txtlblphone.text.isEmpty())
            {
                Snackbar.make(
                    getWindow().getDecorView().getRootView(),
                    "Please Enter 10 Digit Phone Number",
                    Snackbar.LENGTH_LONG
                ).setAction("Action", null).show()
            }else{
                startLoadingDialog()
                verify()
            }
        }

        btnAuthOTP.setOnClickListener {
            if(txtlblphone.text.isEmpty())
            {
                Snackbar.make(
                    getWindow().getDecorView().getRootView(),
                    "Please Enter 10 Digit Phone Number & Click Send OTP",
                    Snackbar.LENGTH_LONG
                ).setAction("Action", null).show()
            }else if(txtlblotp.text.isEmpty())
            {

                Snackbar.make(
                    getWindow().getDecorView().getRootView(),
                    "Please Enter 6 Digit OTP Number",
                    Snackbar.LENGTH_LONG
                ).setAction("Action", null).show()
            }else{
                startLoadingDialog()
                authenticate()
            }
        }
    }



    private fun verificationCallback(){
            callbacks = object: PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signIn(credential)
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    dismissDialog()
                    Snackbar.make(
                        getWindow().getDecorView().getRootView(),
                        p0.message.toString(),
                        Snackbar.LENGTH_LONG
                    ).setAction("Action", null).show()
                }

                override fun onCodeSent(verfication: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(verfication, p1)
                    verificationId = verfication
                    dismissDialog()
                    Snackbar.make(
                        getWindow().getDecorView().getRootView(),
                        "OTP Sent Successfully",
                        Snackbar.LENGTH_LONG
                    ).setAction("Action", null).show()

                }
            }

        }

    private fun verify(){

            verificationCallback()

            var phoneNumber = txtlblphone.text.toString()
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91$phoneNumber", // Phone number to verify
                60, // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                Activity(), // Activity (for callback binding)
                callbacks
            ) // OnVerificationStateChangedCallbacks

    }

    private fun authenticate () {

        val verifiNo = txtlblotp.text.toString()
        val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, verifiNo)

        signIn(credential)

    }

    private fun signIn(credential: PhoneAuthCredential) {
        myAuth.signInWithCredential(credential)
            .addOnCompleteListener{
                    task: Task<AuthResult> ->
                if(task.isSuccessful) {

                    var phoneNumber = txtlblphone.text.toString()
                    val query: Query =
                        FirebaseDatabase.getInstance().reference.child("users/profile/$phoneNumber/")

                    query.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                        }
                        override fun onDataChange(ds: DataSnapshot) {
                                // 1 or more Item exist"
                //Another Activity
                            var userid = ds.child("user_UID").value.toString()
                            val sharedPref: SharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE)
                            var edt:SharedPreferences.Editor=sharedPref.edit()
                            edt.putString("User_id", userid)
                            edt.commit()
                            dismissDialog()
                            startActivity(Intent(this@Login, MainScreen::class.java))
                            finish()
                        }
                    })
                }
                else{
                    dismissDialog()
                    Snackbar.make(
                        window.decorView.rootView,
                        task.exception?.message.toString(),
                        Snackbar.LENGTH_LONG
                    ).setAction("Action", null).show()
                    var message = "Something is wrong, we will fix it soon..."

                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        message = "Invalid code entered..."
                    }

                    val snackbar = Snackbar.make(
                        window.decorView.rootView,
                        message,
                        Snackbar.LENGTH_LONG
                    )

                    snackbar.show()
                    txtlblotp.text.clear()
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
