package com.example.fitnessme

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

const val TAG = "FirstFragment"
const val RC_SIGN_IN = 123
const val RC_ONE_TAP = 124

class RegisterAccountFragment : Fragment() {

    // Control whether user declined One Tap UI
    lateinit var gso: GoogleSignInOptions
    lateinit var mGoogleSignInClient: GoogleSignInClient
    val Req_Code: Int = 123
    val firebaseAuth = FirebaseAuth.getInstance()

    private lateinit var editText1: EditText
    private lateinit var editText2: EditText

    private lateinit var button: Button
    private lateinit var cardView: CardView

    private lateinit var auth: FirebaseAuth

    private val db = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_account, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        auth.signOut()
//        signOut()
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(resources.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        initUI()

        button.setOnClickListener{
            val email = editText1?.text.toString()
            val password = editText2?.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                signInWithPassword(email, password)
                // if the account exist, then sign in, otherwise create a new account
            }

        }

        cardView.setOnClickListener{

            loginWithGoogle()
        }
    }
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){

            val email = editText1?.text.toString()
            val password = editText2?.text.toString()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        val user = auth.currentUser
//                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(requireContext(), "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
//                        updateUI(null)
                    }
                }

        }
    }

    private fun initUI(){

        editText1 = view?.findViewById(R.id.editText1)!!
        editText2 = view?.findViewById(R.id.editText2)!!

        button = view?.findViewById(R.id.button)!!
        cardView = view?.findViewById(R.id.cardView)!!
    }

    private fun loginWithEmailPassword(){
        val email = editText1?.text.toString()
        val password = editText2?.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            signInWithPassword(email, password)
            // if the account exist, then sign in, otherwise create a new account
        }
    }

    private fun loginWithGoogle(){
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, Req_Code)
        val email = editText1?.text.toString()
        val password = editText2?.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            signInWithPassword(email, password)
            // if the account exist, then sign in, otherwise create a new account
        }
    }
    // Sign in to an existing password account
    private fun signInWithPassword(email: String, password: String) {

        // check if the account exist
        firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val signInMethods = task.result?.signInMethods
                if (signInMethods?.isEmpty() == true) {
                    // account does not exist
                    Toast.makeText(requireContext(), "Account does not exist", Toast.LENGTH_SHORT).show()
                    // create new account
                    createAccount(email, password)
                } else {
                    // account exists
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show()
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(requireContext(), "Login failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

//
//        firebaseAuth.signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener(requireActivity()) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d(TAG, "signInWithEmail:success")
//                    val user = firebaseAuth.currentUser
//                    updateUI(user)
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.w(TAG, "signInWithEmail:failure", task.exception)
//                    Toast.makeText(
//                        requireActivity(), "Authentication failed.",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    updateUI(null)
//                }
//            }

    }

    private fun createAccount(email: String, password: String) {

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = firebaseAuth.currentUser


                    // create user in database

                    var newUser = User(email = email)

                    db.collection("user")
                        .document(user!!.uid)
                        .set(newUser)

//                    DBHelper.createUser(newUser)

                    // go to fragment 2
                    loadFragment()

                    // if success, then sign in
//                    signInWithPassword(email, password)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        requireActivity(), "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Req_Code) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }

    // handleResult() function -  this is where we update the UI after Google signin takes place
    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                UpdateUI(account)
            }
        } catch (e: ApiException) {
            Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    // UpdateUI() function - this is where we specify what UI updation are needed after google signin has taken place.
    private fun UpdateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->

                // if the user is new bring them to next fragment to fill in their details


                // check if the user is new
                // check if the other value of user have been filled in
                db.collection("user")
                    .whereEqualTo("email", account.email)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            if (document.data["account_created_completed"] == false) {
                                // if the user is new bring them to next fragment to fill in their details
                                loadFragment()
                            } else {
                                // if the user is not new, then go to main activity
                                val intent = Intent(requireContext(), MainActivity::class.java)
                                startActivity(intent)
                            }
                        }
                    }

            }
        }

    private fun loadFragment(){
        val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.frameLayout, RegisterInfo1Fragment.newInstance())
        fragmentTransaction?.addToBackStack(null)
        fragmentTransaction?.commit()
    }

    private fun signOut() {

        // Google sign out
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(requireActivity(), OnCompleteListener{
                Toast.makeText(requireContext(),"Signed Out",Toast.LENGTH_SHORT).show()
            })
    }

    companion object {

        @JvmStatic
        fun newInstance() = RegisterAccountFragment()
    }
}