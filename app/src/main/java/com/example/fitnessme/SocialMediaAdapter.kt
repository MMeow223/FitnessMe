package com.example.fitnessme

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView


class SocialMediaAdapter(val socialMediaList: ArrayList<String>) :
    RecyclerView.Adapter<SocialMediaAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var workoutImageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(socialMedia: String) {

            if (socialMedia.contains("twitter.com")) {
                workoutImageView.setImageResource(R.drawable.twitter_logo)
            } else if (socialMedia.contains("wa.me")) {
                workoutImageView.setImageResource(R.drawable.whatsapp_icon)
            } else if (socialMedia.contains("www.facebook.com")) {
                workoutImageView.setImageResource(R.drawable.facebook_logo)
            } else if (socialMedia.contains("www.linkedin.com")) {
                workoutImageView.setImageResource(R.drawable.linkedin_logo)
            } else if (socialMedia.contains("www.instagram.com")) {
                workoutImageView.setImageResource(R.drawable.instagram_logo)
            } else{
                workoutImageView.setImageResource(R.color.text_grey)
            }
        }

    }

    // Returns a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.social_media_recycler_view_item, parent, false)
        return RecipeViewHolder(view)
    }

    // Returns size of data list
    override fun getItemCount(): Int {
        return socialMediaList.size
    }

    // Displays data at a certain position
    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(socialMediaList[position])

        holder.itemView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW,Uri.parse(socialMediaList[position]))
            startActivity(it.context, intent, null)

        }
    }
}
