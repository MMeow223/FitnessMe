package com.example.fitnessme

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class RegisterInfo1Fragment : Fragment() {

    private lateinit var button: Button

    private lateinit var spinner: Spinner

    private val db = Firebase.firestore
    private val user = Firebase.auth.currentUser

    private lateinit var editText1: EditText
    private lateinit var editText2: EditText
    private lateinit var editText3: EditText
    private lateinit var editText4: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_info1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()

        initSpinner()

        button.setOnClickListener{

            // update user info in database

            db.collection("user")
                .whereEqualTo("email", user?.email)
                .get()
                .addOnSuccessListener { result ->

                    result.documents[0].reference.update("firstname", editText1.text.toString())
                    result.documents[0].reference.update("lastname", editText2.text.toString())
                    result.documents[0].reference.update("heights", FieldValue.arrayUnion(editText3.text.toString().toFloat()))
                    result.documents[0].reference.update("weights", FieldValue.arrayUnion(editText4.text.toString().toFloat()))

                   result.documents[0].reference.update("role", spinner.selectedItem.toString())

                    loadFragment()

                }
        }
    }

    private fun initSpinner() {
        if (spinner != null) {
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                arrayListOf("Trainer", "Trainee")
            )
//            adapter.setDropDownViewResource(R.layout.spinner)
            spinner.adapter = adapter
        }

//        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            @RequiresApi(Build.VERSION_CODES.O)
//            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
//                // display the current text
//                updateGridView(parent.getItemAtPosition(position).toString())
//            }
//            override fun onNothingSelected(parent: AdapterView<*>) {
//            }
//        }
    }


    private fun initUI(){
        button = view?.findViewById(R.id.button)!!
        spinner = view?.findViewById(R.id.spinner)!!

        editText1 = view?.findViewById(R.id.editText1)!!
        editText2 = view?.findViewById(R.id.editText2)!!
        editText3 = view?.findViewById(R.id.editText3)!!
        editText4 = view?.findViewById(R.id.editText4)!!

    }

    private fun loadFragment(){
        val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.frameLayout, RegisterInfo2Fragment.newInstance())
        fragmentTransaction?.addToBackStack(null)
        fragmentTransaction?.commit()
    }

    companion object {

        @JvmStatic
        fun newInstance() =  RegisterInfo1Fragment()
    }
}