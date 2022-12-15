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
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.Executors
import kotlin.time.Duration.Companion.seconds


class AddUserToProgrammeAdapter(val programmeList: ArrayList<Programme>, val traineeEmail: String) :
    RecyclerView.Adapter<AddUserToProgrammeAdapter.ViewHolder>() {

    private val db = Firebase.firestore
    private val user = Firebase.auth.currentUser

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var nameTextView: TextView = itemView.findViewById(R.id.textView)
        private var workoutImageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(programme: Programme) {
            nameTextView.text = programme.programme_name


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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.add_user_to_programme_recycler_view_item, parent, false)
        return ViewHolder(view)
    }

    // Returns size of data list
    override fun getItemCount(): Int {
        return programmeList.size
    }

    // Displays data at a certain position
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(programmeList[position])

        holder.itemView.setOnClickListener {

//            data class UserProgramme(
//                var programme_name: String = "",
//                var trainer: String = "",
//                var schedule: ArrayList<String> = ArrayList(),
//                var expected_end_date: Timestamp = Timestamp.now(),
//                var image_link: String = "",
//                var user: String = ""
//            )

            var numberOfMonth = programmeList[position].program_duration
            var datetimeNow = Timestamp.now().seconds + (numberOfMonth* 2628288)
            var expected_end_date = Timestamp(datetimeNow, 0)

            val hashMap = hashMapOf(
                "programme_name" to programmeList[position].programme_name,
                "trainer" to user?.email.toString(),
                "expected_end_date" to expected_end_date,
                "image_link" to programmeList[position].image_link,
                "user" to traineeEmail
            )

            db.collection("user_programme")
                .add(hashMap)
                .addOnSuccessListener {
                    Toast.makeText( holder.itemView.context,"Added User To Programme", Toast.LENGTH_SHORT).show()
                }

        }
    }
}
