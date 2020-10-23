package com.parthkapatel.antiquepavbhaji

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_product_details.*
import kotlinx.android.synthetic.main.item_deatils_row.view.*

class ProductDetailsFragment(var category:String) : Fragment() {


    private lateinit var dialog : AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
        //Hide Cart Image And CartBadge
        val cartimage = activity?.findViewById<ImageView>(R.id.cartImage)
        val cartimagebadge = activity?.findViewById<TextView>(R.id.counterbadgeCart)
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

        return inflater.inflate(R.layout.fragment_product_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val bbt = AnimationUtils.loadAnimation(context,R.anim.rtl)
        itemRecycler.startAnimation(bbt)
        startLoadingDialog()
        val arrayList = ArrayList<add_items>()
        var myRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("admin/items/$category")
        var lm= LinearLayoutManager(context, RecyclerView.VERTICAL,false)
        itemRecycler.layoutManager=lm
        var ad= context?.let { Add_Item_adapter(arrayList, it) }
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                arrayList.clear()
                for (ds in dataSnapshot.children) {
                    Log.d("check",ds.toString())
                    arrayList.add(ds.getValue(add_items::class.java)!!)
                }
                itemRecycler?.adapter?.notifyDataSetChanged()
                itemRecycler.adapter=ad
                dismissDialog()
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })


    }


    fun startLoadingDialog(){
        var builder = AlertDialog.Builder(activity)
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
var item_name = ""
var item_id = ""
class Add_Item_adapter(private val arrayList: java.util.ArrayList<add_items>, var context: Context) :
    RecyclerView.Adapter<Add_Item_adapter.ViewHolder>()  {


    class ViewHolder(item_view : View):RecyclerView.ViewHolder(item_view){


        fun bindItems(additems: add_items){
            itemView.txtlblItemName.text = additems.item_name
            itemView.txtlblItemAmount.text = additems.amount
            itemView.txtlblItemDesc.text = additems.description
            item_id = additems.item_Uid
            item_name = additems.cat_name

            var userid = ""
            val myAuth =  FirebaseAuth.getInstance().currentUser
            var user = myAuth?.phoneNumber?.substring(3).toString()
            val query2: Query =
                FirebaseDatabase.getInstance().reference.child("users/profile/$user")
            query2.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    userid = dataSnapshot.child("user_UID").value.toString()
                }
            })
            val query: Query =
                FirebaseDatabase.getInstance().reference.child("users/addcart/").orderByChild("cart_item_Uid").equalTo("$item_id")
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (ds in dataSnapshot.children)
                    {
                        if(ds.child("cart_User_Uid").value.toString() == userid)
                        {
                            itemView.btnPdtRemoveCart.visibility = View.VISIBLE
                            itemView.btnPdtAddCart.text = "Go To Cart"
                        }
                    }
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_deatils_row,parent,false)

        return  ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val user =   FirebaseAuth.getInstance().currentUser
        var users = user?.phoneNumber?.substring(3).toString()
        var prf = ProductDetailsFragment("")
        holder.bindItems(arrayList[position])
        holder.itemView.btnPdtAddCart.setOnClickListener {view ->
            holder.itemView.btnPdtAddCart.isEnabled = false
            if(holder.itemView.btnPdtAddCart.text.toString() == "Add To Cart") {

                val database = FirebaseDatabase.getInstance()
                val item = holder.itemView.txtlblItemName.text.toString()
                //Get User UID
                val sharedPref: SharedPreferences =
                    this.context.getSharedPreferences("Login", Context.MODE_PRIVATE)
                val useruid = sharedPref.getString("User_id", "").toString()
                //Get Item UID
                var itemuid = ""
                val query: Query =
                    FirebaseDatabase.getInstance().reference.child("admin/items/$item_name/$item")
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        itemuid = dataSnapshot.child("item_Uid").value.toString()
                        var itemrs = dataSnapshot.child("amount").value.toString()
                        val myRef = database.getReference("users/addcart/")
                        val key = myRef.push().key
                        myRef.child("$key").setValue(key?.let {
                            add_cart(it, useruid, itemuid, item_name,item,itemrs,"1")
                        }).addOnCompleteListener {
                            holder.itemView.btnPdtRemoveCart.visibility = View.VISIBLE
                            holder.itemView.btnPdtAddCart.text = "Go To Cart"
                            Snackbar.make(
                                view,
                                "${item}  added to your cart",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }.addOnCanceledListener { }
                    }
                })
                holder.itemView.btnPdtAddCart.isEnabled = true
            }else{

                val manager: FragmentManager = (context as AppCompatActivity).supportFragmentManager
                val fragmentTransaction = manager.beginTransaction()
                fragmentTransaction.replace(R.id.homeframe, CartFragment()).addToBackStack(null).commit()
            }
        }

        holder.itemView.btnPdtRemoveCart.setOnClickListener {view->

            if(holder.itemView.btnPdtAddCart.text.toString() != "Add To Cart") {

                val database = FirebaseDatabase.getInstance()
                val item = holder.itemView.txtlblItemName.text.toString()
                //Get Item UID
                var itemuid = ""
                val myRef = database.getReference("users/addcart/")
                val query: Query =
                    FirebaseDatabase.getInstance().reference.child("admin/items/$item_name/$item")
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        itemuid = dataSnapshot.child("item_Uid").value.toString()
                        /*-----------------------------------------------------------*/
                        //check Item is already added in cart or not
                        val query: Query =
                            FirebaseDatabase.getInstance().reference.child("users/addcart/")
                                .orderByChild("cart_item_Uid").equalTo("$itemuid")
                        query.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {
                            }

                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                for (ds in dataSnapshot.children) {
                                    val cart_uid = ds.child("cart_Uid").value.toString()

                                        myRef.child("$cart_uid").removeValue().addOnCompleteListener {
                                            holder.itemView.btnPdtRemoveCart.visibility = View.INVISIBLE
                                            holder.itemView.btnPdtAddCart.text = "Add To Cart"
                                            Snackbar.make(
                                                view,
                                                "${item}   removed from your cart",
                                                Snackbar.LENGTH_LONG
                                            ).show()
                                        }
                                }
                            }
                        })
                    }
                })
            }
        }

    }


}