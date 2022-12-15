package com.example.fitnessme

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.Executors


class PhotoAdapter(val photoList: ArrayList<String>) :
    RecyclerView.Adapter<PhotoAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var workoutImageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(photo: String) {
            val handler = Handler(Looper.getMainLooper())
            var image: Bitmap? = null
            Executors.newSingleThreadExecutor().execute {
                try {
                    val `in` = java.net.URL(photo).openStream()
                    image = BitmapFactory.decodeStream(`in`)
                    handler.post {
                        workoutImageView.setImageBitmap(image)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    // Returns a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.photo_recycler_view_item, parent, false)
        return RecipeViewHolder(view)
    }

    // Returns size of data list
    override fun getItemCount(): Int {
        return photoList.size
    }

    // Displays data at a certain position
    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(photoList[position])

        holder.itemView.setOnClickListener {

            //open
            val intent = Intent(it.context, ViewPhotoActivity::class.java)
            intent.putExtra("photo", photoList[position])
            it.context.startActivity(intent)
        }
        holder.itemView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.context, R.anim.animation_one))
    }
}
