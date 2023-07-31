package com.example.dietifinic.models




class MealTime {
    private val list=mutableListOf("Breakfast","Lunch","Dinner")
    public fun getMealTimeList():MutableList<String>{
        return list;
    }
}