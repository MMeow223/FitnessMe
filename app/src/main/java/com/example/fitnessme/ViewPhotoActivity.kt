package com.example.fitnessme

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import java.util.concurrent.Executors

class ViewPhotoActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_photo)

        supportActionBar?.hide()

        imageView = findViewById(R.id.imageView)

        val imageLink = intent.getStringExtra("photo")

        val handler = Handler(Looper.getMainLooper())
        var image: Bitmap? = null
        Executors.newSingleThreadExecutor().execute {
            try {
                val `in` = java.net.URL(imageLink).openStream()
                image = BitmapFactory.decodeStream(`in`)
                handler.post {
                    imageView.setImageBitmap(image)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }



    }
}