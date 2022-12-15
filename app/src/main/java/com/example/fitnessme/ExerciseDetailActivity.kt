package com.example.fitnessme

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.ZonedDateTime.parse
import java.util.concurrent.Executors
import java.util.logging.Level.parse

class ExerciseDetailActivity : AppCompatActivity() {

    private lateinit var lineChart1: LineChart
    private lateinit var lineChart2: LineChart
    private lateinit var lineChart3: LineChart

    private lateinit var imageView: ImageView

    private lateinit var textView1: TextView
    private lateinit var textView2: TextView
    private lateinit var textView3: TextView
    private lateinit var textView4: TextView
    private lateinit var textView5: TextView

    private lateinit var button: Button

    private var userExercise = UserExercise()

    private var db = Firebase.firestore
    private val user = Firebase.auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_detail)

        supportActionBar?.hide()
        initUI()

        loadExercise()

        button.setOnClickListener {
            showAddRecordPopUpWindow()
        }
    }

    fun showAddRecordPopUpWindow() {

        val inflater = LayoutInflater.from(this)
        val popupView: View = inflater?.inflate(R.layout.pop_up_add_exercise_record, null)!!

        var popupEditText1 = popupView.findViewById<EditText>(R.id.editText1)
        var popupEditText2 = popupView.findViewById<EditText>(R.id.editText2)
        var popupEditText3 = popupView.findViewById<EditText>(R.id.editText3)
        var popupEditText4 = popupView.findViewById<EditText>(R.id.editText4)
        var popupEditText5 = popupView.findViewById<EditText>(R.id.editText5)

        var popupButton = popupView.findViewById<Button>(R.id.button)

        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true // lets taps outside the popup also dismiss it
        val popupWindow = PopupWindow(popupView, width, height, focusable)

        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)

        val container = popupWindow.contentView.rootView
        val context = popupWindow.contentView.context
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val p = container.layoutParams as WindowManager.LayoutParams
        p.flags = p.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
        p.dimAmount = 0.5f
        wm.updateViewLayout(container, p)

        popupButton.setOnClickListener{
            db.collection("user_exercise")
                .whereEqualTo("user", userExercise.user)
                .whereEqualTo("exercise_name", userExercise.exercise_name)
                .get()
                .addOnSuccessListener { result ->

                    var email = userExercise.user
                    var volume = popupEditText1.text.toString().toInt() *
                            popupEditText2.text.toString().toInt() *
                            popupEditText3.text.toString().toInt()

                    var weightArray = arrayListOf<Int>()
                    weightArray = result.documents[0].get("weight") as ArrayList<Int>
                    weightArray.add(popupEditText1.text.toString().toInt())

                    var volumeArray = arrayListOf<Int>()
                    volumeArray = result.documents[0].get("volume") as ArrayList<Int>
                    volumeArray.add(volume)

                    var qualityArray = arrayListOf<Int>()
                    qualityArray = result.documents[0].get("quality") as ArrayList<Int>
                    qualityArray.add(popupEditText4.text.toString().toInt())



                    db.collection("user")
                        .whereEqualTo("email",user?.email )
                        .get()
                        .addOnSuccessListener {

                            result.documents[0].reference.update("weight", weightArray)
                            result.documents[0].reference.update("volume", volumeArray)
                            result.documents[0].reference.update("quality", qualityArray)
                            result.documents[0].reference.update("comment_content", popupEditText5.text.toString())
                            result.documents[0].reference.update("comment_author", it.documents[0].get("firstname").toString() + it.documents[0].get("lastname").toString())
                            result.documents[0].reference.update("comment_date", Timestamp.now())

                            loadExercise()
                            updateUI()

                        }

                }
            popupWindow.dismiss()
        }

    }
    private fun initUI(){
        lineChart1 = findViewById(R.id.lineChart1)
        lineChart2 = findViewById(R.id.lineChart2)
        lineChart3 = findViewById(R.id.lineChart3)

        imageView = findViewById(R.id.imageView)

        textView1 = findViewById(R.id.textView1)
        textView2 = findViewById(R.id.textView2)
        textView3 = findViewById(R.id.textView3)
        textView4 = findViewById(R.id.textView4)
        textView5 = findViewById(R.id.textView5)

        button = findViewById(R.id.button)

        initLineChart1()
        initLineChart2()
        initLineChart3()
    }

    private fun loadExercise(){
        db.collection("user_exercise")
//            .whereEqualTo("exercise_name", "Bench Press")
            .whereEqualTo("exercise_name", intent.getStringExtra("exercise_name"))
            .get()
            .addOnSuccessListener { result ->

                userExercise = DBHelper.convertQuerySnapshotToUserExercise(result)

                updateUI()

            }
    }

    private fun updateUI() {
        // image view
        val handler = Handler(Looper.getMainLooper())
        var image: Bitmap? = null
        Executors.newSingleThreadExecutor().execute {
            try {
                val `in` = java.net.URL(userExercise.image_link).openStream()
                image = BitmapFactory.decodeStream(`in`)
                handler.post {
                    imageView.setImageBitmap(image)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // convert firebase timestamp to formatted date time string
        val date = userExercise.comment_date.toDate()
        val formattedDate = android.text.format.DateFormat.format("dd/MM/yyyy", date)
        val formattedTime = android.text.format.DateFormat.format("HH:mm", date)

        textView1.text = userExercise.exercise_name
        if (!userExercise.weight.isEmpty()){
            textView2.text = userExercise.weight[0].toString()
        }
        else{
            textView2.text = ""
        }
        if (!userExercise.weight.isEmpty()){
            textView2.text = userExercise.weight[0].toString()
            textView3.text = userExercise.weight[userExercise.weight.size - 1].toString()
        }
        else{
            textView2.text = ""
            textView3.text = ""
        }
        textView4.text = "$formattedDate $formattedTime by ${userExercise.comment_author}"
        textView5.text = userExercise.comment_content

        updateLineChart1()
        updateLineChart2()
        updateLineChart3()

    }
    private fun initLineChart1(){
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
        lineChart1.legend.isEnabled = false

        lineChart1.setTouchEnabled(false)
        lineChart1.setPinchZoom(false)
        lineChart1.setDrawGridBackground(false)
        lineChart1.setDrawBorders(false)
        lineChart1.setDrawMarkers(false)
    }

    private fun initLineChart2(){
        // remove grid behing line
        lineChart2.xAxis.setDrawGridLines(false)
        lineChart2.axisLeft.gridLineWidth = 2f

        lineChart2.axisLeft.gridColor = resources.getColor(R.color.grey)
        lineChart2.axisRight.gridColor = resources.getColor(R.color.grey)


        lineChart2.axisLeft.setDrawAxisLine(false)
        lineChart2.axisRight.setDrawAxisLine(false)
        lineChart2.xAxis.setDrawAxisLine(false)

        lineChart2.xAxis.setDrawLabels(false)
        lineChart2.axisLeft.setDrawLabels(false)
        lineChart2.axisRight.setDrawLabels(false)

        lineChart2.description.isEnabled = false
        lineChart2.legend.isEnabled = false

        lineChart2.setTouchEnabled(false)
        lineChart2.setPinchZoom(false)
        lineChart2.setDrawGridBackground(false)
        lineChart2.setDrawBorders(false)
        lineChart2.setDrawMarkers(false)
    }

    private fun initLineChart3(){
        // remove grid behing line
        lineChart3.xAxis.setDrawGridLines(false)
        lineChart3.axisLeft.gridLineWidth = 2f

        lineChart3.axisLeft.gridColor = resources.getColor(R.color.grey)
        lineChart3.axisRight.gridColor = resources.getColor(R.color.grey)


        lineChart3.axisLeft.setDrawAxisLine(false)
        lineChart3.axisRight.setDrawAxisLine(false)
        lineChart3.xAxis.setDrawAxisLine(false)

        lineChart3.xAxis.setDrawLabels(false)
        lineChart3.axisLeft.setDrawLabels(false)
        lineChart3.axisRight.setDrawLabels(false)

        lineChart3.description.isEnabled = false
        lineChart3.legend.isEnabled = false

        lineChart3.setTouchEnabled(false)
        lineChart3.setPinchZoom(false)
        lineChart3.setDrawGridBackground(false)
        lineChart3.setDrawBorders(false)
        lineChart3.setDrawMarkers(false)
    }

    private fun updateLineChart1() {
        val entries : ArrayList<Entry> = ArrayList()

        for (i in 0 until userExercise.weight.size) {
            entries.add(Entry(i.toFloat(), userExercise.weight[i].toFloat()))
        }

        val dataSet = LineDataSet(entries, "Weight")

        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.cubicIntensity = 0.2f
        dataSet.setDrawFilled(true)
        dataSet.lineWidth = 1.8f
        dataSet.circleRadius = 4f
        dataSet.setColor(resources.getColor(R.color.orange))
        dataSet.setValueTextColor(resources.getColor(R.color.orange))
        dataSet.fillColor = resources.getColor(R.color.orange)
        dataSet.valueTextSize = 10f

        val lineData = LineData(dataSet)
        if(userExercise.weight.size > 0){
            lineChart1.data = lineData
        }
//        lineChart1.setData(lineData)
        lineChart1.invalidate() // refresh

    }

    private fun updateLineChart2() {
        val entries : ArrayList<Entry> = ArrayList()

        for (i in 0 until userExercise.volume.size) {
            entries.add(Entry(i.toFloat(), userExercise.volume[i].toFloat()))
        }

        val dataSet = LineDataSet(entries, "Volume")

        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.cubicIntensity = 0.2f
        dataSet.setDrawFilled(true)
        dataSet.lineWidth = 1.8f
        dataSet.circleRadius = 4f
        dataSet.setColor(resources.getColor(R.color.orange))
        dataSet.setValueTextColor(resources.getColor(R.color.orange))
        dataSet.fillColor = resources.getColor(R.color.orange)
        dataSet.valueTextSize = 10f

        val lineData = LineData(dataSet)
        if(userExercise.volume.size > 0){
            lineChart2.setData(lineData)
        }
        lineChart2.invalidate() // refresh

    }

    private fun updateLineChart3() {
        val entries : ArrayList<Entry> = ArrayList()

        for (i in 0 until userExercise.quality.size) {
            entries.add(Entry(i.toFloat(), userExercise.quality[i].toFloat()))
        }

        val dataSet = LineDataSet(entries, "Quality")

        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.cubicIntensity = 0.2f
        dataSet.setDrawFilled(true)
        dataSet.lineWidth = 1.8f
        dataSet.circleRadius = 4f
        dataSet.setColor(resources.getColor(R.color.orange))
        dataSet.setValueTextColor(resources.getColor(R.color.orange))
        dataSet.fillColor = resources.getColor(R.color.orange)
        dataSet.valueTextSize = 10f

        val lineData = LineData(dataSet)
        if(userExercise.quality.size > 0){
            lineChart3.setData(lineData)
        }
        lineChart3.invalidate() // refresh

    }

}