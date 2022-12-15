package com.example.fitnessme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract.Helpers.update
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddUserToProgrammeActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user_to_programme)
        initUI()
        updateUI()
    }

    private fun initUI(){
        supportActionBar?.hide()

        recyclerView = findViewById(R.id.recyclerView)
        val manager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = manager
    }
    private fun updateUI(){
        getProgrammes()
    }

    private fun getProgrammes(){
        db.collection("programme")
            .get()
            .addOnSuccessListener { result ->

                var programmeList: ArrayList<Programme> = ArrayList()

            for (document in result) {
                programmeList.add(document.toObject(Programme::class.java))

                recyclerView.adapter = AddUserToProgrammeAdapter(programmeList, intent.getStringExtra("traineeEmail").toString())
            }
        }
    }
}