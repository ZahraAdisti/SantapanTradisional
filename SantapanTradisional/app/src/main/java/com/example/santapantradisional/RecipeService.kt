package com.example.santapantradisional

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface RecipeService {
    @GET("recipes")
    fun getRecipes(): Call<RecipeResponse>

    @GET("recipes/{id}")
    fun getRecipeById(@Path("id") recipeId: Int): Call<DetailRecipeResponse>
}

