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


class WorkoutTrendAdapter(val programmeList: ArrayList<Programme>) :
    RecyclerView.Adapter<WorkoutTrendAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var workoutTextView: TextView = itemView.findViewById(R.id.textView)
        private var workoutImageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(programme: Programme) {
            workoutTextView.text = programme.programme_name

            val handler = Handler(Looper.getMainLooper())
            var image: Bitmap? = null
            Executors.newSingleThreadExecutor().execute {
                try {
                    val `in` = java.net.URL(programme.image_link).openStream()
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
            .inflate(R.layout.workout_trend_recycler_view_item, parent, false)
        return RecipeViewHolder(view)
    }

    // Returns size of data list
    override fun getItemCount(): Int {
        return programmeList.size
    }

    // Displays data at a certain position
    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(programmeList[position])

        holder.itemView.setOnClickListener {

            val position: Int = holder.adapterPosition
            val intent = Intent(holder.itemView.context, ProgrammeDetailActivity::class.java)
            var programme_details =  programmeList[position].copy()

            var stringify: ArrayList<String> = ArrayList()

            for( trainer in programmeList[position].trainers) {
                stringify.add(trainer.id)
            }
            programme_details.trainers = ArrayList()

            var bundle = Bundle()
            bundle.putSerializable("programme", programme_details)
            bundle.putStringArrayList("trainers_references",  stringify)
            intent.putExtras(bundle)
            holder.itemView.context.startActivity(intent)
        }
    }
}
