package com.example.fitnessme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore

class ExploreExerciseActivity : AppCompatActivity() {
    private lateinit var recyclerView1: androidx.recyclerview.widget.RecyclerView
    private lateinit var textView1: TextView
    private lateinit var textView2: TextView
    private lateinit var textView3: TextView
    private lateinit var textView4: TextView

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore_exercise)

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
                recyclerView1.adapter = ExploreExerciseAdapter(DBHelper.convertQuerySnapshotToExerciseList(result))
            }
    }
}