package com.example.santapantradisional

data class RecipeResponse(
    val status: String,
    val data: Data
)

data class Data(
    val recipes: List<Recipe>
)

data class DetailRecipeResponse(
    val status: String,
    val data: RecipeData
)

data class RecipeData(
    val recipe: Recipe
)

data class Recipe(
    val id: Int,
    val name: String,
    val region: String,
    val portion: String,
    val image: String,
    val cookTime: String,
    val difficulty: String,
    val ingredients: List<String>,
    val steps: List<String>
)
