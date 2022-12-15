package com.example.fitnessme

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.Nullable
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import kotlin.collections.ArrayList

class ExerciseFragment : Fragment() {

    private lateinit var recyclerView1: androidx.recyclerview.widget.RecyclerView
    private lateinit var cardView: CardView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout


    private val db = FirebaseFirestore.getInstance()
    var exerciseList: ArrayList<Exercise> = ArrayList<Exercise>()
    var userExerciseList: ArrayList<UserExercise> = ArrayList<UserExercise>()
    private lateinit var button: Button

    private val user = Firebase.auth.currentUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exercise, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        updateUI()

        cardView.setOnClickListener{
            val intent = Intent(activity, ExploreExerciseActivity::class.java)
            startActivity(intent)
        }

        button.setOnClickListener{
            val intent = Intent(activity, AddUserExerciseActivity::class.java)
            startActivity(intent)
        }

        swipeRefreshLayout.setOnRefreshListener {
            updateUI()
            swipeRefreshLayout.isRefreshing = false
        }

    }

    private fun initUI(){
        val actionbar: android.app.ActionBar? = activity?.actionBar
        val colorDrawable = ColorDrawable(Color.parseColor("#FFFFFF"))
        actionbar?.setBackgroundDrawable(colorDrawable)
        actionbar?.setTitle(Html.fromHtml("<font color='black'> Exercise </font>"))

        swipeRefreshLayout = view?.findViewById(R.id.swipeRefreshLayout)!!
        recyclerView1 = view?.findViewById(R.id.recyclerView1)!!
        cardView = view?.findViewById(R.id.cardView1)!!
        button = view?.findViewById(R.id.button)!!
    }

    private fun updateUI(){

        val manager = LinearLayoutManager(activity)
        recyclerView1.layoutManager = manager
        userExerciseList = ArrayList<UserExercise>()
        getCurrentUserExercise()
    }

    private fun getCurrentUserExercise() {

        db.collection("user_exercise")
            .whereEqualTo("user", user?.email)
            .get()
            .addOnSuccessListener { result ->

                for(document in result){
                    userExerciseList.add(document.toObject(UserExercise::class.java))
                }
                recyclerView1.adapter = ExerciseAdapter(userExerciseList)

            }
    }

    private fun loadToRecyclerView() {
//        println(userExerciseList.size)
//        println(userExerciseList)

        recyclerView1.adapter = ExerciseAdapter(userExerciseList)
    }
    companion object {

        @JvmStatic
        fun newInstance() = ExerciseFragment()
    }
}