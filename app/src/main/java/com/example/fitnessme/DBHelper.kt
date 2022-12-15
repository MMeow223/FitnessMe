package com.example.fitnessme

import android.net.Uri
import android.os.Build
import android.os.Handler
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.google.type.Date
import com.google.type.DateTime
import kotlinx.coroutines.tasks.await
import java.time.Instant.now
import java.time.LocalDateTime
import java.time.LocalDateTime.now

class DBHelper {
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    //constructor
    constructor() {
        createDBInstance()
        createStorageInstance()
    }

    private fun createDBInstance(){
        db = Firebase.firestore
    }

    private fun createStorageInstance(){
        storage = Firebase.storage("gs://fitnessme-8fe4a.appspot.com")
    }



    fun createExercise(exercise: Exercise){
//        fun createExercise(id:Int,alternative: String, description: String, name: String,imageUrl:String, videoUrl: String){
        // Create a new user with a first and last name
        val user = hashMapOf(
            "alternative" to exercise.alternative,
            "description" to exercise.description,
            "exercise_id" to exercise.exercise_id,
            "exercise_name" to exercise.name,
            "image_link" to exercise.imageUrl,
            "video_link" to exercise.videoUrl,
        )

        db.collection("exercises").add(user)

    }
    fun getAllExercises(): ArrayList<Exercise>{

        // await for the result
        val exercises = ArrayList<Exercise>()
        db.collection("exercises").get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val exercise = Exercise(
                        document.data["exercise_name"].toString(),
                        document.data["exercise_id"].toString().toInt(),
                        document.data["description"].toString(),
                        document.data["target_muscle"].toString(),
                        document.data["alternative"].toString(),
                        document.data["video_link"].toString(),
                        document.data["image_link"].toString()
                    )
                    exercises.add(exercise)

                }
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }

        return exercises

    }




    fun uploadFile(file:Uri){
        val storageRef = storage.reference

        val uuid = java.util.UUID.randomUUID().toString()

        val riversRef = storageRef.child("images/${uuid}")
       riversRef.putFile(file)
    }

    companion object {

        fun createProgramme(programme: Programme){

//            var programme_name: String = "",
//            var image_link: String = "",
//            var description: String = "",
//            var trainers: ArrayList<User> = ArrayList(),
//            var difficulty: String = "",
//            var program_duration: Int = 0,
//            var session_duration: Int = 0,
//            var ferquency: Int = 0,
//            var venue: String = "",
//            var other_detail: String = "",
//            var warmup: Int = 0,
//            var workout: Int = 0,
//            var cooldown: Int = 0,
//            var price: Int = 0,
            val db = Firebase.firestore
            val programme = hashMapOf(
                "programme_name" to programme.programme_name,
                "image_link" to programme.image_link,
                "description" to programme.description,
                "trainers" to programme.trainers,
                "difficulty" to programme.difficulty,
                "program_duration" to programme.program_duration,
                "session_duration" to programme.session_duration,
                "ferquency" to programme.ferquency,
                "venue" to programme.venue,
                "other_detail" to programme.other_detail,
                "warmup" to programme.warmup,
                "workout" to programme.workout,
                "cooldown" to programme.cooldown,
                "price" to programme.price,
            )
            // Add a new document with a generated ID
            db.collection("programme").add(programme)
        }

        fun createUserProgramme(){

            val db = Firebase.firestore

//            var programmeObj: Programme = Programme()
            var programme: HashMap<String, Any> = HashMap()

//            db.collection("programme")
//                .whereEqualTo("programme_name", "Beginner Gym")
//                .get()
//                .addOnSuccessListener { result ->
//                    for (document in result) {
//
//                        // document to object
//                        programmeObj = document.toObject(Programme::class.java)
//                    }

            programme = hashMapOf(
                "programme_name" to "Beginner Gym",
                "trainer" to "Andrew Tate",
                "schedule" to ArrayList<String>(),
                "expected_end_date" to Timestamp.now() ,
                "image_link" to "https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/woman-doing-barbell-lunges-during-workout-royalty-free-image-1642082324.jpg?crop=0.646xw:0.969xh;0.192xw,0.00777xh&resize=640:*",
                "user" to "elviswong2002@icloud.com"
                )
            // generate uid for the document
//            val uuid = java.util.UUID.randomUUID().toString()

//            val path = db.collection("user_programme").document(uuid)
            db.collection("user_programme").add(programme)

//            val user = Firebase.auth.currentUser

//            if (user != null) {
//                db.collection("user")
//                    .whereEqualTo("email", user.email)
//                    .get()
//                    .addOnSuccessListener { result ->
//
//                        result.documents[0].reference.update("current_programmes", FieldValue.arrayUnion(path))
//                    }
//            }
//                }
//                .addOnFailureListener { exception ->
//                    println("Error getting documents: $exception")
//                }




        }

        fun createUserExercise(userExercise: UserExercise): HashMap<String, Any> {

            val db = Firebase.firestore
//            val exercise = hashMapOf(
//                "exercise_name" to "Dead Lift",
//                "image_link" to "https://firebasestorage.googleapis.com/v0/b/fitnessme-8fe4a.appspot.com/o/exercise%2FGuide-to-Deadlifts-GettyImages-1368073669-9492fe76328041169af7baf93afe1bc5.jpg?alt=media&token=eed237e8-de2c-452e-8533-375146e1dcff",
//                "comment_author" to "John",
//                "comment_content" to "This is a comment",
//                "comment_date" to Timestamp.now(),
//                "quality" to arrayListOf(70,50,80,84,91),
//                "volume" to arrayListOf(144,189,189,288,150),
//                "weight" to arrayListOf(40,50,70,20,30),
//            )

            val exercise = hashMapOf(
                "exercise_name" to userExercise.exercise_name,
                "image_link" to userExercise.image_link,
                "comment_author" to  userExercise.comment_author,
                "comment_content" to  userExercise.comment_content,
                "comment_date" to  userExercise.comment_date,
                "quality" to  userExercise.quality,
                "volume" to  userExercise.volume,
                "weight" to  userExercise.weight,
                "user" to userExercise.user,
            )
            return exercise

            // generate uid for the document
//            val uuid = java.util.UUID.randomUUID().toString()
//
//            val path = db.collection("user_exercise").document(uuid)
//            db.collection("user_exercise").document(uuid).set(exercise)
//
//            db.collection("user")
//                .whereEqualTo("email", "elviswong2002@icloud.com")
//                .get()
//                .addOnSuccessListener { result ->
//
//                    result.documents[0].reference.update("exercises", FieldValue.arrayUnion(path))
////                    for (document in result) {
////                        document.reference.update("exercises", FieldValue.arrayUnion(path))
////
////                    }
//                }

//            var userexercisepath = db.collection("user_exercise").add(exercise).getResult().path



//            // add the document reference to user exercises
//            val userRef = db.collection("user").document("user_id")
//            userRef.update("exercises", arrayListOf("user_exercise/1","user_exercise/2"))





        }




        fun convertQuerySnapshotToExerciseList(querySnapshot: QuerySnapshot): ArrayList<Exercise> {
            val exercises = ArrayList<Exercise>()
            for (document in querySnapshot) {
                val exercise = Exercise(
                    document.data["exercise_name"].toString(),
                    document.data["exercise_id"].toString().toInt(),
                    document.data["description"].toString(),
                    document.data["target_muscle"].toString(),
                    document.data["alternative"].toString(),
                    document.data["video_link"].toString(),
                    document.data["image_link"].toString()
                )
                exercises.add(exercise)
            }
            return exercises
        }


        fun convertDocumentSnapshotToUser(documentSnapshot: DocumentSnapshot): User {

            var user = User()
            user.firstname = documentSnapshot.data!!["firstname"] as String
            user.firstname = documentSnapshot.data!!["firstname"].toString()
            user.lastname = documentSnapshot.data!!["lastname"].toString()
            user.gender = documentSnapshot.data!!["gender"].toString()
            user.phone = documentSnapshot.data!!["phone"].toString()
            user.email = documentSnapshot.data!!["email"].toString()
            user.age = documentSnapshot.data!!["age"].toString().toInt()

            user.heights = documentSnapshot.data!!["heights"] as ArrayList<Float>
            user.weights = documentSnapshot.data!!["weights"] as ArrayList<Float>
            user.strengths = documentSnapshot.data!!["strengths"] as ArrayList<Float>
            user.diseases = documentSnapshot.data!!["diseases"].toString()
            user.goal = documentSnapshot.data!!["goal"].toString()

            user.exercises = documentSnapshot.data!!["exercises"] as ArrayList<DocumentReference>
            user.profile_image_link = documentSnapshot.data!!["profile_image_link"].toString()
            user.image_links = documentSnapshot.data!!["image_links"] as ArrayList<String>
            user.role = documentSnapshot.data!!["role"].toString()

            user.region = documentSnapshot.data!!["region"].toString()
            user.field = documentSnapshot.data!!["field"].toString()
            user.description = documentSnapshot.data!!["description"].toString()
            user.rating = documentSnapshot.data!!["rating"].toString().toInt()
            user.expected_fee = documentSnapshot.data!!["expected_fee"].toString().toInt()
            user.social_medias = documentSnapshot.data!!["social_medias"] as ArrayList<String>
            user.coaching_experience = documentSnapshot.data!!["coaching_experience"].toString().toInt()
            user.trainees = documentSnapshot.data!!["trainees"] as ArrayList<User>
            user.active_trainee = documentSnapshot.data!!["active_trainee"].toString().toInt()
            user.accumulated_trainee = documentSnapshot.data!!["accumulated_trainee"].toString().toInt()

            user.trainer = documentSnapshot.data!!["trainer"].toString()

            user.account_created_completed = documentSnapshot.data!!["account_created_completed"] as Boolean

            return user
        }

        fun convertQuerySnapshotToUsers(querySnapshot: QuerySnapshot): ArrayList<User> {
            var userList = ArrayList<User>()

            for (document in querySnapshot) {
                var user = User()
                user.firstname = document.data["firstname"].toString()
                user.lastname = document.data["lastname"].toString()
                user.gender = document.data["gender"].toString()
                user.phone = document.data["phone"].toString()
                user.email = document.data["email"].toString()
                user.age = document.data["age"].toString().toInt()
                user.heights = document.data["heights"] as ArrayList<Float>
                user.weights = document.data["weights"] as ArrayList<Float>
                user.strengths = document.data["strengths"] as ArrayList<Float>

                user.diseases = document.data["diseases"].toString()
                user.goal = document.data["goal"].toString()
                user.exercises = document.data["exercises"] as ArrayList<DocumentReference>

                user.profile_image_link = document.data["profile_image_link"].toString()
                user.image_links = document.data["image_links"] as ArrayList<String>
                user.role = document.data["role"].toString()
                user.region = document.data["region"].toString()

                user.field = document.data["field"].toString()
                user.description = document.data["description"].toString()
                user.rating = document.data["rating"].toString().toInt()
                user.expected_fee = document.data["expected_fee"].toString().toInt()
                user.social_medias = document.data["social_medias"] as ArrayList<String>
                user.coaching_experience = document.data["coaching_experience"].toString().toInt()
                user.trainees = document.data["trainees"] as ArrayList<User>
                user.active_trainee = document.data["active_trainee"].toString().toInt()
                user.accumulated_trainee = document.data["accumulated_trainee"].toString().toInt()

                user.trainer = document.data["trainer"].toString()
                user.account_created_completed = document.data["account_created_completed"] as Boolean

                userList.add(user)

            }
            return userList
        }

        fun convertQuerySnapshotToUserExercise(querySnapshot: QuerySnapshot): UserExercise {

            var userExercise = UserExercise()

            for (document in querySnapshot) {
                userExercise = UserExercise(
                    document.data["exercise_name"].toString(),
                    document.data["image_link"].toString(),
                    document.data["comment_author"].toString(),
                    document.data["comment_content"].toString(),
                    document.data["comment_date"] as Timestamp,
                    document.data["quality"] as ArrayList<Int>,
                    document.data["volume"] as ArrayList<Int>,
                    document.data["weight"] as ArrayList<Int>,
                    document.data["user"].toString()
                )
            }

            return userExercise
        }

        fun createUser(user: User){
            val db = Firebase.firestore

            val user = hashMapOf(
                "firstname" to user.firstname,
                "lastname" to user.lastname,
                "gender" to user.gender,
                "phone" to user.phone,
                "email" to user.email,
                "age" to user.age,
                "heights" to user.heights,
                "strengths" to user.strengths,
                "weights" to user.weights,
                "diseases" to user.diseases,
                "goal" to user.goal,
                "field" to user.field,

                "profile_image_link" to user.profile_image_link,
                "image_links" to user.image_links,
                "role" to user.role,
                "region" to user.region,

                "exercises" to user.exercises,
                "current_programmes" to user.current_programmes,
                "previous_programmes" to user.previous_programmes,
                "calendars" to user.calendars,

                "description" to user.description,
                "rating" to user.rating,
                "expected_fee" to user.expected_fee,
                "social_medias" to user.social_medias,
                "coaching_experience" to user.coaching_experience,
                "trainees" to user.trainees,
                "active_trainee" to user.active_trainee,
                "accumulated_trainee" to user.accumulated_trainee,

                "trainer" to user.trainer,
                "account_created_completed" to false,

                )

            // Add a new document with a generated ID
            db.collection("user").add(user)

        }
    }

}