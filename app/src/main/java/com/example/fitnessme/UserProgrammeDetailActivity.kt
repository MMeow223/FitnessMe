package com.example.fitnessme

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.Executors

class UserProgrammeDetailActivity : AppCompatActivity() {

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
    private lateinit var textView14: TextView
    private lateinit var textView15: TextView
    private lateinit var textView16: TextView
    private lateinit var textView17: TextView
    private lateinit var textView18: TextView

    private lateinit var imageView1: ImageView
    private lateinit var imageView2: ImageView

    private lateinit var cardView: CardView



    private var programme = Programme()
    private var trainer = User()
    private lateinit var bundle: Bundle

    private val db = Firebase.firestore
    private val user = Firebase.auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_programme_detail)

        // get bundle
        bundle = intent.extras!!

        initUI()

        updateUI()
    }

    private fun updateProgrammeInformation(){
        db.collection("programme")
            .whereEqualTo("programme_name", bundle?.getString("programme_name"))
            .get()
            .addOnSuccessListener { document ->
                if (!document.documents.isEmpty()) {
                    programme = document.documents[0].toObject(Programme::class.java)!!

                    textView1.text = programme.programme_name
                    textView2.text = programme.programme_name
                    textView3.text = programme.description
                    textView4.text = programme.difficulty
                    textView5.text = programme.program_duration.toString()
                    textView6.text = programme.session_duration.toString()
                    textView7.text = programme.ferquency.toString()
                    textView8.text = programme.venue
                    textView9.text = programme.other_detail
                    textView10.text = programme.warmup.toString()
                    textView11.text = programme.workout.toString()
                    textView12.text = programme.cooldown.toString()
                    textView13.text = programme.price.toString()

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

    }

    private fun updateTrainerInformation(){
        db.collection("user")
            .whereEqualTo("email", bundle?.getString("trainer"))
            .get()
            .addOnSuccessListener { document ->
                if (!document.documents.isEmpty()) {
                    trainer = document.documents[0].toObject(User::class.java)!!
                    textView14.text = trainer.firstname + " " + trainer.lastname
                    textView15.text = ""
                    textView16.text = trainer.field
                    textView17.text = trainer.coaching_experience.toString() + " Years"
                    textView18.text = trainer.region

                    for (i in 1..trainer.rating) {
                        textView15.text = textView15.text.toString() + "⭐"
                    }

                    if(5-trainer.rating != 0) {
                        for (i in 1..5-trainer.rating) {
                            textView15.text = textView15.text.toString() + "★"
                        }
                    }

                    val handler = Handler(Looper.getMainLooper())
                    var image: Bitmap? = null
                    Executors.newSingleThreadExecutor().execute {
                        try {
                            val `in` = java.net.URL(trainer.profile_image_link).openStream()
                            image = BitmapFactory.decodeStream(`in`)
                            handler.post {
                                imageView2.setImageBitmap(image)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    cardView.setOnClickListener {
                        val bundle = Bundle()
                        bundle.putString("trainer_email", trainer.email)
                        val intent = Intent(this, TrainerDetailActivity::class.java)
                        intent.putExtras(bundle)
                        startActivity(intent)
                    }
                }
            }
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
        textView14 = findViewById(R.id.textView14)
        textView15 = findViewById(R.id.textView15)
        textView16 = findViewById(R.id.textView16)
        textView17 = findViewById(R.id.textView17)
        textView18 = findViewById(R.id.textView18)

        imageView1 = findViewById(R.id.imageView1)
        imageView2 = findViewById(R.id.imageView2)

        cardView = findViewById(R.id.cardView)

    }

    private fun updateUI(){
        updateProgrammeInformation()
        updateTrainerInformation()
    }
}