package com.parthkapatel.antiquepavbhaji

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main_screen.*
import kotlinx.android.synthetic.main.nav_header_main.*


class MainScreen : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener {

    private lateinit var dialog : AlertDialog
    private val myAuth = FirebaseAuth.getInstance().currentUser
    private val user = myAuth?.phoneNumber?.substring(3).toString()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)

        startLoadingDialog()
        if (myAuth != null) {
            val query2: Query = FirebaseDatabase.getInstance().reference.child("users/profile/$user/")
            query2.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.childrenCount > 0){
                        setFragment(MainScreenFragment())
                    }else{
                        setFragment(EditProfileFragment())
                    }
                }
            })
        }
        dismissDialog()
        headerNameUpdate()

        val tabLayout=findViewById<TabLayout>(R.id.tabLayout)

        tabLayout.addTab(tabLayout.newTab().setText("Home"))
        tabLayout.addTab(tabLayout.newTab().setText("Pavbhaji"))
        tabLayout.addTab(tabLayout.newTab().setText("Pulav"))
        tabLayout.addTab(tabLayout.newTab().setText("Masala Pav"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        supportFragmentManager.beginTransaction().replace(
                            R.id.homeframe,
                            MainScreenFragment()
                        ).addToBackStack(null).commit()
                    }
                    1 -> {
                        supportFragmentManager.beginTransaction().replace(R.id.homeframe,
                            ProductDetailsFragment("Pavbhaji")
                        ).addToBackStack(null).commit()
                    }
                    2 -> {
                        supportFragmentManager.beginTransaction().replace(R.id.homeframe,
                            ProductDetailsFragment("Pulav")
                        ).addToBackStack(null).commit()
                    }
                    3 -> {
                        supportFragmentManager.beginTransaction().replace(R.id.homeframe,
                            ProductDetailsFragment("Masala Pav")
                        ).addToBackStack(null).commit()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        supportFragmentManager.beginTransaction().replace(R.id.homeframe,
                            MainScreenFragment()
                        ).addToBackStack(null).commit()
                    }
                    1 -> {
                        supportFragmentManager.beginTransaction().replace(R.id.homeframe,
                            ProductDetailsFragment("Pavbhaji")
                        ).addToBackStack(null).commit()
                    }
                    2 -> {
                        supportFragmentManager.beginTransaction().replace(R.id.homeframe,
                            ProductDetailsFragment("Pulav")
                        ).addToBackStack(null).commit()
                    }
                    3 -> {
                        supportFragmentManager.beginTransaction().replace(R.id.homeframe,
                            ProductDetailsFragment("Masala Pav")
                        ).addToBackStack(null).commit()
                    }
                }

            }

        })

        menuImage.setOnClickListener {
            headerNameUpdate()
            val drawer: DrawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
            drawer.openDrawer(GravityCompat.START)
        }

        cartImage.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.homeframe,CartFragment()).addToBackStack(null).commit()
        }

     val navigationView : NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
    }


    private fun headerNameUpdate(){
        val query2: Query =
            FirebaseDatabase.getInstance().reference.child("users/profile/$user/")
        query2.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                headerName.text = dataSnapshot.child("name").value.toString()
            }
        })
    }




    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val drawer: DrawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        when (item.itemId) {
            R.id.nav_home -> supportFragmentManager.beginTransaction().replace(R.id.homeframe,
                MainScreenFragment()
            ).commit()
            R.id.nav_myaccount -> supportFragmentManager.beginTransaction().replace(R.id.homeframe,
                MyAccountFragment()
            ).addToBackStack(null).commit()
            R.id.nav_cart -> supportFragmentManager.beginTransaction().replace(R.id.homeframe,
                CartFragment()
            ).addToBackStack(null).commit()
            R.id.nav_logout -> {
                val myAuth = FirebaseAuth.getInstance()
                myAuth.signOut()
                startActivity(Intent(this,Login::class.java))
                finish()
            }
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }


    override fun onBackPressed() {
        val drawer: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
    private fun setFragment(fragment:Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.homeframe, fragment)
        fragmentTransaction.commit()
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


