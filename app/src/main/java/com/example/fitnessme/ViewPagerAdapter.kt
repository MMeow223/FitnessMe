package com.example.fitnessme

import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ViewPagerAdapter(appCompatActivity: MainActivity, private val tabTiles:Array<String>)
    : FragmentStateAdapter(appCompatActivity){

    private val user = Firebase.auth.currentUser

    private val db = Firebase.firestore
    private var userFromDB = User()

    override fun getItemCount(): Int = tabTiles.size

    override fun createFragment(position: Int): Fragment {

        db.collection("user")
            .whereEqualTo("email", user?.email)
            .get()
            .addOnSuccessListener { result ->
                userFromDB = DBHelper.convertDocumentSnapshotToUser(result.documents[0])
//                userFromDB = result.documents[0].toObject(User::class.java)!!
            }

        when (position) {
            0 -> return DashboardFragment()
            1 -> {
                if (userFromDB.role == "Trainer") {
                    return TraineeFragment()
                } else {
                    return ExerciseFragment()
                }
            }
            2 ->{
                if (userFromDB.role == "Trainer") {
                    return ProfileFragment()
                } else {
                    return ProgrammeFragment()
                }
            }

            3 -> return ProfileFragment()

            else -> return TraineeFragment()
        }
    }

}
