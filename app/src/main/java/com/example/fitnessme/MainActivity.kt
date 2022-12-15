package com.example.fitnessme

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.PopupWindow
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private val user = Firebase.auth.currentUser
    private var userFromDB = User()

    private lateinit var toolbar: Toolbar



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI()

        updateUI()


    }


    private fun initUI(){
        supportActionBar?.hide()

        toolbar = findViewById(R.id.toolbar)
    }


    private fun updateUI(){
        db.collection("user")
            .whereEqualTo("email", user?.email)
            .get()
            .addOnSuccessListener { result ->
                userFromDB = result.documents[0].toObject(User::class.java)!!

                var tabTiles: Array<String>
                var tabIcons: Array<Int>

                if(userFromDB.role == "Trainer"){
                    tabTiles = arrayOf("Dashboard", "Trainee", "Profile")
                    tabIcons = arrayOf(R.drawable.dashboard_icon, R.drawable.trainee_icon,R.drawable.profile_icon)

                }
                else {
                    tabTiles = arrayOf("Dashboard", "Exercises", "Programme", "Profile")
                    tabIcons = arrayOf(R.drawable.dashboard_icon, R.drawable.trainee_icon, R.drawable.programme_icon, R.drawable.profile_icon)

                }


                val viewPagerAdapter = ViewPagerAdapter(this, tabTiles)

                val viewPager = findViewById<ViewPager2>(R.id.pager)
                val tabLayout = findViewById<TabLayout>(R.id.tab_layout)

                viewPager.adapter = viewPagerAdapter
                viewPager.isUserInputEnabled = false


                TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                    tab.text = tabTiles[position]
                    tab.icon = getDrawable(tabIcons[position])

                }.attach()

                tabLayout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab) {
                        viewPager.currentItem = tab.position

                        toolbar.title = tab.text
//                actionbar?.setTitle(Html.fromHtml("<font color='black'> ${tab.text} </font>"))
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab) {
                    }

                    override fun onTabReselected(tab: TabLayout.Tab) {
                    }
                })
            }

    }


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 10 && resultCode == RESULT_OK) {
//            val uri = data?.data
//            if (uri != null) {
//                db.uploadFile(uri)
//            }
//        }
//
//        if (requestCode == 1 && resultCode == RESULT_OK) {
//
//
//            // request permission for storage
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
//            {
//                ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),2)
//            } else {
//                val extras = data?.extras
//                val imageBitmap = extras?.get("data") as Bitmap
////                uuid
//                val uuid = java.util.UUID.randomUUID().toString()
//
//                val file = File(filesDir, uuid+".jpg")
//                if (!file.exists()) {
//                    Log.d("path", file.toString())
//                    var fos: FileOutputStream? = null
//                    try {
//                        fos = FileOutputStream(file)
//                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
//                        fos.flush()
//                        fos.close()
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                    }
//                }
//            }


//            val uri = data?.data
//            if (uri != null) {
//                db.uploadFile(uri)
//            }
//        }
//    }

}