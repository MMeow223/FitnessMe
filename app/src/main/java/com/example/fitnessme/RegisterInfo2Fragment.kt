package com.example.fitnessme

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class RegisterInfo2Fragment : Fragment() {

    private lateinit var button: Button
    private lateinit var textView1: TextView
    private lateinit var editText1: EditText

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
        return inflater.inflate(R.layout.fragment_register_info2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()


        db.collection("user")
            .whereEqualTo("email", user?.email)
            .get()
            .addOnSuccessListener { result ->

                val role = result.documents[0].get("role").toString().lowercase()
                if(role == "trainer"){
                    textView1.text = "I wish to become a trainer in ..."
                    editText1.hint = "Enter your area of expertise"
                }
                else if(role == "trainee"){
                    textView1.text = "Your goal is ..."
                    editText1.hint = "Enter your goal"

                }

            }

        button.setOnClickListener{


            db.collection("user")
                .whereEqualTo("email", user?.email)
                .get()
                .addOnSuccessListener { result ->


                    if( result.documents[0].get("role").toString().lowercase() == "trainee"){
                        result.documents[0].reference.update("goal", editText1.text.toString())
                    }
                    else{
                        result.documents[0].reference.update("field", editText1.text.toString())
                    }
                    result.documents[0].reference.update("account_created_completed", true)

                    logMainActivity()

                }

        }
    }

    private fun initUI(){
        button = view?.findViewById(R.id.button)!!
        textView1 = view?.findViewById(R.id.textView1)!!
        editText1 = view?.findViewById(R.id.editText1)!!

        val role = "trainee"
        if(role == "trainer"){
            textView1.text = "I wish to become a trainer in ..."
            editText1.hint = "Enter your area of expertise"
        }
        else if(role == "trainee"){
            textView1.text = "Your goal is ..."
            editText1.hint = "Enter your goal"

        }
    }

    private fun logMainActivity(){
        // login
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)

    }
    companion object {

        @JvmStatic
        fun newInstance() =  RegisterInfo2Fragment()
    }
}