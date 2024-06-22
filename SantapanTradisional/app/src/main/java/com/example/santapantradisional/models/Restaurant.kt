package com.example.santapantradisional.models
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurants")
data class Restaurant(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val location: String,
    val cuisine: String,
    val rating: Float,
    val imageUrl: String
)