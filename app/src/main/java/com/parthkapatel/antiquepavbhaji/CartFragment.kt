package com.parthkapatel.antiquepavbhaji

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_cart.*


class CartFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
        //Hide Cart Image And CartBadge
        var cartimage = activity?.findViewById<ImageView>(R.id.cartImage)
        var cartimagebadge = activity?.findViewById<TextView>(R.id.counterbadgeCart)
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
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    private lateinit var dialog : AlertDialog
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        var itemName = ""
        var itemQty = ""
        var itemRs = ""

        val cartRecycler = activity?.findViewById<RecyclerView>(R.id.cartRecyclerID)
        val bbt = AnimationUtils.loadAnimation(context,R.anim.rtl)
        cartRecycler?.startAnimation(bbt)

        startLoadingDialog()
        val arrayList1 = ArrayList<add_cart>()
        val sharedPref: SharedPreferences =
            this.context?.getSharedPreferences("Login", Context.MODE_PRIVATE) ?: return
        val useruid = sharedPref.getString("User_id", "").toString()
        val myRef: Query = FirebaseDatabase.getInstance().getReference("users/addcart/").orderByChild("cart_User_Uid").equalTo(useruid)
        val lm= LinearLayoutManager(context, RecyclerView.VERTICAL,false)
        if (cartRecycler != null) {
            cartRecycler.layoutManager=lm
        }


        val totalprice = this.activity?.findViewById<TextView>(R.id.total_price)
        val ad= context?.let { Add_Cart_adapter(arrayList1, it) }
        myRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var totalcartrs : Int = 0
                arrayList1.clear()
                var itemName1 = ""
                var itemQty1 = ""
                var itemRs1 = ""
              /*  if(dataSnapshot.childrenCount.toString() == "0"){
                    cart_nodata_image.visibility = View.VISIBLE
                    cart_linearlayout.visibility = View.INVISIBLE
                    cart_framelayout.visibility = View.INVISIBLE
                    dismissDialog()
                }else{*/



                    for (ds in dataSnapshot.children) {
                        arrayList1.add(ds.getValue(add_cart::class.java)!!)
                        val rs = ds.child("cart_item_amount").value.toString()
                        val qty = ds.child("cart_item_qty").value.toString()
                        val mul = rs.toInt() * qty.toInt()
                        totalcartrs +=  mul
                        totalprice?.text = "₹$totalcartrs"

                        val itemname = ds.child("cart_item_name").value.toString()

                        if(itemName1 == "" && itemQty1 == "" && itemRs1 == ""){
                            itemName1 = itemname
                            itemQty1 = qty
                            itemRs1 = rs
                        }else if(itemName1 != "" && itemQty1 != "" && itemRs1 != ""){
                            itemName1 = "$itemName1&$itemname"
                            itemQty1 = "$itemQty1&$qty"
                            itemRs1 = "$itemRs1&$rs"
                        }



                    }
                        itemName = itemName1
                        itemQty = itemQty1
                        itemRs = itemRs1
                    cartRecycler?.adapter?.notifyDataSetChanged()
                    cartRecycler?.adapter=ad
                    dismissDialog()
               // }
            }
            override fun onCancelled(error: DatabaseError) {
            }

        })

        btnCartCheckout.setOnClickListener {
            val intent = Intent(context, Add_address::class.java)
            intent.putExtra("itemName", itemName)
            intent.putExtra("itemQty", itemQty)
            intent.putExtra("itemRs", itemRs)
            startActivity(intent)
        }
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