package com.example.fitnessme

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.Executors

class ProgrammeDetailActivity : AppCompatActivity() {

    private lateinit var textView1: TextView
    private lateinit var textView2: TextView
    private lateinit var textView3: TextView
    private lateinit var textView4: TextView
    private lateinit var textView5: TextView
    private lateinit var textView6: TextView
    private lateinit var textView7: TextView
    private lateinit var textView8: TextView
    private lateinit var textView9: TextView
    private lateinit var textView10: TextView
    private lateinit var textView11: TextView
    private lateinit var textView12: TextView
    private lateinit var textView13: TextView

    private lateinit var imageView1: ImageView

    private lateinit var recyclerView1: RecyclerView

    private var programme: Programme = Programme()
    private var trainerList = ArrayList<User>()

    private var db = Firebase.firestore
    private val user = Firebase.auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_programme_detail)

        initUI()
        val manager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView1.layoutManager = manager

        programme = intent.getSerializableExtra("programme") as Programme


        var references = intent.getStringArrayListExtra("trainers_references")!!


        for( trainer in references ){
            db.collection("user")
                .whereEqualTo("role", "Trainer")
                .get()
                .addOnSuccessListener { result ->
                    if (result != null) {
                        DBHelper.convertQuerySnapshotToUsers(result)?.let { trainerList = it }

//                        for (document in result) {
//                            todo might show two tme
//                            val user = document.toObject(User::class.java)
//                            trainerList.add(user)
                        }
//                    } else {
//                        Log.d("TAG", "No such document")
//                    }
                    recyclerView1.adapter = TrainerAdapter(trainerList)

                }
                .addOnFailureListener { exception ->
                    Log.d("TAG", "get failed with ", exception)
                }
        }


//        recyclerView1.adapter?.notifyDataSetChanged()

//        val trainerDocumentReference = programme.trainers
//
//        for (exercise in trainerDocumentReference) {
//            exercise.addSnapshotListener { snapshot, e ->
//                if (e != null) {
//                    Log.w(TAG, "Listen failed.", e)
//                    return@addSnapshotListener
//                }
//
//                if (snapshot != null && snapshot.exists()) {
//                    Log.d(TAG, "Current data: ${snapshot.data}")
//                    val trainer = snapshot.toObject(User::class.java)
//                    if (trainer != null) {
//                        trainerList.add(trainer)
//                    }
//                } else {
//                    Log.d(TAG, "Current data: null")
//                }
//            }
//
//        }
        updateUI()

//        trainerList.add(User())





//        trainerList.add(Trainer("Steve Hawker","Man Physique Trainer",4,R.drawable.yoga, 3, "Kuching"))
//        trainerList.add(Trainer("Arnold","IFBB Pro Trainer",5,R.drawable.yoga, 3, "Kuching"))



    }

    private fun initUI(){
        supportActionBar?.hide()

        textView1 = findViewById(R.id.textView1)
        textView2 = findViewById(R.id.textView2)
        textView3 = findViewById(R.id.textView3)
        textView4 = findViewById(R.id.textView4)
        textView5 = findViewById(R.id.textView5)
        textView6 = findViewById(R.id.textView6)
        textView7 = findViewById(R.id.textView7)
        textView8 = findViewById(R.id.textView8)
        textView9 = findViewById(R.id.textView9)
        textView10 = findViewById(R.id.textView10)
        textView11 = findViewById(R.id.textView11)
        textView12 = findViewById(R.id.textView12)
        textView13 = findViewById(R.id.textView13)

        imageView1 = findViewById(R.id.imageView1)

        recyclerView1 = findViewById(R.id.recyclerView1)

    }

    private fun updateUI(){
        textView1.text = programme.programme_name
        textView2.text = programme.programme_name
        textView3.text = programme.description
        textView4.text = programme.difficulty
        textView5.text = programme.program_duration.toString() + " month"
        textView6.text = programme.session_duration.toString() + " min"
        textView7.text = programme.ferquency.toString() + " times/week"
        textView8.text = programme.venue
        textView9.text = programme.other_detail
        textView10.text = programme.warmup.toString() + " min"
        textView11.text = programme.workout.toString() + " min"
        textView12.text = programme.cooldown.toString() + " min"
        textView13.text =  "RM " + programme.price.toString() + "/month"

        // load image from link
        val handler = Handler(Looper.getMainLooper())
        var image: Bitmap? = null
        Executors.newSingleThreadExecutor().execute {
            try {
                val `in` = java.net.URL(programme.image_link).openStream()
                image = BitmapFactory.decodeStream(`in`)
                handler.post {
                    imageView1.setImageBitmap(image)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }
}