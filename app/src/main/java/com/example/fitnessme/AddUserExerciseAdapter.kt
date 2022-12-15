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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.Executors


class AddUserExerciseAdapter(val exerciseList: ArrayList<Exercise>, val traineeEmail: String) :
    RecyclerView.Adapter<AddUserExerciseAdapter.RecipeViewHolder>() {
    private val db = Firebase.firestore
    private val user = Firebase.auth.currentUser

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var exerciseTextView: TextView = itemView.findViewById(R.id.textView1)
//        private var exerciseImageView: ImageView = itemView.findViewById(R.id.imageView)


        fun bind(exercise: Exercise) {
            exerciseTextView.text = exercise.name

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

            db.collection("user")
                .whereEqualTo("email", user?.email)
                .get()
                .addOnSuccessListener {




                    val exercise_name = exerciseList[position].name
                    var userExercise = UserExercise()
                    userExercise.exercise_name = exercise_name
                    userExercise.image_link = exerciseList[position].imageUrl


                    if(it.documents[0].data?.get("role") == "Trainer"){
                        userExercise.user = traineeEmail
                    }
                    else{
                        userExercise.user = user?.email.toString()
                    }


                    val uid =  db.collection("user_exercise").document().id
                    val documentReference = db.collection("user_exercise").document(uid)

                    val hashMap: HashMap<String,Any> = DBHelper.createUserExercise(userExercise)
                    // create a user exercise
                    db.collection("user_exercise")
                        .document(uid)
                        .set(
                            hashMap, SetOptions.merge()
                        )
                        Toast.makeText(holder.itemView.context, "Exercise added to your list", Toast.LENGTH_SHORT).show()

                }
                .addOnFailureListener {
                    Toast.makeText(holder.itemView.context, "Error adding exercise to your list", Toast.LENGTH_SHORT).show()
                }

        }
    }
}
