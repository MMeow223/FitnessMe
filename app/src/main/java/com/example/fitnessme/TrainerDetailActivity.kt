package com.example.fitnessme

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.transition.Scene
import android.transition.Transition
import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text
import java.util.concurrent.Executors

class TrainerDetailActivity : AppCompatActivity() {

    private lateinit var recyclerView1: RecyclerView
    private lateinit var recyclerView2: RecyclerView
    private lateinit var button: Button

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

    private lateinit var star1: TextView
    private lateinit var star2: TextView
    private lateinit var star3: TextView
    private lateinit var star4: TextView
    private lateinit var star5: TextView

    private lateinit var imageView1: ImageView
    private var trainer: User = User()

    private var db = Firebase.firestore
    private val user = Firebase.auth.currentUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trainer_detail)

        initUI()

         if (intent.hasExtra("trainer")) {
             trainer = intent.getSerializableExtra("trainer") as User
             updateUI()
         }
        else{
             db.collection("user")
                .whereEqualTo("email", intent.getStringExtra("trainer_email"))
                .get()
                .addOnSuccessListener { result ->
                    trainer = result.documents[0].toObject(User::class.java)!!
                    updateUI()
                }
         }

        button.setOnClickListener {
            directToWhatsapp()
        }

        star1.setOnClickListener{
            db.collection("user")
                .whereEqualTo("email", trainer.email)
                .get()
                .addOnSuccessListener { result ->
                    db.collection("user")
                        .document(result.documents[0].id)
                        .update("rating", 1)
                        .addOnSuccessListener {
                            updateTrainer()
                        }

                }
        }

        star2.setOnClickListener{
            db.collection("user")
                .whereEqualTo("email", trainer.email)
                .get()
                .addOnSuccessListener { result ->
                    db.collection("user")
                        .document(result.documents[0].id)
                        .update("rating", 2)
                        .addOnSuccessListener {
                            updateTrainer()
                        }

                }
        }

        star3.setOnClickListener{
            db.collection("user")
                .whereEqualTo("email", trainer.email)
                .get()
                .addOnSuccessListener { result ->
                    db.collection("user")
                        .document(result.documents[0].id)
                        .update("rating", 3)
                        .addOnSuccessListener {
                            updateTrainer()
                        }

                }
        }

        star4.setOnClickListener{
            db.collection("user")
                .whereEqualTo("email", trainer.email)
                .get()
                .addOnSuccessListener { result ->
                    db.collection("user")
                        .document(result.documents[0].id)
                        .update("rating", 4)
                        .addOnSuccessListener {
                            updateTrainer()
                        }

                }
        }

        star5.setOnClickListener{
            db.collection("user")
                .whereEqualTo("email", trainer.email)
                .get()
                .addOnSuccessListener { result ->
                    db.collection("user")
                        .document(result.documents[0].id)
                        .update("rating", 5)
                        .addOnSuccessListener {
                            updateTrainer()
                        }

                }
        }
    }

    private fun updateTrainer(){
        db.collection("user")
            .whereEqualTo("email", trainer.email)
            .get()
            .addOnSuccessListener { documents ->
                trainer = documents.first().toObject(User::class.java)!!
                updateUI()

            }
    }

    private fun initUI(){
        supportActionBar?.hide()

        recyclerView1 = findViewById(R.id.recyclerView1)
        recyclerView2 = findViewById(R.id.recyclerView2)
        button = findViewById(R.id.button1)

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

        star1 = findViewById(R.id.star1)
        star2 = findViewById(R.id.star2)
        star3 = findViewById(R.id.star3)
        star4 = findViewById(R.id.star4)
        star5 = findViewById(R.id.star5)

        imageView1 = findViewById(R.id.imageView)

    }

    private fun updateUI(){


        var starList = listOf(star1, star2, star3, star4, star5)

        textView1.text = trainer.firstname  + " " + trainer.lastname
        textView2.text = trainer.description
        textView3.text = trainer.coaching_experience.toString() + " years"
        textView4.text = trainer.active_trainee.toString() + " ppl"
        textView5.text = trainer.accumulated_trainee.toString() + " ppl"
        textView6.text = "RM " + trainer.expected_fee + "/month"
        textView7.text = trainer.region
        textView8.text = ""
        textView9.text = trainer.phone
        textView10.text = trainer.email

        for (i in 1..trainer.rating) {
            textView8.text = textView8.text.toString() + "⭐"
        }

        if(5-trainer.rating != 0) {
            for (i in 1..5-trainer.rating) {
                textView8.text = textView8.text.toString() + "★"
            }
        }
        for(star in starList){
            star.text = "★"
        }

        for(i in 0..trainer.rating-1){
            starList[i].text = "⭐"
        }

        // load image from link
        val handler = Handler(Looper.getMainLooper())
        var image: Bitmap? = null
        Executors.newSingleThreadExecutor().execute {
            try {
                val `in` = java.net.URL(trainer.profile_image_link).openStream()
                image = BitmapFactory.decodeStream(`in`)
                handler.post {
                    imageView1.setImageBitmap(image)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        val socialMediaList = ArrayList<String>()
//        socialMediaList.add("https://www.instagram.com/elviswong2002/")
//        socialMediaList.add("https://www.facebook.com/elvis.wong.148")

        for(i in trainer.social_medias){
            socialMediaList.add(i)
        }

        val photoList = ArrayList<String>()
        for( link in trainer.image_links){
            photoList.add(link)
        }


        val manager1 = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val manager2 = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView1.layoutManager = manager1
        recyclerView1.adapter = PhotoAdapter(photoList)

        recyclerView2.layoutManager = manager2
        recyclerView2.adapter = SocialMediaAdapter(socialMediaList)
    }

    private fun directToWhatsapp(){
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://wa.me/" + trainer.phone)
        startActivity(intent)
    }

}