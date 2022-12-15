package com.example.fitnessme

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.view.marginLeft
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProgrammeFragment : Fragment() {

    private lateinit var recyclerView1: RecyclerView
    private lateinit var recyclerView2: RecyclerView
    private lateinit var recyclerView3: RecyclerView
    private lateinit var recyclerView4: RecyclerView

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

//    private lateinit var cardView1: CardView

    private val user = Firebase.auth.currentUser

    private val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_programme, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val actionbar: android.app.ActionBar? = activity?.actionBar
        val colorDrawable = ColorDrawable(Color.parseColor("#FFFFFF"))
        actionbar?.setBackgroundDrawable(colorDrawable)
        actionbar?.setTitle(Html.fromHtml("<font color='black'> Programme </font>"))

        initUI()

        updateUI()

//        cardView1.setOnClickListener{
//            val intent = Intent(context, SeeMoreTrainerActivity::class.java)
//            startActivity(intent)
////            seeMore()
//        }

        swipeRefreshLayout.setOnRefreshListener {

            updateUI()
            swipeRefreshLayout.isRefreshing = false

        }
    }

    private fun initUI(){
        recyclerView1 = requireView().findViewById(R.id.recyclerView1)
        recyclerView2 = requireView().findViewById(R.id.recyclerView2)
        recyclerView3 = requireView().findViewById(R.id.recyclerView3)
        recyclerView4 = requireView().findViewById(R.id.recyclerView4)

        swipeRefreshLayout = requireView().findViewById(R.id.swipeRefreshLayout)
//        cardView1 = requireView().findViewById(R.id.cardView1)
    }

    private fun updateUI(){
        val workoutList = ArrayList<Programme>()
        val trainerList = ArrayList<User>()
        val currentProgrammeList = ArrayList<UserProgramme>()
        val previousProgrammeList = ArrayList<UserProgramme>()


        db.collection("programme").get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    workoutList.add(document.toObject(Programme::class.java))
                }
                val manager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                recyclerView1.layoutManager = manager
                recyclerView1.adapter = WorkoutTrendAdapter(workoutList)
            }

        db.collection("user")
            .whereEqualTo("role", "Trainer")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    trainerList.add(document.toObject(User::class.java))
                }
                val manager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                recyclerView2.layoutManager = manager
                recyclerView2.adapter = TrainerAdapter(trainerList)
            }

        // TODO suppose get from user not directly here
        db.collection("user_programme")
//            .whereLessThan("expected_end_date", Timestamp.now())
            .whereEqualTo("user", user?.email)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if(document.data["expected_end_date"] as Timestamp >= Timestamp.now()){
                        currentProgrammeList.add(document.toObject(UserProgramme::class.java))
                    }
//                currentProgrammeList.add(document.toObject(UserProgramme::class.java))
                }
                val manager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                recyclerView3.layoutManager = manager
                recyclerView3.adapter = ProgrammeAdapter(currentProgrammeList)
            }


        // TODO suppose get from user not directly here
        db.collection("user_programme")
//            .whereGreaterThan("expected_end_date", Timestamp.now())
            .whereEqualTo("user", user?.email)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if(document.data["expected_end_date"] as Timestamp <= Timestamp.now()){
                        previousProgrammeList.add(document.toObject(UserProgramme::class.java))
                    }
//                    previousProgrammeList.add(document.toObject(UserProgramme::class.java))
                }
                val manager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                recyclerView4.layoutManager = manager
                recyclerView4.adapter = ProgrammeAdapter(previousProgrammeList)
            }

        val manager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        val manager2 = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        val manager3 = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        val manager4 = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView1.layoutManager = manager
        recyclerView1.adapter = WorkoutTrendAdapter(workoutList)

        recyclerView2.layoutManager = manager2
        recyclerView2.adapter = TrainerAdapter(trainerList)

        recyclerView3.layoutManager = manager3
        recyclerView3.adapter = ProgrammeAdapter(currentProgrammeList)

        recyclerView4.layoutManager = manager4
        recyclerView4.adapter = ProgrammeAdapter(previousProgrammeList)
    }

    companion object {

        @JvmStatic
        fun newInstance() =  ProgrammeFragment()
    }
}