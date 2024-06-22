package com.example.santapantradisional

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.santapantradisional.models.Restaurant
import com.google.gson.Gson

class RestaurantAdapter(private val context: Context, private val restaurantList: List<Restaurant>) : RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    class RestaurantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val textViewLocation: TextView = itemView.findViewById(R.id.textViewLocation)
        val textViewCuisine: TextView = itemView.findViewById(R.id.textViewCuisine)
        val textViewRating: TextView = itemView.findViewById(R.id.textViewRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_restaurant, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val restaurant = restaurantList[position]
        holder.textViewName.text = restaurant.name
        holder.textViewLocation.text = restaurant.location
        holder.textViewCuisine.text = restaurant.cuisine
        holder.textViewRating.text = "Rating: ${restaurant.rating}"

        Glide.with(holder.itemView.context)
            .load(restaurant.imageUrl)
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailRestaurantActivity::class.java)
            intent.putExtra("restaurant", Gson().toJson(restaurant))
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }
}