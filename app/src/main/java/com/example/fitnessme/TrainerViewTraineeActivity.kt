package com.example.fitnessme

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
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
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akexorcist.snaptimepicker.SnapTimePickerDialog
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

class TrainerViewTraineeActivity : AppCompatActivity() {

    private lateinit var profileImageView: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var phoneTextView: TextView

    private lateinit var cardView1:CardView
    private lateinit var cardView2:CardView
    private lateinit var cardView3:CardView
    private lateinit var cardView4:CardView



    private val db = Firebase.firestore
    private val user = Firebase.auth.currentUser

    private var traineeEmail = ""
    private var traineeName = ""
    private var traineePhone = ""
    private var traineeProfile = ""

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trainer_view_trainee)


        // get bundle
        val bundle: Bundle? = intent.extras
        traineeEmail = bundle?.getString("traineeEmail").toString()
        traineeName = bundle?.getString("traineeName").toString()
        traineePhone = bundle?.getString("traineePhone").toString()
        traineeProfile = bundle?.getString("traineeProfile").toString()

        initUI()

        updateUI()

        // user selections
        var checkInDate: Date? = null
        var checkOutDate: Date? = null
        var roomTypeIndex: Int? = null
        var year: Int = 0
        var month: Int = 0
        var day: Int = 0

        cardView1.setOnClickListener {
            openAddSessionPopUp()
        }

        cardView2.setOnClickListener {
            openAddExercisePopUp()
        }

        cardView3.setOnClickListener {
            openAddExerciseRecordPopUp()
        }

        cardView4.setOnClickListener {

            //intent adn bundle
            val intent = Intent(this, AddUserToProgrammeActivity::class.java)
            intent.putExtra("traineeEmail", traineeEmail)
            startActivity(intent)

        }

    }

    private fun openAddExerciseRecordPopUp(){

        val inflater = LayoutInflater.from(this)
        val popupView: View = inflater?.inflate(R.layout.pop_up_add_exercise, null)!!

        var recyclerView = popupView.findViewById<RecyclerView>(R.id.recyclerView)

        db.collection("user_exercise")
            .whereEqualTo("user", traineeEmail)
            .get()
            .addOnSuccessListener { result ->
                var userExerciseList = ArrayList<UserExercise>()

                for(document in result){
                    userExerciseList.add(document.toObject(UserExercise::class.java))
                }
                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.adapter = ExerciseAdapter(userExerciseList)


            }


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

    }


    private fun openAddExercisePopUp(){

        val inflater = LayoutInflater.from(this)
        val popupView: View = inflater?.inflate(R.layout.pop_up_add_exercise, null)!!

        var recyclerView = popupView.findViewById<RecyclerView>(R.id.recyclerView)

        db.collection("exercises").get()
            .addOnSuccessListener { result ->
                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.adapter = AddUserExerciseAdapter(DBHelper.convertQuerySnapshotToExerciseList(result),traineeEmail)
            }


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

    }


    private fun openAddSessionPopUp(){

        val inflater = LayoutInflater.from(this)
        val popupView: View = inflater?.inflate(R.layout.pop_up_add_session, null)!!

        var textView1 = popupView.findViewById<TextView>(R.id.textView1)
        var textView2 = popupView.findViewById<TextView>(R.id.textView2)
        var textView3 = popupView.findViewById<TextView>(R.id.textView3)

        var editText1 = popupView.findViewById<EditText>(R.id.editText1)
        var editText2 = popupView.findViewById<EditText>(R.id.editText2)

        var button = popupView.findViewById<Button>(R.id.button)

        var year: Int = 0
        var month: Int = 0
        var day: Int = 0
        var dateString:String = ""
        var fromHourString:String = ""
        var fromMinuteString:String = ""
        var toHourString:String = ""
        var toMinuteString:String = ""


        textView1.setOnClickListener{
            val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener
            { view, year, monthOfYear, dayOfMonth ->

                var monthString = monthOfYear.toString()
                var dayString = dayOfMonth.toString()
                if(monthOfYear < 10){
                    monthString = "0$monthOfYear"
                }
                if(dayOfMonth < 10){
                    dayString = "0$dayOfMonth"
                }

                val mSimpleDateFormat2 = SimpleDateFormat("yyyy-MM-dd")
                dateString = mSimpleDateFormat2.format(mSimpleDateFormat2.parse("$year-$monthString-$dayString"))

                textView1.text = "$dateString"


            }, year, month, day)
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        }
        textView2.setOnClickListener{
            val dialog = SnapTimePickerDialog.Builder().build()
            dialog.setListener { hour, minute ->

                if(hour < 10){
                    fromHourString = "0$hour"
                }else{
                    fromHourString = hour.toString()
                }
                if(minute < 10){
                    fromMinuteString = "0$minute"
                }
                else{
                    fromMinuteString = minute.toString()
                }

                textView2.text = "$fromHourString:$fromMinuteString"
            }
            dialog.show(supportFragmentManager, "timePicker")

        }
        textView3.setOnClickListener{
            val dialog = SnapTimePickerDialog.Builder().build()
            dialog.setListener { hour, minute ->

                if(hour < 10){
                    toHourString = "0$hour"
                }
                else{
                    toHourString = hour.toString()
                }
                if(minute < 10){
                    toMinuteString = "0$minute"
                }
                else{
                    toMinuteString = minute.toString()
                }

                textView3.text = "$toHourString:$toMinuteString"
            }
            dialog.show(supportFragmentManager, "timePicker")

        }


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

        button.setOnClickListener{
            // Parsing the date and time
            val mSimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

            var fromtimestamp = Timestamp(mSimpleDateFormat.parse("${dateString}T${fromHourString}:${fromMinuteString}:00"))
            var totimestamp = Timestamp(mSimpleDateFormat.parse("${dateString}T${toHourString}:${toMinuteString}:00"))

            var newCalendar = hashMapOf(
                "trainer" to user?.email.toString(),
                "user" to traineeEmail,
                "from" to fromtimestamp,
                "to" to totimestamp,
                "name" to editText1.text.toString(),
                "location" to editText2.text.toString()
            )
            db.collection("user_calendar")
                .add(newCalendar)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(this, "Session Added", Toast.LENGTH_SHORT).show()
                    popupWindow.dismiss()

                }
                .addOnFailureListener { e ->
                    println("Error adding document: $e")
                }
        }
    }
    private fun initUI(){
        supportActionBar?.hide()

        cardView1 = findViewById(R.id.cardView1)
        cardView2 = findViewById(R.id.cardView2)
        cardView3 = findViewById(R.id.cardView3)
        cardView4 = findViewById(R.id.cardView4)

        profileImageView =findViewById(R.id.imageView)
        nameTextView = findViewById<TextView>(R.id.nameTextView)
        emailTextView = findViewById<TextView>(R.id.emailTextView )
        phoneTextView = findViewById<TextView>(R.id.phoneTextView )

    }

    private fun updateUI(){

        nameTextView.text = traineeName
        emailTextView.text = traineeEmail
        phoneTextView.text = traineePhone

        val handler = Handler(Looper.getMainLooper())
        var image: Bitmap? = null
        Executors.newSingleThreadExecutor().execute {
            try {
                val `in` = java.net.URL(traineeProfile).openStream()
                image = BitmapFactory.decodeStream(`in`)
                handler.post {
                    profileImageView.setImageBitmap(image)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }
}