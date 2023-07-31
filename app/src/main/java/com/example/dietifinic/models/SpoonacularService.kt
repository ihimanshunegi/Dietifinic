package com.example.dietifinic.models

import com.example.dietifinic.models.MealPlanResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Query


interface SpoonacularService {

    @GET("mealplanner/generate")
    fun generateMealPlan(
        @Query("apiKey") apiKey: String,
        @Query("timeFrame") timeFrame: String,
        @Query("targetCalories") targetCalories: Int?,
        @Query("diet") diet:String?
    ): Call<MealPlanResponse>
    fun connectUser(
        @Field("username") username: String?,
        @Field("apiKey") apiKey: String?
    ): Call<ResponseBody?>?
}