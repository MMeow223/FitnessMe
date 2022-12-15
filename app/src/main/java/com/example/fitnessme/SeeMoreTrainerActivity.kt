package com.example.fitnessme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SeeMoreTrainerActivity : AppCompatActivity() {
    private lateinit var recyclerView1: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_more_trainer)

        supportActionBar?.hide()
        initUI()
        loadRecyclerView()

    }

    private fun initUI(){
        recyclerView1 = findViewById(R.id.recyclerView1)
    }


    private fun loadRecyclerView(){

        val trainerList = ArrayList<Trainer>()

        trainerList.add(Trainer("Jamie Jane","Yoga Trainer",3,R.drawable.yoga, 3, "Kuching"))
        trainerList.add(Trainer("Steve Hawker","Man Physique Trainer",4,R.drawable.yoga, 3, "Kuching"))
        trainerList.add(Trainer("Arnold","IFBB Pro Trainer, Yoga Trainer, BodyBuilder",5,R.drawable.yoga, 3, "Kuching"))
        trainerList.add(Trainer("Arnold","IFBB Pro Trainer",5,R.drawable.yoga, 3, "Kuching"))
        trainerList.add(Trainer("Arnold","IFBB Pro Trainer",5,R.drawable.yoga, 3, "Kuching"))
        trainerList.add(Trainer("Arnold","IFBB Pro Trainer",5,R.drawable.yoga, 3, "Kuching"))
        trainerList.add(Trainer("Arnold","IFBB Pro Trainer",5,R.drawable.yoga, 3, "Kuching"))
        trainerList.add(Trainer("Arnold","IFBB Pro Trainer",5,R.drawable.yoga, 3, "Kuching"))
        trainerList.add(Trainer("Arnold","IFBB Pro Trainer",5,R.drawable.yoga, 3, "Kuching"))

        val manager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView1.layoutManager = manager
        recyclerView1.adapter = MoreTrainerAdapter(trainerList)


    }
}