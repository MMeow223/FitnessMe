package com.example.fitnessme

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanQRCode

class TraineeFragment : Fragment() {

    private lateinit var recyclerView1: RecyclerView
    private lateinit var button: Button
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
        return inflater.inflate(R.layout.fragment_trainee, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView1 = view.findViewById(R.id.recyclerView1)
        button = view.findViewById(R.id.button)

        val scanQrCodeLauncher = registerForActivityResult(ScanQRCode()) { result ->

            val traineeID = when (result) {
                is QRResult.QRSuccess -> result.content.rawValue
                QRResult.QRUserCanceled -> "User canceled"
                QRResult.QRMissingPermission -> "Missing permission"
                is QRResult.QRError -> "${result.exception.javaClass.simpleName}: ${result.exception.localizedMessage}"
            }

            db.collection("user")
                .document(traineeID)
                .update("trainer", user?.email)
                .addOnSuccessListener {
                    Toast.makeText(context, "Successfully added trainee", Toast.LENGTH_SHORT).show()

                    updateUI()
                }
        }

        updateUI()

        button.setOnClickListener{
            scanQrCodeLauncher.launch(null)
        }

    }

    private fun updateUI() {
        var traineeList = ArrayList<User>()

        db.collection("user")
            .whereEqualTo("trainer", user?.email)
            .get()
            .addOnSuccessListener { result ->
                traineeList = DBHelper.convertQuerySnapshotToUsers(result)

                recyclerView1.adapter = TraineeAdapter(traineeList)
                recyclerView1.layoutManager = LinearLayoutManager(context)
                recyclerView1.setHasFixedSize(true)
            }
        val manager = LinearLayoutManager(activity)

        recyclerView1.layoutManager = manager
        recyclerView1.adapter = TraineeAdapter(traineeList)

    }

    companion object {

        @JvmStatic
        fun newInstance() = TraineeFragment()
    }
}