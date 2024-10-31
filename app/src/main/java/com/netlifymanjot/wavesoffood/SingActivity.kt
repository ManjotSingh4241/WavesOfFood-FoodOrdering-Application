package com.netlifymanjot.wavesoffood

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.netlifymanjot.wavesoffood.databinding.ActivitySingBinding
import com.netlifymanjot.wavesoffood.databinding.ActivityStartBinding
import com.netlifymanjot.wavesoffood.model.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.GoogleAuthProvider


class SingActivity : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var password: String
    private lateinit var username: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClint : GoogleSignInClient


    private val binding:ActivitySingBinding by lazy {
        ActivitySingBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        auth = Firebase.auth

        database = Firebase.database.reference

        googleSignInClint = GoogleSignIn.getClient(this, googleSignInOptions)

        binding.CreateAccountButton.setOnClickListener { 
            username = binding.userName.text.toString()
            email = binding.EmailAddress.text.toString().trim()
            password = binding.password.text.toString().trim()
            
            if(email.isEmpty()||password.isEmpty()||username.isEmpty()){
                Toast.makeText(this, "Please Fill All The Required Information", Toast.LENGTH_SHORT).show()
            }else{
                createAccount(email, password)
            }
        }

        binding.alreadyhavebutton.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.GoogleButton.setOnClickListener {
            val signIntent = googleSignInClint.signInIntent
            launcher.launch(signIntent)
        }
    }
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result->
        if (result.resultCode == Activity.RESULT_OK){
           val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if(task.isSuccessful){
                val account: GoogleSignInAccount? =task.result
                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener {
                    task->
                    if (task.isSuccessful){
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }else{
                        Toast.makeText(this, "Account Creation Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }else{
            Toast.makeText(this, "Account Creation Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            task-> if(task.isSuccessful){
            Toast.makeText(this, "Account Created", Toast.LENGTH_SHORT).show()
            saveUserData()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }else{
            Toast.makeText(this, "Account Creation Failed", Toast.LENGTH_SHORT).show()
            Log.d("Account", "createAccount: Failure", task.exception)
        }
        }
    }

    private fun saveUserData() {
        username = binding.userName.text.toString()
        password = binding.password.text.toString().trim()
        email = binding.EmailAddress.text.toString().trim()

        val user = UserModel(username, email, password)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        database.child("user").child(userId).setValue(user)
    }
}