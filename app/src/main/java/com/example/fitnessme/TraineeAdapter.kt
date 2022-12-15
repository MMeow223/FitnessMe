package com.example.fitnessme

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class TraineeAdapter(val traineeList: ArrayList<User>) :
    RecyclerView.Adapter<TraineeAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var nameTextView: TextView = itemView.findViewById(R.id.textView1)

        fun bind(trainee: User) {
            nameTextView.text = trainee.firstname + " " + trainee.lastname

        }
    }

    // Returns a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.trainee_recycler_view_item, parent, false)
        return RecipeViewHolder(view)
    }

    // Returns size of data list
    override fun getItemCount(): Int {
        return traineeList.size
    }

    // Displays data at a certain position
    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(traineeList[position])

        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, TrainerViewTraineeActivity::class.java)
            intent.putExtra("traineeEmail", traineeList[position].email)
            intent.putExtra("traineeProfile", traineeList[position].profile_image_link)
            intent.putExtra("traineePhone", traineeList[position].phone)
            intent.putExtra("traineeName", traineeList[position].firstname + " " + traineeList[position].lastname)

            it.context.startActivity(intent)
        }
    }
}
