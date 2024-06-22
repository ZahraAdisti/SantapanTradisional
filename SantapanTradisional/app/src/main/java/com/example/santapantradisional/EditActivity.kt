package com.example.santapantradisional

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.santapantradisional.ProfileActivity
import com.example.santapantradisional.databinding.ActivityEditBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class EditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var sharedPreferences: SharedPreferences // Deklarasi variabel sharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE) // Inisialisasi sharedPreferences

        // Load current user data to EditTexts
        loadUserData()

        binding.buttonSave.setOnClickListener {
            updateUserProfile()
        }
    }

    private fun loadUserData() {
        val currentUser = firebaseAuth.currentUser
        currentUser?.let {
            binding.editTextFullName.setText(it.displayName)
            binding.editTextUsername.setText(it.email) // Using email as username for simplicity

            // Retrieve additional user data from Firestore
            val userRef = firestore.collection("users").document(it.uid)
            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    binding.editTextFullName.setText(document.getString("fullname"))
                    binding.editTextUsername.setText(document.getString("username"))
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to load user data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUserProfile() {
        val newUsername = binding.editTextUsername.text.toString().trim()
        val newFullName = binding.editTextFullName.text.toString().trim()
        val currentPassword = binding.editTextCurrentPassword.text.toString().trim()

        if (TextUtils.isEmpty(newUsername) || TextUtils.isEmpty(newFullName) || TextUtils.isEmpty(currentPassword)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = firebaseAuth.currentUser
        currentUser?.let { user ->
            // Reauthenticate the user with current password
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
            user.reauthenticate(credential).addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    // Re-authentication was successful, proceed to update the profile
                    val updates = UserProfileChangeRequest.Builder()
                        .setDisplayName(newFullName)
                        .build()

                    user.updateProfile(updates).addOnCompleteListener { profileTask ->
                        if (profileTask.isSuccessful) {
                            saveUserDataToFirestore(newUsername, newFullName)
                        } else {
                            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    // Re-authentication failed
                    Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveUserDataToFirestore(newUsername: String, newFullName: String) {
        val user = hashMapOf(
            "username" to newUsername,
            "fullname" to newFullName
        )

        firestore.collection("users").document(firebaseAuth.currentUser!!.uid)
            .set(user)
            .addOnSuccessListener {
                val editor = sharedPreferences.edit()
                editor.putString("username", newUsername)
                editor.putString("fullname", newFullName)
                editor.apply()

                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                navigateToProfilePage(newFullName, newUsername)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update profile in Firestore", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToProfilePage(fullname: String, username: String) {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra("fullname", fullname)
        intent.putExtra("username", username)
        startActivity(intent)
        finish()
    }
}
