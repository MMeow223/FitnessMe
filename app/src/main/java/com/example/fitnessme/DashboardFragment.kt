package com.example.fitnessme

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.*
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.type.DateTime
import java.sql.Time


class DashboardFragment : Fragment() {


    private lateinit var recyclerView1: RecyclerView
    private lateinit var lineChart1: LineChart
    private lateinit var barChart1: BarChart
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private val db = Firebase.firestore
    private val user = Firebase.auth.currentUser

    private lateinit var textView1: TextView

    private var userData: User = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView1 = view.findViewById(R.id.recyclerView1)
        initUI()



//        var min = 300
//
//        var toTimestamp = Timestamp(Timestamp.now().seconds + (min * 60), 0)
//        val calendarList = ArrayList<Calendar>()
//        calendarList.add(Calendar(name="Gym Session", from=Timestamp.now(), to=toTimestamp,location="Swinburne Angelus Gym"))
//        calendarList.add(Calendar(name="Yoga Session", from=Timestamp.now(), to=toTimestamp,location="Yoga Blizz Studio"))

        val manager = LinearLayoutManager(activity)
        recyclerView1.layoutManager = manager

        getCurrentUser()

//        if(userData.role == "Trainer"){
//            db.collection("user_calendar")
//                .whereEqualTo("trainer", user?.email)
//                .get()
//                .addOnSuccessListener {
//                    val calendarList = ArrayList<Calendar>()
//                    for (document in it) {
//                        val calendar = document.toObject(Calendar::class.java)
//                        calendarList.add(calendar)
//                    }
//                    val adapter = CalendarAdapter(calendarList)
//                    recyclerView1.adapter = adapter
//                    recyclerView1.layoutManager = LinearLayoutManager(context)
//                }
//                .addOnFailureListener {
//                    Log.d("TAG", "Error getting documents: ", it)
//                }
//        }
//        else{
//            db.collection("user_calendar")
//                .whereEqualTo("user", user?.email)
//                .get()
//                .addOnSuccessListener {
//                    val calendarList = ArrayList<Calendar>()
//                    for (document in it) {
//                        val calendar = document.toObject(Calendar::class.java)
//                        calendarList.add(calendar)
//                    }
//                    val adapter = CalendarAdapter(calendarList)
//                    recyclerView1.adapter = adapter
//                    recyclerView1.layoutManager = LinearLayoutManager(context)
//                }
//                .addOnFailureListener {
//                    Log.d("TAG", "Error getting documents: ", it)
//                }
//        }



        swipeRefreshLayout.setOnRefreshListener {
            getCurrentUser()
            swipeRefreshLayout.isRefreshing = false
        }

    }



    private fun getCurrentUser() {

        // query for user that have the same email as the current user
        db.collection("user")
            .whereEqualTo("email",user?.email )
            .get()
            .addOnFailureListener { exception ->
                 Log.d(TAG, "Error getting documents: ", exception)
            }
            .addOnSuccessListener { result ->
                Log.d(TAG, "Success getting documents: ")

                userData =  DBHelper.convertDocumentSnapshotToUser(result.documents[0])

                updateUI()

            }

    }

    private fun initUI(){
        textView1 = view?.findViewById(R.id.textView1)!!
        initLineChart()
        initBarChart()
        swipeRefreshLayout = view?.findViewById(R.id.swipeRefreshLayout)!!
    }


    private fun initLineChart(){
        lineChart1 = view?.findViewById(R.id.lineChart1)!!
        // remove grid behing line

        lineChart1.xAxis.setDrawGridLines(false)
        lineChart1.axisLeft.gridLineWidth = 2f

        lineChart1.axisLeft.gridColor = resources.getColor(R.color.grey)
        lineChart1.axisRight.gridColor = resources.getColor(R.color.grey)

        lineChart1.axisLeft.setDrawAxisLine(false)
        lineChart1.axisRight.setDrawAxisLine(false)
        lineChart1.xAxis.setDrawAxisLine(false)

        lineChart1.xAxis.setDrawLabels(false)
        lineChart1.axisLeft.setDrawLabels(false)
        lineChart1.axisRight.setDrawLabels(false)

        lineChart1.description.isEnabled = false
        lineChart1.legend.isEnabled = true
        lineChart1.setTouchEnabled(false)
        lineChart1.setPinchZoom(false)
        lineChart1.setDrawGridBackground(false)
        lineChart1.setDrawBorders(false)
        lineChart1.setDrawMarkers(false)
    }

    private fun initBarChart() {
        barChart1 = view?.findViewById(R.id.barChart1)!!

        // remove grid behing line
        barChart1.xAxis.setDrawGridLines(false)
        barChart1.axisLeft.gridLineWidth = 2f

        barChart1.axisLeft.gridColor = resources.getColor(R.color.grey)
        barChart1.axisRight.gridColor = resources.getColor(R.color.grey)

        barChart1.axisLeft.setDrawAxisLine(false)
        barChart1.axisRight.setDrawAxisLine(false)
        barChart1.xAxis.setDrawAxisLine(false)

        barChart1.xAxis.setDrawLabels(false)
        barChart1.axisLeft.setDrawLabels(false)
        barChart1.axisRight.setDrawLabels(false)

        barChart1.description.isEnabled = false
        barChart1.legend.isEnabled = true
        barChart1.setTouchEnabled(false)
        barChart1.setPinchZoom(false)
        barChart1.setDrawGridBackground(false)
        barChart1.setDrawBorders(false)
        barChart1.setDrawMarkers(false)

    }

    private fun updateUI(){
        textView1.text = "Welcome Back, \n${userData.firstname} ${userData.lastname} [${userData.role}]"
        updateLineChart()
        updateBarChart()

        if(userData.role == "Trainer"){
            db.collection("user_calendar")
                .whereEqualTo("trainer", user?.email)
                .get()
                .addOnSuccessListener {
                    val calendarList = ArrayList<Calendar>()
                    for (document in it) {
                        val calendar = document.toObject(Calendar::class.java)
                        calendarList.add(calendar)
                    }
                    val adapter = CalendarAdapter(calendarList)
                    recyclerView1.adapter = adapter
                    recyclerView1.layoutManager = LinearLayoutManager(context)
                }
                .addOnFailureListener {
                    Log.d("TAG", "Error getting documents: ", it)
                }
        }
        else{
            db.collection("user_calendar")
                .whereEqualTo("user", user?.email)
                .get()
                .addOnSuccessListener {
                    val calendarList = ArrayList<Calendar>()
                    for (document in it) {
                        val calendar = document.toObject(Calendar::class.java)
                        calendarList.add(calendar)
                    }
                    val adapter = CalendarAdapter(calendarList)
                    recyclerView1.adapter = adapter
                    recyclerView1.layoutManager = LinearLayoutManager(context)
                }
                .addOnFailureListener {
                    Log.d("TAG", "Error getting documents: ", it)
                }
        }

    }

    private fun updateLineChart(){

        val entries1 : ArrayList<Entry> = ArrayList()
        val entries2 : ArrayList<Entry> = ArrayList()

        //from 0 to weight size
        if(!userData.weights.isEmpty()){
            for (i in userData.weights.indices) {

                var value = userData.weights[i]
                entries1.add(Entry(i.toFloat(), value))
            }
        }

        if(!userData.strengths.isEmpty()){
            for (i in userData.strengths.indices) {

                var value = userData.strengths[i]
                entries2.add(Entry(i.toFloat(), value))
            }
        }

        val dataSet = LineDataSet(entries1, "Body Weight")
        val dataSet2 = LineDataSet(entries2, "Strength")

        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.cubicIntensity = 0.2f
        dataSet.setDrawFilled(true)
        dataSet.lineWidth = 1.8f
        dataSet.circleRadius = 4f
        dataSet.setColor(resources.getColor(R.color.orange))
        dataSet.setValueTextColor(resources.getColor(R.color.orange))
        dataSet.fillColor = resources.getColor(R.color.orange)
        dataSet.valueTextSize = 10f

        dataSet2.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet2.cubicIntensity = 0.2f
        dataSet2.setDrawFilled(true)
        dataSet2.lineWidth = 1.8f
        dataSet2.circleRadius = 4f
        dataSet2.setColor(resources.getColor(R.color.light_green))
        dataSet2.setValueTextColor(resources.getColor(R.color.light_green))
        dataSet2.fillColor = resources.getColor(R.color.light_green)
        dataSet2.fillAlpha = 20
        dataSet2.valueTextSize = 10f

        lineChart1.setData(LineData(dataSet,dataSet2))
        lineChart1.invalidate() // refresh

    }

    private fun updateBarChart(){

        val entries : ArrayList<BarEntry> = ArrayList()

        entries.add(BarEntry(0f,1f))
        entries.add(BarEntry(1f,3f))
        entries.add(BarEntry(2f,1f))
        entries.add(BarEntry(3f,1f))
        entries.add(BarEntry(4f,7f))

        val dataSet = BarDataSet(entries, "Weight")
        dataSet.valueTextSize = 10f

        val barData = BarData(dataSet)
        barChart1.setData(barData)
        barChart1.invalidate() // refresh

    }
    companion object {
        @JvmStatic
        fun newInstance() = DashboardFragment()
    }
}
