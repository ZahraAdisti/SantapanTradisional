package com.example.santapantradisional

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.santapantradisional.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE)

        val fullname = intent.getStringExtra("fullname") ?: sharedPreferences.getString("fullname", "No Name")
        val username = intent.getStringExtra("username") ?: sharedPreferences.getString("username", "No Username")
        val email = FirebaseAuth.getInstance().currentUser?.email ?: "No Email"

        Log.d("ProfileActivity", "Received user data: fullname=$fullname, username=$username, email=$email")

        binding.textViewFullName.text = "Nama Lengkap: $fullname"
        binding.textViewUsername.text = "Username: $username"
        binding.textViewEmail.text = "Email: $email"

        binding.buttonBack.setOnClickListener {
            onBackPressed()
        }

        binding.Logout.setOnClickListener {
            logoutUser()
        }

        binding.buttonEdit.setOnClickListener {
            val intent = Intent(this, EditActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveUserData(fullname: String, username: String) {
        val editor = sharedPreferences.edit()
        editor.putString("fullname", fullname)
        editor.putString("username", username)
        editor.apply()
    }

    private fun logoutUser() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        FirebaseAuth.getInstance().signOut()

        val intent = Intent(this@ProfileActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        val resultIntent = Intent()
        resultIntent.putExtra("updatedUsername", binding.textViewUsername.text.toString())
        setResult(RESULT_OK, resultIntent)
        super.onBackPressed()
    }
}
