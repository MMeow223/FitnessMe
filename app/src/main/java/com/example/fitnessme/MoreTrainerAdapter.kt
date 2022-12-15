package com.example.fitnessme

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class MoreTrainerAdapter(val trainerList: ArrayList<Trainer>) :
    RecyclerView.Adapter<MoreTrainerAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var nameTextView: TextView = itemView.findViewById(R.id.textView1)
        private var ratingTextView: TextView = itemView.findViewById(R.id.textView2)
        private var typeTextView: TextView = itemView.findViewById(R.id.textView3)
        private var experienceTextView: TextView = itemView.findViewById(R.id.textView4)
        private var locationTextView: TextView = itemView.findViewById(R.id.textView5)

        private var workoutImageView: ImageView = itemView.findViewById(R.id.imageView1)


        fun bind(trainer: Trainer) {
            nameTextView.text = trainer.name
            typeTextView.text = trainer.type
            ratingTextView.text = ""
            experienceTextView.text = trainer.experience.toString() + " Years"
            locationTextView.text = trainer.location

            for (i in 1..trainer.rating) {
                ratingTextView.text = ratingTextView.text.toString() + "⭐"
            }

            if(5-trainer.rating != 0) {
                for (i in 1..5-trainer.rating) {
                    ratingTextView.text = ratingTextView.text.toString() + "★"
                }
            }

//            ratingTextView.text = trainer.rating.toString()
            workoutImageView.setImageResource(trainer.image)
        }
    }

    // Returns a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.more_trainer_recycler_view_item, parent, false)
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
            it.context.startActivity(intent)
        }
    }
}
