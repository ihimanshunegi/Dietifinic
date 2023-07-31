package com.example.dietifinic.models

import com.example.dietifinic.models.Meal
import com.example.dietifinic.models.Nutrients

data class MealPlanResponse(
    val meals: List<Meal>,
    val nutrients: Nutrients
)
