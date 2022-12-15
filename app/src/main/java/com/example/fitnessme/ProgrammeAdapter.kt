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
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.Executors


class ProgrammeAdapter(val programmeList: ArrayList<UserProgramme>) :
    RecyclerView.Adapter<ProgrammeAdapter.ViewHolder>() {

    private val db = Firebase.firestore
    private val user = Firebase.auth.currentUser

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var nameTextView: TextView = itemView.findViewById(R.id.textView1)
        private var trainerTextView: TextView = itemView.findViewById(R.id.textView2)
        private var statusTextView: TextView = itemView.findViewById(R.id.textView3)
        private var workoutImageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(programme: UserProgramme) {
            nameTextView.text = programme.programme_name
            trainerTextView.text = ""
            statusTextView.text = ""

            // if the date is before today, then the programme is expired

            if(programme.expected_end_date < Timestamp.now()) {
                statusTextView.text = "In Progress"
            } else {
                val formattedDate = android.text.format.DateFormat.format("dd/MM/yyyy",  programme.expected_end_date.toDate())
                statusTextView.text =formattedDate
            }

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
            .inflate(R.layout.square_card_recycler_view_item, parent, false)
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

            db.collection("user_programme")
                .whereEqualTo("user", user?.email)
                .whereEqualTo("programme_name", programmeList[position].programme_name)
                .get()
                .addOnSuccessListener { documents ->

                    // if the result is empty, then the user has not started the programme yet

                    if(!documents.isEmpty){
                        for (document in documents) {
                            val bundle = Bundle()
                            bundle.putString("programme_name", document.data["programme_name"] as String)
                            bundle.putString("trainer", document.data["trainer"] as String)

                            val intent = Intent(it.context, UserProgrammeDetailActivity::class.java)
                            intent.putExtras(bundle)
                            it.context.startActivity(intent)
                        }
                    }
                    else{
                        val intent = Intent(holder.itemView.context, ProgrammeDetailActivity::class.java)
                        holder.itemView.context.startActivity(intent)
                    }
                }
        }
    }
}
