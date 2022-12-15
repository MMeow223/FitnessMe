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
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.Executors


class ExploreExerciseAdapter(val exerciseList: ArrayList<Exercise>) :
    RecyclerView.Adapter<ExploreExerciseAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var exerciseTextView: TextView = itemView.findViewById(R.id.textView1)
//        private var exerciseImageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(exercise: Exercise) {
            exerciseTextView.text = exercise.name

//            val handler = Handler(Looper.getMainLooper())
//            var image: Bitmap? = null
//            Executors.newSingleThreadExecutor().execute {
//                try {
//                    val `in` = java.net.URL(exercise.imageUrl).openStream()
//                    image = BitmapFactory.decodeStream(`in`)
//                    handler.post {
//                        exerciseImageView.setImageBitmap(image)
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
        }
    }
    // Returns a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.explore_exercise_recycler_view_item, parent, false)
        return RecipeViewHolder(view)
    }

    // Returns size of data list
    override fun getItemCount(): Int {
        return exerciseList.size
    }

    // Displays data at a certain position
    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(exerciseList[position])

        // go to exercise detail page
        holder.itemView.setOnClickListener {

            val intent = Intent(holder.itemView.context, ExploreExerciseDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("exercise", exerciseList[position])
            intent.putExtras(bundle)
            holder.itemView.context.startActivity(intent)


        }
    }
}
