package com.example.fitnessme

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.Executors


class TrainerAdapter(val trainerList: ArrayList<User>) :
    RecyclerView.Adapter<TrainerAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var nameTextView: TextView = itemView.findViewById(R.id.textView1)
        private var typeTextView: TextView = itemView.findViewById(R.id.textView2)
        private var ratingTextView: TextView = itemView.findViewById(R.id.textView3)
        private var workoutImageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(trainer: User) {
            nameTextView.text = trainer.firstname
            typeTextView.text = trainer.field
            ratingTextView.text = ""

            for (i in 1..trainer.rating) {
                ratingTextView.text = ratingTextView.text.toString() + "⭐"
            }

            if(5-trainer.rating != 0) {
                for (i in 1..5-trainer.rating) {
                    ratingTextView.text = ratingTextView.text.toString() + "★"
                }
            }

//            ratingTextView.text = trainer.rating.toString()
            val handler = Handler(Looper.getMainLooper())
            var image: Bitmap? = null
            Executors.newSingleThreadExecutor().execute {
                try {
                    val `in` = java.net.URL(trainer.profile_image_link).openStream()
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
            .inflate(R.layout.square_card_recycler_view_item, parent, false)
        return RecipeViewHolder(view)
    }

    // Returns size of data list
    override fun getItemCount(): Int {
        return trainerList.size
    }

    // Displays data at a certain position
    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(trainerList[position])

        holder.itemView.setOnClickListener {
            //open
            val intent = Intent(it.context, TrainerDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("trainer", trainerList[position])
            intent.putExtras(bundle)
            it.context.startActivity(intent)
        }
    }
}
