package com.example.fitnessme

import android.os.Parcelable
import android.widget.ArrayAdapter
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.type.DateTime
import java.io.Serializable

data class User(

    // basic information
    var firstname: String = "",
    var lastname: String = "",
    var gender: String = "",
    var phone: String = "",
    var email: String = "",
    var age: Int = 0,
    var heights: ArrayList<Float> = ArrayList(),
    var weights: ArrayList<Float> = ArrayList(),
    var strengths: ArrayList<Float> = ArrayList(),
    var diseases: String = "",
    var goal: String = "",

    // other account information
    var profile_image_link: String = "",
    var image_links: ArrayList<String> = ArrayList(),
    var role: String = "trainee",
    var region: String = "",

    // exercise & programme & calendar information
    var exercises: ArrayList<DocumentReference> = ArrayList(),
    var current_programmes: ArrayList<Programme> = ArrayList(),
    var previous_programmes: ArrayList<Programme> = ArrayList(),
    var calendars: ArrayList<Calendar> = ArrayList(),

    // trainer specific information
    var field: String = "",
    var description: String = "",
    var rating: Int = 0,
    var expected_fee: Int = 0,
    var social_medias: ArrayList<String> = ArrayList(),
    var coaching_experience: Int = 0,
    var trainees: ArrayList<User> = ArrayList(),
    var active_trainee: Int = 0,
    var accumulated_trainee: Int = 0,

    var trainer: String = "",

    var account_created_completed: Boolean = false,

) : Serializable


data class Workout(
    val name: String,
    val image: Int,
)

data class Trainer(
    val name: String,
    val type: String,
    val rating: Int,
    val image: Int,
    val experience: Int,
    val location: String,
)

data class Trainee(
    val name: String,
    val type: String,
)

//data class UserProgramme(
//    var programme: Programme = Programme(),
//    var programme_name: String = "",
//    var trainer: User = User(),
//    var schedule: ArrayList<Calendar> = ArrayList(),
//    var expected_end_date: Timestamp = Timestamp.now(),
//    var image_link: String = "",
//)

data class UserProgramme(
    var programme_name: String = "",
    var trainer: String = "",
    var schedule: ArrayList<String> = ArrayList(),
    var expected_end_date: Timestamp = Timestamp.now(),
    var image_link: String = "",
    var user: String = ""
)

data class Programme(
    var programme_name: String = "",
    var image_link: String = "",
    var description: String = "",
    var trainers: ArrayList<DocumentReference> = ArrayList(),
    var difficulty: String = "",
    var program_duration: Int = 0,
    var session_duration: Int = 0,
    var ferquency: Int = 0,
    var venue: String = "",
    var other_detail: String = "",
    var warmup: Int = 0,
    var workout: Int = 0,
    var cooldown: Int = 0,
    var price: Int = 0,
): Serializable

data class Exercise(
    val name: String,
    val exercise_id: Int,
    val description: String,
    val target_muscle: String,
    val alternative: String,
    val videoUrl: String,
    val imageUrl: String
): Serializable

data class UserExercise(
    var exercise_name: String = "",
    var image_link: String = "",
    var comment_author: String = "",
    var comment_content: String = "",
    var comment_date: Timestamp = Timestamp.now(),
    var quality: ArrayList<Int> = ArrayList(),
    var volume: ArrayList<Int> = ArrayList(),
    var weight: ArrayList<Int> = ArrayList(),
    var user: String = "",
)

data class Calendar(
    var name: String = "",
    var from: Timestamp = Timestamp.now(),
    var to: Timestamp = Timestamp.now(),
    var location: String = "",
    var user: String = "",
    var trainer: String = "",
)

data class SocialMedia(
    val name: String,
    val url: String,
    val image: Int,
)

data class Photo(
    val url: String,
)
