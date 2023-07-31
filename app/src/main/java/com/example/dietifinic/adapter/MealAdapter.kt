package com.example.dietifinic.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.dietifinic.R
import com.example.dietifinic.models.Meal
import com.example.dietifinic.models.Nutrients
import com.squareup.picasso.Picasso


class MealAdapter(private val meals: List<Meal>,private val BLD: List<String>) : RecyclerView.Adapter<MealAdapter.MealViewHolder>() {
    inner class MealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardTitleTextView: TextView = itemView.findViewById(R.id.mealTitle)
        private val cardImageView: ImageView = itemView.findViewById(R.id.foodImage)

        val cardRecipeBtn: Button = itemView.findViewById(R.id.recipe)
        val nextMeal:TextView=itemView.findViewById(R.id.nextMeal)
        fun bind(meal: Meal,bld:String) {
            cardTitleTextView.text = meal.title
            val a:String="https://webknox.com/recipeImages/"
            val b:String="-556x370.jpg"
            val img:String=a+meal.id +b
            nextMeal.text=bld
            Picasso.get().load(img).into(cardImageView)
//            cardNutrientsTV.text= "Calories: " +nutrient.calories
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_meal_card, parent, false)
        return MealViewHolder(view)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val meal = meals[position]
        val bld=BLD[position]
//        val nutrient=nutrients[position]
        holder.bind(meal,bld)
        holder.cardRecipeBtn.setOnClickListener {
            val url = meal.sourceUrl // Replace with the actual link from your data model
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return meals.size
    }
}