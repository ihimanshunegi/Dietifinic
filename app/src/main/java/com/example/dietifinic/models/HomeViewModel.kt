package com.example.dietifinic.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dietifinic.fragments.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class HomeViewModel : ViewModel() {
    private val _getUserData = MutableLiveData<User?>()
    val getUserData: LiveData<User?> get() = _getUserData

    private val _getSavedMeal = MutableLiveData<SavedMeal?>()
    val getSavedMeal: LiveData<SavedMeal?> get() = _getSavedMeal

    private val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private val db = Firebase.firestore

    fun initData() {
        CoroutineScope(Dispatchers.IO).launch {
            val userDeferred = async { fetchUser() }
            val savedMealDeferred = async { fetchSavedMeal() }

            val userResult = userDeferred.await()
            val savedMealResult = savedMealDeferred.await()

            withContext(Dispatchers.Main) {
                _getUserData.value = userResult
                _getSavedMeal.value = savedMealResult
            }
        }
    }

    private suspend fun fetchUser(): User? {
        val docRef = db.collection(Constants.USERS_COLLECTION).document(userId).get().await()
        return docRef.toObject<User>()
    }

    private suspend fun fetchSavedMeal(): SavedMeal? {
        val docRef = db.collection(Constants.SAVED_MEAL_COLLECTION).document(userId).get().await()
        return docRef.toObject<SavedMeal>()
    }

    fun generateNewDietPlan(currentDate: String) {
        // Retrofit and API call logic
        // ...
    }

    override fun onCleared() {
        super.onCleared()
        // Clean up resources if needed
    }
}