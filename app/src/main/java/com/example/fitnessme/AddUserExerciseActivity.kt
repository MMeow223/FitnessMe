package com.example.fitnessme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddUserExerciseActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private val user = Firebase.auth.currentUser
    private lateinit var recyclerView1: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user_exercise)

        initUI()

        loadExercises()

    }
    private fun initUI(){

        supportActionBar?.hide()
        recyclerView1 = findViewById(R.id.recyclerView1)

    }

    private fun loadExercises(){
        db.collection("exercises").get()
            .addOnSuccessListener { result ->
                recyclerView1.layoutManager = LinearLayoutManager(this)
                recyclerView1.adapter = AddUserExerciseAdapter(
                    DBHelper.convertQuerySnapshotToExerciseList(result),user?.email.toString())
            }
    }
}