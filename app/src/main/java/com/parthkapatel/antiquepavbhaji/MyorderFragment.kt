package com.parthkapatel.antiquepavbhaji

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_cart.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyorderFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyorderFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_myorder, container, false)
    }

    private lateinit var dialog : AlertDialog
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val myorderRecycler = activity?.findViewById<RecyclerView>(R.id.myOrderRecylerView)
        val bbt = AnimationUtils.loadAnimation(context,R.anim.rtl)
        myorderRecycler?.startAnimation(bbt)

        startLoadingDialog()
        val arrayList1 = ArrayList<add_place_order>()
        val sharedPref: SharedPreferences =
            this.context?.getSharedPreferences("Login", Context.MODE_PRIVATE) ?: return
        val useruid = sharedPref.getString("User_id", "").toString()
        val myRef: Query = FirebaseDatabase.getInstance().getReference("users/order/").orderByChild("order_user_id").equalTo(useruid)
        val lm= LinearLayoutManager(context, RecyclerView.VERTICAL,false)
        if (myorderRecycler != null) {
            myorderRecycler.layoutManager=lm
        }


        val ad= context?.let { Add_MyOrder_adapter(arrayList1, it) }
        myRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

               for (ds in dataSnapshot.children) {
                    arrayList1.add(ds.getValue(add_place_order::class.java)!!)
                    val orderid = ds.child("order_id").value.toString()
                    val date = ds.child("date").value.toString()
                    val time = ds.child("time").value.toString()
                    val name = ds.child("name").value.toString()
                    val status = ds.child("status").value.toString()
                    val total = ds.child("total").value.toString()

                }
                myorderRecycler?.adapter?.notifyDataSetChanged()
                myorderRecycler?.adapter=ad
                dismissDialog()

            }
            override fun onCancelled(error: DatabaseError) {
            }

        })


    }


    fun startLoadingDialog(){
        var builder = AlertDialog.Builder(this.activity)
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