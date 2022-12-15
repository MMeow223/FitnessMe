package com.example.fitnessme

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.webkit.WebChromeClient
import android.webkit.WebSettings.PluginState
import android.webkit.WebView
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.Executors


class ExploreExerciseDetailActivity : AppCompatActivity() {

    private lateinit var button: Button
    private lateinit var textView1: TextView
    private lateinit var textView2: TextView
    private lateinit var textView3: TextView
    private lateinit var textView4: TextView
    private lateinit var imageView: ImageView
    private lateinit var linearLayout: LinearLayout


    val videoUrl = "https://www.youtube.com/watch?v=vthMCtgVtFwe"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore_exercise_detail)

        supportActionBar?.hide()

        initUI()

        // extract bundle
        val bundle: Bundle? = intent.extras
        val exercise = bundle?.getSerializable("exercise") as Exercise



        val handler = Handler(Looper.getMainLooper())
        var image: Bitmap? = null
        Executors.newSingleThreadExecutor().execute {
            try {
                val `in` = java.net.URL(exercise.imageUrl).openStream()
                image = BitmapFactory.decodeStream(`in`)
                handler.post {
                    imageView.setImageBitmap(image)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        textView1.text = exercise.name
        textView2.text = exercise.description
        textView3.text = exercise.target_muscle
        textView4.text = exercise.alternative

        val textview = TextView(this)
        textview.text = "Watch a video on how to do this exercise"
        textview.textSize = 50f

//        linearLayout.addView(textview)


        button.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(exercise.videoUrl)
            startActivity(intent)
        }
    }

    private fun initUI(){
        button = findViewById(R.id.button)

        textView1 = findViewById(R.id.textView1)
        textView2 = findViewById(R.id.textView2)
        textView3 = findViewById(R.id.textView3)
        textView4 = findViewById(R.id.textView4)

        imageView = findViewById(R.id.imageView)

        linearLayout = findViewById(R.id.linearLayout)
    }

}