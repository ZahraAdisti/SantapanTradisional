package com.example.santapantradisional

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.santapantradisional.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi sharedPreferences
        sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE)

        binding.RegisterButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, RegisterActivity::class.java))
        }

        binding.LoginButton.setOnClickListener { v -> loginUser() }
    }

    private fun loginUser() {
        val email = binding.Email.text.toString()
        val password = binding.Password.text.toString()
        loginAccountInFirebase(email, password)
    }

    private fun loginAccountInFirebase(email: String, password: String) {
        val firebaseAuth = FirebaseAuth.getInstance()
        changeInProgress(true)
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            changeInProgress(false)
            if (task.isSuccessful) {
                val currentUser = firebaseAuth.currentUser
                if (currentUser != null && currentUser.isEmailVerified) {
                    getUserDataAndProceed(currentUser.uid)
                } else {
                    Toast.makeText(this@MainActivity, "Email not verified, Please verify your email.", Toast.LENGTH_SHORT).show()
                }
            } else {
                task.exception?.localizedMessage?.let {
                    Toast.makeText(this@MainActivity, "Incorrect Email or Password.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getUserDataAndProceed(uid: String) {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(uid)
        userRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val fullname = document.getString("fullname") ?: "No Name"
                    val email = document.getString("email") ?: "No Email"
                    val username = document.getString("username") ?: "No Username"

                    Log.d("MainActivity", "Retrieved user data: fullname=$fullname, username=$username, email=$email")

                    saveUserDataToSharedPreferences(fullname, username, email)
                    proceedToHome()
                } else {
                    Log.e("MainActivity", "No such document")
                    Toast.makeText(this@MainActivity, "No such document", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("MainActivity", "Error getting documents: $exception")
                Toast.makeText(this@MainActivity, "Error getting documents: $exception", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveUserDataToSharedPreferences(fullname: String, username: String, email: String) {
        val editor = sharedPreferences.edit()
        editor.putString("fullname", fullname)
        editor.putString("username", username)
        editor.putString("email", email)
        editor.putBoolean("isLoggedIn", true)
        editor.apply()
    }

    private fun proceedToHome() {
        startActivity(Intent(this@MainActivity, HomeActivity::class.java))
        finish()
    }

    private fun changeInProgress(inProgress: Boolean) {
        if (inProgress) {
            binding.LoginButton.visibility = TextView.GONE
        } else {
            binding.LoginButton.visibility = TextView.VISIBLE
        }
    }
}
