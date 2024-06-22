package com.example.santapantradisional

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.santapantradisional.databinding.ActivityDetailRestaurantBinding
import com.example.santapantradisional.models.Restaurant
import com.google.gson.Gson

class DetailRestaurantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailRestaurantBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailRestaurantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data restoran yang dipilih dari intent
        val restaurant = Gson().fromJson(intent.getStringExtra("restaurant"), Restaurant::class.java) as Restaurant

        // Tampilkan detail restoran di UI
        binding.textViewTitle.text = restaurant.name
        Glide.with(this@DetailRestaurantActivity).load(restaurant.imageUrl).into(binding.imageViewFood)
        binding.textViewDescription.text = "Rating : " + restaurant.rating + "\n" + "Jenis Makanan : " + restaurant.cuisine + "\n" + "Lokasi : " + restaurant.location

        // Tambahkan kode untuk menampilkan detail restoran lainnya sesuai kebutuhan

        // Set listener untuk tombol back
        binding.buttonBack.setOnClickListener {
            navigateToHomePage()
        }
    }

    private fun navigateToHomePage() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
