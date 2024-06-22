package com.example.santapantradisional

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.santapantradisional.databinding.ActivityHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var locationManager: LocationManager
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Home"

        binding.recyclerViewRecipes.layoutManager = LinearLayoutManager(this)

        swipeRefreshLayout = binding.swipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            // Ketika pengguna menarik ke bawah, lakukan refresh
            fetchRecipes()
        }

        // Inisialisasi locationManager
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Cek izin lokasi
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request izin lokasi jika belum diberikan
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Jika izin sudah diberikan, mulai memperoleh lokasi dan memperoleh resep
            getLocationAndRecipes()
        }

        // Ambil username dari SharedPreferences
        val sharedPrefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        username = sharedPrefs.getString("username", "Nama Pengguna") ?: "Nama Pengguna"
        Log.d("HomeActivity", "Username: $username")
        // Log tambahan untuk memeriksa isi SharedPreferences
        val allEntries = sharedPrefs.all
        for ((key, value) in allEntries) {
            Log.d("SharedPreferences", "$key: $value")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_restoran -> {
                val intent = Intent(this, RestaurantActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menu_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Mengambil lokasi pengguna dan memperoleh resep
    private fun getLocationAndRecipes() {
        // Buat objek LocationListener
        val locationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                // Panggil method displayLocationName() dengan menggunakan koordinat location.latitude dan location.longitude
                displayLocationName(location.latitude, location.longitude)
                // Setelah mendapatkan lokasi, dapatkan resep
                fetchRecipes()
                // Hentikan pembaruan lokasi
                locationManager.removeUpdates(this)
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        // Request pembaruan lokasi
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0,
            0f,
            locationListener
        )
    }

    // Mendapatkan resep dari API
    private fun fetchRecipes() {
        RetrofitClient.instance.getRecipes().enqueue(object : Callback<RecipeResponse> {
            override fun onResponse(call: Call<RecipeResponse>, response: Response<RecipeResponse>) {
                if (response.isSuccessful) {
                    val recipes = response.body()?.data?.recipes
                    if (!recipes.isNullOrEmpty()) {
                        recipeAdapter = RecipeAdapter(this@HomeActivity, recipes)
                        binding.recyclerViewRecipes.adapter = recipeAdapter
                    }
                }
                // Setelah selesai mengambil data, beritahu SwipeRefreshLayout untuk menghentikan loading indikator
                swipeRefreshLayout.isRefreshing = false
            }

            override fun onFailure(call: Call<RecipeResponse>, t: Throwable) {
                Log.e("HomeActivity", "Error fetching recipes", t)
                // Setelah selesai mengambil data, beritahu SwipeRefreshLayout untuk menghentikan loading indikator
                swipeRefreshLayout.isRefreshing = false
            }
        })
    }

    // Menampilkan nama lokasi di TextView
    private fun displayLocationName(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>? = geocoder.getFromLocation(
            latitude,
            longitude,
            1
        )
        val locationName = addresses?.get(0)?.getAddressLine(0)
        val greetingMessage = "Halo $username, Anda berada di $locationName"
        binding.helloAndLocationTextView.text = greetingMessage
    }

    // Handle hasil izin lokasi
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationAndRecipes()
            } else {
                Log.e("HomeActivity", "Location permission denied")
                // Handle jika izin lokasi ditolak
            }
        }
    }
}
