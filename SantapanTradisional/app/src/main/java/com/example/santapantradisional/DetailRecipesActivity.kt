package com.example.santapantradisional

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.santapantradisional.databinding.ActivityDetailRecipesBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailRecipesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailRecipesBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailRecipesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        val recipeId = intent.getIntExtra("RECIPE_ID", -1)
        Log.d("DetailRecipesActivity", recipeId.toString())
        if (isLoggedIn()) {
            fetchRecipeDetails(recipeId)
        } else {
            // Handle user not logged in
            finish()
        }

        binding.buttonBack.setOnClickListener {
            navigateToHomePage()
        }
    }

    private fun navigateToHomePage() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun fetchRecipeDetails(recipeId: Int) {
        binding.progressBar.visibility = View.VISIBLE
        binding.scrollViewContent.visibility = View.GONE
        binding.errorTextView.visibility = View.GONE

        RetrofitClient.instance.getRecipeById(recipeId).enqueue(object : Callback<DetailRecipeResponse> {
            override fun onResponse(call: Call<DetailRecipeResponse>, response: Response<DetailRecipeResponse>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val recipeResponse = response.body()?.data?.recipe
                    if (recipeResponse != null ) {
                        val recipe = recipeResponse

                        binding.textViewTitle.text = recipe.name
                        binding.textViewDescription.text = buildRecipeDescription(recipe)
                        Glide.with(this@DetailRecipesActivity).load(recipe.image).into(binding.imageViewFood)
                        binding.scrollViewContent.visibility = View.VISIBLE

                        Log.d("DetailRecipesActivity", "Recipe: $recipe")
                    } else {
                        binding.errorTextView.text = "onResponse"
                        binding.errorTextView.visibility = View.VISIBLE
                    }
                } else {
                    binding.errorTextView.text = "onGagal"
                    binding.errorTextView.visibility = View.VISIBLE
                    Log.e("DetailRecipesActivity", "Error response: ${response.errorBody()}")
                }
            }
            override fun onFailure(call: Call<DetailRecipeResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                binding.errorTextView.text = "Error loading recipe"
                binding.errorTextView.visibility = View.VISIBLE
                Log.e("DetailRecipesActivity", "Error fetching recipe details", t)
            }
        })
    }

    private fun buildRecipeDescription(recipe: Recipe): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("Bahan:\n")
        if (!recipe.ingredients.isNullOrEmpty()) {
            for (ingredient in recipe.ingredients) {
                stringBuilder.append("- $ingredient\n")
            }
        } else {
            stringBuilder.append("- Tidak ada informasi tentang bahan\n")
        }
        stringBuilder.append("\nResep:\n")
        for ((index, step) in recipe.steps.withIndex()) {
            stringBuilder.append("${index + 1}. $step\n")
        }
        return stringBuilder.toString()
    }

    private fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }
}
