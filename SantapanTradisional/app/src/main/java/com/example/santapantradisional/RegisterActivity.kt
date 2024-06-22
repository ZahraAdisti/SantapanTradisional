package com.example.santapantradisional

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.santapantradisional.databinding.ActivityRegisterBinding
import com.example.santapantradisional.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.RegisterButton.setOnClickListener { createAccount() }
        binding.LoginButton.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
        }
    }

    private fun createAccount() {
        val email = binding.Email.text.toString().trim()
        val password = binding.pass.text.toString().trim()
        val name = binding.Nama.text.toString().trim()
        val namaLengkap = binding.NamaLengkap.text.toString().trim()

        if (validateData(name, email, password, namaLengkap)) {
            createAccountInFirebase(name, email, password, namaLengkap)
        }
    }

    private fun validateData(name: String, email: String, password: String, namaLengkap: String): Boolean {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || namaLengkap.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun createAccountInFirebase(name: String, email: String, password: String, namaLengkap: String) {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this@RegisterActivity) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    user?.let {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()
                        it.updateProfile(profileUpdates).addOnCompleteListener { profileTask ->
                            if (profileTask.isSuccessful) {
                                Toast.makeText(this@RegisterActivity, "Account created successfully. Please verify your email.", Toast.LENGTH_SHORT).show()
                                it.sendEmailVerification()
                                val users = Users(
                                    username = name,
                                    fullname = namaLengkap,
                                    email = email,
                                    password = password
                                )
                                saveUserToFirebase(users)
                                firebaseAuth.signOut()
                                navigateToLoginPage() // Navigate to login page
                            } else {
                                Toast.makeText(this@RegisterActivity, "Failed to update profile. Please try again.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    task.exception?.let { exception ->
                        Toast.makeText(this@RegisterActivity, "Error: ${exception.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun saveUserToFirebase(users: Users) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let {
            val documentReference = FirebaseFirestore.getInstance().collection("users")
                .document(it.uid)

            documentReference.set(users).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("RegisterActivity", "User data saved successfully.")
                    sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("username", users.username)  // Menyimpan username
                    editor.putString("fullname", users.fullname)  // Menyimpan nama lengkap
                    editor.apply()
                } else {
                    task.exception?.let { exception ->
                        Log.e("RegisterActivity", "Failed to save user data: ${exception.localizedMessage}")
                    }
                }
            }
        }
    }

    private fun navigateToLoginPage() {
        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
        startActivity(intent)
        finish() // Close the current activity
    }
}
