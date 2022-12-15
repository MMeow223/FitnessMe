package com.example.fitnessme

import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.*
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.cardview.widget.CardView
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.protobuf.DescriptorProtos
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList


class ProfileFragment : Fragment() {

    private lateinit var profileImageView: ImageView
    private lateinit var cardView: CardView
    private lateinit var signOutCardView: CardView
    private lateinit var trainerSettingCardView: CardView
    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var phoneTextView: TextView
    private lateinit var ageContentTextView: TextView
    private lateinit var heightContentTextView: TextView
    private lateinit var bmiContentTextView: TextView
    private lateinit var weightTextView: TextView
    private lateinit var genderTextView: TextView
    private lateinit var diseasesTextView: TextView
    private lateinit var goalTextView: TextView

    private lateinit var healthyStatus: TextView

    private lateinit var qrCodeImageView:ImageView

    private lateinit var imageButton1:ImageButton
    private lateinit var imageButton2:ImageButton

    var imageURIList = ArrayList<Uri>()
    var profilePicURI: Uri? = null

    private val db = Firebase.firestore
    private val user = Firebase.auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()

        updateUI()

        cardView.setOnClickListener{
            showEditBasicInfoPopUpWindow()
        }

        imageButton1.setOnClickListener{
            showEditBodyInfoPopUpWindow()
        }

        imageButton2.setOnClickListener{
            showEditOtherInfoPopUpWindow()
        }

        db.collection("user")
            .whereEqualTo("email", user?.email)
            .get()
            .addOnSuccessListener {
                if(it.documents[0].data!!["role"] == "Trainer"){
                    qrCodeImageView.setImageBitmap(resources.getDrawable(R.drawable.profile_icon).toBitmap())
                    qrCodeImageView.setOnClickListener{
                        var intent = Intent(context, TrainerDetailActivity::class.java)
                        intent.putExtra("trainer_email", user?.email)
                        startActivity(intent)
                    }

                    trainerSettingCardView.setOnClickListener{
                        showTrainerSettingPopUpWindow()
                    }
                }
                else{
                    trainerSettingCardView.visibility = View.GONE

                    qrCodeImageView.setOnClickListener{
                        showQRCodePopUpWindow()
                    }
                }
            }

        signOutCardView.setOnClickListener{
            Firebase.auth.signOut()
            val intent = Intent(activity, RegisterActivity::class.java)
            startActivity(intent)
        }

    }

    private fun initUI(){
        profileImageView = requireView().findViewById(R.id.imageView)
        qrCodeImageView = requireView().findViewById<ImageView>(R.id.qrcodeImageView)
        cardView = requireView().findViewById<CardView>(R.id.editInfoCardView)
        trainerSettingCardView = requireView().findViewById<CardView>(R.id.editTrainerCardView)
        signOutCardView = requireView().findViewById<CardView>(R.id.signOutCardView)
        imageButton1 = requireView().findViewById<ImageButton>(R.id.editBodyStatImageButton)
        imageButton2 = requireView().findViewById<ImageButton>(R.id.editOtherStatImageButton)

        nameTextView = requireView().findViewById<TextView>(R.id.nameTextView)
        emailTextView = requireView().findViewById<TextView>(R.id.emailTextView )
        phoneTextView = requireView().findViewById<TextView>(R.id.phoneTextView )
        ageContentTextView = requireView().findViewById<TextView>(R.id.ageContentTextView )
        heightContentTextView = requireView().findViewById<TextView>(R.id.heightContentTextView )
        bmiContentTextView = requireView().findViewById<TextView>(R.id.bmiContentTextView )
        weightTextView = requireView().findViewById<TextView>(R.id.weightContentTextView )
        genderTextView = requireView().findViewById<TextView>(R.id.genderTextView )
        diseasesTextView = requireView().findViewById<TextView>(R.id.diseasesTextView )
        goalTextView = requireView().findViewById<TextView>(R.id.goalTextView )

        healthyStatus = requireView().findViewById<TextView>(R.id.bmiUnitTextView)

    }

    private fun updateUI(){
        db.collection("user")
            .whereEqualTo("email", user?.email)
            .get()
            .addOnSuccessListener { result ->
                val userFromDB = result.documents[0].toObject(User::class.java)!!

                val height = userFromDB.heights[userFromDB.heights.size - 1]
                val weight = userFromDB.weights[userFromDB.weights.size - 1]
                val bmi = weight / (height/100 * height/100)

                nameTextView.text = userFromDB.firstname + " " + userFromDB.lastname
                emailTextView.text = userFromDB.email
                phoneTextView.text = userFromDB.phone
                ageContentTextView.text = userFromDB.age.toString()
                heightContentTextView.text = height.toString()
                bmiContentTextView.text = bmi.toString()
                weightTextView.text = weight.toString()
                genderTextView.text = userFromDB.gender
                diseasesTextView.text = userFromDB.diseases

                if(userFromDB.role == "Trainer"){
                    goalTextView.text = userFromDB.field
                }
                else{
                    goalTextView.text = userFromDB.goal

                }

                if( bmi < 18.5){
                    healthyStatus.text = "Underweight"
                    healthyStatus.setTextColor(Color.parseColor("#FF0000"))
                }
                else if( bmi >= 18.5 && bmi <= 24.9){
                    healthyStatus.text = "Normal"
                    healthyStatus.setTextColor(Color.parseColor("#00FF00"))
                }
                else if( bmi >= 25 && bmi <= 29.9){
                    healthyStatus.text = "Overweight"
                    healthyStatus.setTextColor(Color.parseColor("#FFA500"))
                }
                else if( bmi >= 30){
                    healthyStatus.text = "Obese"
                    healthyStatus.setTextColor(Color.parseColor("#FF0000"))
                }

                val handler = Handler(Looper.getMainLooper())
                var image: Bitmap? = null
                Executors.newSingleThreadExecutor().execute {
                    try {
                        val `in` = java.net.URL(userFromDB.profile_image_link).openStream()
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

    fun showQRCodePopUpWindow() {


        val inflater = LayoutInflater.from(context)
        val popupView: View = inflater?.inflate(R.layout.pop_up_qr_code, null)!!


        val inputValue = user!!.uid
        val smallerDimension = 500
        val imageView = popupView.findViewById<ImageView>(R.id.imageView)

        // QR Code
        val qrgEncoder = QRGEncoder(inputValue, null, QRGContents.Type.TEXT, smallerDimension)
        qrgEncoder.setColorBlack(Color.WHITE)
        qrgEncoder.setColorWhite(Color.BLACK)
        // Getting QR-Code as Bitmap
        val bitmap = qrgEncoder.getBitmap()
        // Setting Bitmap to ImageView
        imageView.setImageBitmap(bitmap)

        // ClipBoard
//        cardView.setOnClickListener(){
//            val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//            val clip = android.content.ClipData.newPlainText("QR Code", inputValue)
//            clipboard.setPrimaryClip(clip)
//            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
//        }

        // create the popup window
        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true // lets taps outside the popup also dismiss it
        val popupWindow = PopupWindow(popupView, width, height, focusable)
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

        // set the background to black with 50% opacity
        val container = popupWindow.contentView.rootView
        val context = popupWindow.contentView.context
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val p = container.layoutParams as WindowManager.LayoutParams
        p.flags = p.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
        p.dimAmount = 0.5f
        wm.updateViewLayout(container, p)

    }

    fun showEditBasicInfoPopUpWindow() {

        val inflater = LayoutInflater.from(context)
        val popupView: View = inflater?.inflate(R.layout.pop_up_edit_info, null)!!

        var editText1 = popupView.findViewById<EditText>(R.id.editText1)
        var editText2 = popupView.findViewById<EditText>(R.id.editText2)
        var editText3 = popupView.findViewById<EditText>(R.id.editText3)

        var textView1 = popupView.findViewById<TextView>(R.id.textView1)

        var button = popupView.findViewById<Button>(R.id.button)

        db.collection("user")
            .whereEqualTo("email", user?.email)
            .get()
            .addOnSuccessListener { result ->
                if(result.documents[0].data!!["firstname"] != ""){
                    editText1.hint = result.documents[0].get("firstname").toString()
                }
                if(result.documents[0].data!!["lastname"] != ""){
                    editText2.hint = result.documents[0].get("lastname").toString()
                }
                if(result.documents[0].data!!["phone"] != ""){
                    editText3.hint = result.documents[0].get("phone").toString()
                }
            }

        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true // lets taps outside the popup also dismiss it
        val popupWindow = PopupWindow(popupView, width, height, focusable)

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

        val container = popupWindow.contentView.rootView
        val context = popupWindow.contentView.context
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val p = container.layoutParams as WindowManager.LayoutParams
        p.flags = p.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
        p.dimAmount = 0.5f
        wm.updateViewLayout(container, p)

        textView1.setOnClickListener{
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent,"Select Picture"), 2);
        }

        button.setOnClickListener{

            db.collection("user")
                .whereEqualTo("email", user?.email)
                .get()
                .addOnSuccessListener { result ->

                    if( editText1.text.toString() != ""){
                        db.collection("user").document(result.documents[0].id).update("firstname", editText1.text.toString())
                    }
                    if( editText2.text.toString() != ""){
                        db.collection("user").document(result.documents[0].id).update("lastname", editText2.text.toString())
                    }
                    if( editText3.text.toString() != ""){
                        db.collection("user").document(result.documents[0].id).update("phone", editText3.text.toString())
                    }
                    if( editText3.text.toString() != ""){
                        db.collection("user").document(result.documents[0].id).update("phone", editText3.text.toString())
                    }

                    val filename = UUID.randomUUID().toString()
                    val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

                    profilePicURI?.let { it1 ->
                        ref.putFile(it1)
                            .addOnSuccessListener {
                                Log.d("RegisterActivity", "Successfully uploaded image: ${it.metadata?.path}")

                                ref.downloadUrl.addOnSuccessListener { imageUrl ->
                                    Log.d("RegisterActivity", "File Location: $it")

                                    result.documents[0].reference.update("profile_image_link", imageUrl.toString())

                                    updateUI()

                                }
                            }
                            .addOnFailureListener {
                                Log.d("RegisterActivity", "Failed to upload image to storage: ${it.message}")
                            }
                    }

                    updateUI()

                    popupWindow.dismiss()

                }



        }



    }

    fun showEditBodyInfoPopUpWindow() {

        val inflater = LayoutInflater.from(context)
        val popupView: View = inflater?.inflate(R.layout.pop_up_edit_info, null)!!


        var editText1 = popupView.findViewById<EditText>(R.id.editText1)
        var editText2 = popupView.findViewById<EditText>(R.id.editText2)
        var editText3 = popupView.findViewById<EditText>(R.id.editText3)

        var button = popupView.findViewById<Button>(R.id.button)

        db.collection("user")
            .whereEqualTo("email", user?.email)
            .get()
            .addOnSuccessListener { result ->
                if(result.documents[0].data!!["age"] != ""){
                    editText1.hint = result.documents[0].get("age").toString() + " (y/o)"
                }
                else{
                    editText1.hint = "Age"
                }
                if(result.documents[0].data!!["heights"] != ""){
                    var heights = result.documents[0].get("heights") as ArrayList<Float>
                    editText2.hint = heights.last().toString() + " (cm)"
                } else{
                    editText2.hint = "Height"
                }
                if(result.documents[0].data!!["weights"] != ""){
                    var weights = result.documents[0].get("weights") as ArrayList<Float>
                    editText3.hint = weights.last().toString() + " (kg)"
                } else{
                    editText3.hint = "Weight"
                }

            }

        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true // lets taps outside the popup also dismiss it
        val popupWindow = PopupWindow(popupView, width, height, focusable)

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

        val container = popupWindow.contentView.rootView
        val context = popupWindow.contentView.context
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val p = container.layoutParams as WindowManager.LayoutParams
        p.flags = p.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
        p.dimAmount = 0.5f
        wm.updateViewLayout(container, p)

        button.setOnClickListener{

            db.collection("user")
                .whereEqualTo("email", user?.email)
                .get()
                .addOnSuccessListener { result ->


                    if( editText1.text.toString() != ""){
                        val age = editText1.text.toString().toInt()
                        db.collection("user").document(result.documents[0].id).update("age", age)
                    }
                    if( editText2.text.toString() != ""){
                        val height = editText2.text.toString().toFloat()
                        db.collection("user").document(result.documents[0].id).update("heights",FieldValue.arrayUnion(height))
                    }
                    if( editText3.text.toString() != ""){
                        val weight = editText3.text.toString().toFloat()
                        db.collection("user").document(result.documents[0].id).update("weights", FieldValue.arrayUnion(weight))
                    }

                    updateUI()

                    popupWindow.dismiss()

                }
        }


    }

    fun showEditOtherInfoPopUpWindow() {

        val inflater = LayoutInflater.from(context)
        val popupView: View = inflater?.inflate(R.layout.pop_up_edit_info, null)!!



        var editText1 = popupView.findViewById<EditText>(R.id.editText1)
        var editText2 = popupView.findViewById<EditText>(R.id.editText2)
        var editText3 = popupView.findViewById<EditText>(R.id.editText3)

        var button = popupView.findViewById<Button>(R.id.button)

        db.collection("user")
            .whereEqualTo("email", user?.email)
            .get()
            .addOnSuccessListener { result ->
                if(result.documents[0].data!!["gender"] != ""){
                    editText1.hint = result.documents[0].get("gender").toString()
                }
                else{
                    editText1.hint = "Gender"
                }
                if(result.documents[0].data!!["diseases"] != ""){
                    editText2.hint =result.documents[0].get("diseases").toString()
                } else{
                    editText2.hint = "Diseases"
                }
                if(result.documents[0].data!!["role"] == "Trainer"){
                    if(result.documents[0].data!!["field"] != ""){
                        editText3.hint = result.documents[0].get("field").toString()
                    } else{
                        editText3.hint = "Field"
                    }
                }
                else{
                    if(result.documents[0].data!!["goal"] != ""){
                        editText3.hint = result.documents[0].get("goal").toString()
                    } else{
                        editText3.hint = "Goal"
                    }
                }
            }


        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true // lets taps outside the popup also dismiss it
        val popupWindow = PopupWindow(popupView, width, height, focusable)

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

        val container = popupWindow.contentView.rootView
        val context = popupWindow.contentView.context
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val p = container.layoutParams as WindowManager.LayoutParams
        p.flags = p.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
        p.dimAmount = 0.5f
        wm.updateViewLayout(container, p)


        button.setOnClickListener{

            db.collection("user")
                .whereEqualTo("email", user?.email)
                .get()
                .addOnSuccessListener { result ->

                    if( editText1.text.toString() != ""){
                        db.collection("user").document(result.documents[0].id).update("gender",  editText1.text.toString())
                    }
                    if( editText2.text.toString() != ""){
                        db.collection("user").document(result.documents[0].id).update("diseases", editText2.text.toString())
                    }

                    if(result.documents[0].data!!["role"] == "Trainer"){
                        if( editText3.text.toString() != ""){
                            db.collection("user").document(result.documents[0].id).update("field", editText3.text.toString())
                        }
                    }
                    else{
                        if( editText3.text.toString() != ""){
                            db.collection("user").document(result.documents[0].id).update("goal", editText3.text.toString())
                        }
                    }

                    updateUI()

                    popupWindow.dismiss()

                }
        }
    }

    private fun showTrainerSettingPopUpWindow() {

        val inflater = LayoutInflater.from(context)
        val popupView: View = inflater?.inflate(R.layout.pop_up_edit_trainer_info, null)!!

        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true // lets taps outside the popup also dismiss it
        val popupWindow = PopupWindow(popupView, width, height, focusable)

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

        val container = popupWindow.contentView.rootView
        val context = popupWindow.contentView.context
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val p = container.layoutParams as WindowManager.LayoutParams
        p.flags = p.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
        p.dimAmount = 0.5f
        wm.updateViewLayout(container, p)

        var button = popupView.findViewById<Button>(R.id.button)
        var textView1 = popupView.findViewById<TextView>(R.id.textView1)
        var editText1 = popupView.findViewById<EditText>(R.id.editText1)
        var editText2 = popupView.findViewById<EditText>(R.id.editText2)
        var editText3 = popupView.findViewById<EditText>(R.id.editText3)
        var editText4 = popupView.findViewById<EditText>(R.id.editText4)
        var editText5 = popupView.findViewById<EditText>(R.id.editText5)

        textView1.setOnClickListener{
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);
        }


        // upload image to firebase storage
        button.setOnClickListener {
            val socialMedia = editText4.text.split(",").toTypedArray()

            db.collection("user")
                .whereEqualTo("email", user?.email)
                .get()
                .addOnSuccessListener { documents ->
                    if(editText1.text.toString() != ""){
                        documents.documents[0].reference.update("coaching_experience", editText1.text.toString().toInt())
                    }
                    if(editText2.text.toString() != ""){
                        documents.documents[0].reference.update("expected_fee", editText2.text.toString().toInt())
                    }
                    if(editText3.text.toString() != ""){
                        documents.documents[0].reference.update("region", editText3.text.toString())
                    }
                    if(!socialMedia.isEmpty()){
                        for(link in socialMedia){
                            documents.documents[0].reference.update("social_medias", FieldValue.arrayUnion(link))
                        }
                    }
                    if(editText5.text .toString() != ""){
                        documents.documents[0].reference.update("description", editText5.text.toString())
                    }
                }

            // show the images link
            for (uri in imageURIList) {

                Log.d("RegisterActivity", "PHOTO RESULT: ${uri.toString()}")

                // upload image to firebase storage
                val filename = UUID.randomUUID().toString()
                val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

                ref.putFile(uri)
                    .addOnSuccessListener {
                        Log.d("RegisterActivity", "Successfully uploaded image: ${it.metadata?.path}")

                        ref.downloadUrl.addOnSuccessListener { imageUrl ->
                            Log.d("RegisterActivity", "File Location: $it")

                            db.collection("user")
                                .whereEqualTo("email", user?.email)
                                .get()
                                .addOnSuccessListener { result ->
                                    db.collection("user")
                                        .document(result.documents[0].id)
                                        .update("image_links", FieldValue.arrayUnion(imageUrl))
                                }

                        }
                    }
                    .addOnFailureListener {
                        Log.d("RegisterActivity", "Failed to upload image to storage: ${it.message}")
                    }
            }

            Toast.makeText(context, "Update Successfully", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }
    }

    // get images result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {

            val clipData = data.clipData

            clipData.toString()

            // show the images link
            for (i in 0 until clipData!!.itemCount) {
                val item = clipData.getItemAt(i)
                val uri = item.uri

                imageURIList.add(uri)
            }
        }
        else if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {

            val clipData = data.clipData

            clipData.toString()

            profilePicURI = clipData!!.getItemAt(0).uri

        }
    }


    companion object {

        @JvmStatic
        fun newInstance() = ProfileFragment()
    }
}