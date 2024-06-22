package com.example.santapantradisional

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.santapantradisional.database.AppDatabase
import com.example.santapantradisional.databinding.ActivityRestaurantBinding
import com.example.santapantradisional.models.Restaurant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RestaurantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestaurantBinding
    private lateinit var restaurantAdapter: RestaurantAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        val database = AppDatabase.getDatabase(this)

        lifecycleScope.launch {
            if (database.restaurantDao().getAllRestaurants().isEmpty()) {
                insertDefaultRestaurants(database)
            }
            loadRestaurants(database)
        }

        binding.buttonBack.setOnClickListener {
            navigateToHomePage()
        }
    }

    private suspend fun insertDefaultRestaurants(database: AppDatabase) {
        val restaurants = listOf(
            Restaurant(0, "Bebek Tepi Sawah", "Bali", "Indonesian", 4.6f, "https://www.intiwhiz.com/images/planner/bebek_tepi_sawah.jpg"),
            Restaurant(1, "Sate Khas Senayan", "Jakarta", "Indonesian", 4.4f, "https://asset.kompas.com/crops/wYTJV1LsKLh1c40tz8zGXZ_8Sy4=/107x0:1187x720/750x500/data/photo/2019/07/13/4200449735.jpeg"),
            Restaurant(2, "Gudeg Sagan", "DIY", "Gudeg", 4.5f, "https://asset.kompas.com/crops/gl4yXjwGAvVjNgeOMorZzWAkwNY=/32x19:1000x664/750x500/data/photo/2022/12/09/6392a51cdec7d.jpg"),
            Restaurant(3, "Warung Mak Beng", "Bali", "Seafood", 4.3f, "https://asset-2.tstatic.net/bali/foto/bank/images/Kondisi-Warung-Mak-Beng-Sanur-Denpasar.jpg"),
            Restaurant(4, "Nasi Ayam Kedewatan Bu Mangku", "Ubud", "Indonesian regional cuisine", 4.7f, "https://lh3.googleusercontent.com/p/AF1QipMGukS8sntmCFKweWjbdk_77-ge-uhJj-ycsl4g=s680-w680-h510"),
            Restaurant(5, "Soto Betawi Haji Husen", "Jakarta", "Soto Betawi", 4.4f, "https://assets-pergikuliner.com/Td-EF6BQJ8t4Nsys0Y1Yb7T94xw=/fit-in/1366x768/smart/filters:no_upscale()/https://assets-pergikuliner.com/uploads/image/picture/1471688/picture-1562064423.jpg")
        )
        withContext(Dispatchers.IO) {
            database.restaurantDao().insertAll(restaurants)
        }
    }

    private suspend fun loadRestaurants(database: AppDatabase) {
        val restaurants = database.restaurantDao().getAllRestaurants()
        restaurantAdapter = RestaurantAdapter(this, restaurants)
        binding.recyclerView.adapter = restaurantAdapter
    }

    private fun navigateToHomePage() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
