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


class ExerciseAdapter(val exerciseList: ArrayList<UserExercise>) :
    RecyclerView.Adapter<ExerciseAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var exerciseTextView: TextView = itemView.findViewById(R.id.textView)
        private var exerciseImageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(exercise: UserExercise) {
            exerciseTextView.text = exercise.exercise_name
//            exerciseImageView.setImageResource(exercise.image)

            val handler = Handler(Looper.getMainLooper())
            var image: Bitmap? = null
            Executors.newSingleThreadExecutor().execute {
                try {
                    val `in` = java.net.URL(exercise.image_link).openStream()
                    image = BitmapFactory.decodeStream(`in`)
                    handler.post {
                        exerciseImageView.setImageBitmap(image)
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
            .inflate(R.layout.exercise_recycler_view_item, parent, false)
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
            val intent = Intent(holder.itemView.context, ExerciseDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putString("exercise_name", exerciseList[position].exercise_name)
//            bundle.putSerializable("exercise", exerciseList[position])
            intent.putExtras(bundle)
            holder.itemView.context.startActivity(intent)
        }

    }
}
