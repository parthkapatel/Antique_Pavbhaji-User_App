package com.parthkapatel.antiquepavbhaji

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.cart_details_row.view.*
import kotlinx.android.synthetic.main.home_menu_row.view.*
import kotlinx.android.synthetic.main.myorder_data_row.view.*
import java.util.*
import kotlin.collections.ArrayList


data class add_user(var user_UID:String,var name:String,var address:String,var email:String,var phone:String){
    constructor():this("","","","","")
}

data class add_items(var item_Uid:String,var cat_name:String,var item_name:String,var amount:String,var description:String){
    constructor():this("","","","","")
}

data class add_category(var name:String,var image_path:String){
    constructor():this("","")
}

data class add_cart(var cart_Uid:String,var cart_User_Uid:String,var cart_item_Uid:String,var cart_cat_name:String,var cart_item_name:String,var cart_item_amount:String,var cart_item_qty:String){
    constructor() :this("","","","","","","")
}

data class add_place_order(var order_id:String,var order_user_id:String,var payment_method:String,var name:String,var delivery_address:String,var item_array:String,var item_qty_array:String,var item_rs_array:String,var total:String,var status:String,var date:String,var time:String){
    constructor():this("","","","","","","","","","","","")
}

class Add_Category_adapter(private val arrayList: ArrayList<add_category>, val context: Context) :
    RecyclerView.Adapter<Add_Category_adapter.ViewHolder>() {

    class ViewHolder(item_view : View):RecyclerView.ViewHolder(item_view){

        @RequiresApi(Build.VERSION_CODES.O)
        fun bindItems(addCategory: add_category){
            itemView.lblItemName.text = addCategory.name
            val base64String = addCategory.image_path
            val base64Image: String = base64String.split(",").get(1)
            val decodedString: ByteArray = Base64.getDecoder().decode(base64Image)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            itemView.imgCateName.setImageBitmap(decodedByte)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.home_menu_row,parent,false)

        return  ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        holder.bindItems(arrayList[position])
        holder.itemView.setOnClickListener {
            val manager: FragmentManager = (context as AppCompatActivity).supportFragmentManager
            val fragmentTransaction = manager.beginTransaction()
            fragmentTransaction.replace(R.id.homeframe, ProductDetailsFragment(arrayList[position].name)).addToBackStack(null)
            fragmentTransaction.commit()

        }
    }
}

var itemname = ""
var cat_name = ""
var cartid = ""
class Add_Cart_adapter(private val arrayList1: ArrayList<add_cart>, val context: Context) :
    RecyclerView.Adapter<Add_Cart_adapter.ViewHolder>() {


    open class ViewHolder(item_view : View):RecyclerView.ViewHolder(item_view){
        @RequiresApi(Build.VERSION_CODES.O)
        fun bindItems(addCart: add_cart){
            //val userid = addCart.cart_User_Uid
            val itemid = addCart.cart_item_Uid
            cat_name = addCart.cart_cat_name
            itemname = addCart.cart_item_name
            cartid = addCart.cart_Uid

                    val myRef: Query = FirebaseDatabase.getInstance().getReference("users/addcart/$cartid")
                    myRef.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                        }

                        override fun onDataChange(ds: DataSnapshot) {
                            itemView.txtlblCartName.text = ds.child("cart_item_name").value.toString()
                            val qty = ds.child("cart_item_qty").value.toString()
                            val rs =  ds.child("cart_item_amount").value.toString()
                            itemView.txtQtyItem.text = qty
                            itemView.lblCartMRs.text = "$rs x $qty"
                            if(qty != "null" && rs != "null"){
                                itemView.lblCartRs.text = (qty.toInt() * rs.toInt()).toString()
                            }
                        }
                    })
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cart_details_row,parent,false)
        return  ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return arrayList1.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        holder.bindItems(arrayList1[position])
        //Log.d("",holder.itemId.toString())
            holder.itemView.removeBtnCart.setOnClickListener {view ->


                val database = FirebaseDatabase.getInstance()
                val item = holder.itemView.txtlblCartName.text.toString()
                val myRef = database.getReference("users/addcart/")

                val query: Query =
                    FirebaseDatabase.getInstance().reference.child("users/addcart/")
                        .orderByChild("cart_item_name").equalTo(item)
                query.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (ds in dataSnapshot.children) {
                            val cart_uid = ds.child("cart_Uid").value.toString()

                            myRef.child(cart_uid).removeValue().addOnCompleteListener {
                                Snackbar.make(
                                   view,
                                    "$item removed from your cart",
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                })
            }

        holder.itemView.imgPlusQty.setOnClickListener {
            val itemqty = holder.itemView.txtQtyItem.text.toString()

            if(itemqty != "0")
            {
                val item = holder.itemView.txtlblCartName.text.toString()
                var itemid = ""
                var itemq = ""
                val myRef = FirebaseDatabase.getInstance().getReference("users/addcart/").orderByChild("cart_item_name").equalTo(item)
                myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }
                    override fun onDataChange(ds: DataSnapshot) {
                        for(ds in ds.children) {
                            Log.d("jf",ds.toString())
                            val key = ds.key.toString()
                            itemq = ds.child("cart_item_qty").value.toString()
                            val sum = itemq.toInt() + 1
                            FirebaseDatabase.getInstance().getReference("users/addcart/$key/").child("cart_item_qty").setValue((sum).toString())
                        }
                    }
                })

            }
        }

            holder.itemView.imgMinusItem.setOnClickListener {view ->
                val itemqty = holder.itemView.txtQtyItem.text.toString()

                if(itemqty != "0" && itemqty != "1")
                {
                    val item = holder.itemView.txtlblCartName.text.toString()
                    var itemid = ""
                    var itemq = ""
                    val myRef = FirebaseDatabase.getInstance().getReference("users/addcart/").orderByChild("cart_item_name").equalTo(item)
                    myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                        }
                        override fun onDataChange(ds: DataSnapshot) {
                            for(ds in ds.children) {
                                Log.d("jf",ds.toString())
                                val key = ds.key.toString()
                                itemq = ds.child("cart_item_qty").value.toString()
                                val sum = itemq.toInt() - 1
                                FirebaseDatabase.getInstance().getReference("users/addcart/$key/").child("cart_item_qty").setValue((sum).toString())
                            }
                        }
                    })
                }
                else if(itemqty == "1"){
                    val database = FirebaseDatabase.getInstance()
                    val item = holder.itemView.txtlblCartName.text.toString()
                    val myRef = database.getReference("users/addcart/")

                    val query: Query =
                        FirebaseDatabase.getInstance().reference.child("users/addcart/")
                            .orderByChild("cart_item_name").equalTo(item)
                    query.addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onCancelled(error: DatabaseError) {
                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (ds in dataSnapshot.children) {
                                val cart_uid = ds.child("cart_Uid").value.toString()

                                myRef.child(cart_uid).removeValue().addOnCompleteListener {
                                    Snackbar.make(
                                        view,
                                        "$item removed from your cart",
                                        Snackbar.LENGTH_LONG
                                    ).show()

                                }
                            }
                        }
                    })
                }
            }
        }
    }

class Add_MyOrder_adapter(private val arrayList1: ArrayList<add_place_order>, val context: Context) :
    RecyclerView.Adapter<Add_MyOrder_adapter.ViewHolder>() {


    open class ViewHolder(item_view: View) : RecyclerView.ViewHolder(item_view) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bindItems(addPlaceOrder: add_place_order) {

            val orderid = addPlaceOrder.order_id

            val myRef: Query = FirebaseDatabase.getInstance().getReference("users/order/$orderid")
            myRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(ds: DataSnapshot) {
                    itemView.txtOrderId.text = ds.child("order_id").value.toString()
                    val date = ds.child("date").value.toString()
                    val time = ds.child("time").value.toString()
                    itemView.txtOrderPlaceOn.text = date + " "+time
                    itemView.txtOrderDeliverTo.text = ds.child("name").value.toString()
                    itemView.txtOrderTotal.text = ds.child("total").value.toString()
                    itemView.txtOrderStatus.text = ds.child("status").value.toString()
                }
            })
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.myorder_data_row, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return arrayList1.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(arrayList1[position])


    }
}










