package com.parthkapatel.antiquepavbhaji

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main_screen.*
import kotlinx.android.synthetic.main.fragment_main_screen.*
import java.util.prefs.Preferences


class MainScreenFragment : Fragment() {

    private lateinit var dialog : AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        var cartimage = activity?.findViewById<ImageView>(R.id.cartImage)
        if (cartimage != null) {
            cartimage.visibility = View.VISIBLE
        }
        cartBadgeCounter()
        val bbt = AnimationUtils.loadAnimation(context,R.anim.bbt)
        mainScreenRecycler.startAnimation(bbt)
       startLoadingDialog()

                val arrayList = ArrayList<add_category>()
                var myRef: DatabaseReference =
                    FirebaseDatabase.getInstance().getReference("category")
                var lm = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                mainScreenRecycler.layoutManager = lm
                var ad = context?.let { Add_Category_adapter(arrayList, it) }
                myRef.addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        arrayList.clear()
                        for (ds in dataSnapshot.children) {
                            arrayList.add(ds.getValue(add_category::class.java)!!)
                        }
                        mainScreenRecycler?.adapter?.notifyDataSetChanged()
                        mainScreenRecycler.adapter = ad
                       dismissDialog()
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })

    }

    override fun onResume() {
        super.onResume()
        cartBadgeCounter()
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

     fun cartBadgeCounter(){
        //Cart Badge Value
        var main = activity?.findViewById<TextView>(R.id.counterbadgeCart)
        var counter = 0
        val sharedPref: SharedPreferences =
            this.context?.getSharedPreferences("Login", Context.MODE_PRIVATE) ?: return
        var useruid = sharedPref.getString("User_id", "").toString()

        val myRef = FirebaseDatabase.getInstance().getReference("users/addcart/").orderByChild("cart_User_Uid").equalTo(useruid)
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(ds in dataSnapshot.children){

                    counter += 1
                }
                if(counter == 0)
                {
                    if (main != null) {
                        main.visibility = View.INVISIBLE
                    }
                }
                else{
                    if (main != null) {
                        main.text = counter.toString()
                        main.visibility = View.VISIBLE
                    }

                }
            }
        })
    }
}