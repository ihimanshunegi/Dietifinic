package com.example.dietifinic.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.dietifinic.R
import com.example.dietifinic.adapter.MealAdapter
import com.example.dietifinic.databinding.FragmentHomeBinding
import com.example.dietifinic.models.Meal
import com.example.dietifinic.models.MealPlanResponse
import com.example.dietifinic.models.MealTime
import com.example.dietifinic.models.Nutrients
import com.example.dietifinic.models.SavedMeal
import com.example.dietifinic.models.SpoonacularService
import com.example.dietifinic.models.User
import com.example.dietifinic.models.UserName
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
//
object Constants {
//    const val API_KEY = getString(R.string.apiKey)
    const val TIME_FRAME = "day"
    const val USERS_COLLECTION = "users"
    const val SAVED_MEAL_COLLECTION = "savedMeal"
}

class HomeFragment : Fragment() {
    //binding class objects
    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    private var adapter: MealAdapter= MealAdapter(emptyList(), emptyList())
    var user: User?=User() //instantiation of User class
    var savedMeal: SavedMeal?= SavedMeal() //instantiation of User class
    var userName: UserName?=UserName() //instantiation of User class
    val userId= FirebaseAuth.getInstance().currentUser?.uid.toString() //fetching current userId
    private val mealsList: MutableList<Meal> = mutableListOf()
    private var mealTime=MealTime()
    val db = Firebase.firestore

    private lateinit var lastGeneratedDate: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view=binding.root
        dataBaseCall(view)
        return view
    }

    private fun generateDietPlan(view:View) {

        lastGeneratedDate=savedMeal?.date.toString()
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        if (lastGeneratedDate == currentDate) {
            // Diet plan for today already generated, no need to generate again
            // Display the existing diet plan
            val existingDietPlan = savedMeal?.savedMealPlan.toString()
            existingDietPlan?.let {
                // Use the existing diet plan to display nutrients and meals
                mealsList.clear()
                val mealPlanResponse = Gson().fromJson(it, MealPlanResponse::class.java)

                displayNutrients(view, mealPlanResponse.nutrients)
                mealsList.addAll(mealPlanResponse.meals ?: emptyList())
                adapter.notifyDataSetChanged()
            }
        } else {
            // Generate a new diet plan for the day
            generateNewDietPlan(view,currentDate)
        }
    }

    fun displayInfo(view: View){
        val dateFormat= SimpleDateFormat("EEEE ',' dd MMMM")
        val currentDateAndTime = dateFormat.format(Date())

        binding.setName.setText("Hi, ${userName?.name}!")
        binding.setDate.setText(currentDateAndTime)
    }//eof

    private fun displayNutrients(view: View,nutrients: Nutrients) {
        binding.textViewCalories.text =  nutrients.calories.toString()
        binding.textViewCarbs.text = nutrients.carbohydrates.toString()
        binding.textViewFat.text = nutrients.fat.toString()
        binding.textViewProtein.text = nutrients.protein.toString()
    }//eof

    private fun setRecyclerView(view:View){
        adapter = MealAdapter(mealsList,mealTime.getMealTimeList()) //passing list as parameters for constructor
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
//        binding.recyclerView.scrollToPosition(0)
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerView)
    }//eof

    private fun generateNewDietPlan(view:View,currentDate:String){
        val apiKey = getString(R.string.apiKey)
        val targetCalories = user?.targetCal
        val dietPreference=user?.dietType

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.spoonacular.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(SpoonacularService::class.java)

        val call = service.generateMealPlan(apiKey,Constants.TIME_FRAME, targetCalories,dietPreference)
        //genetateMealPlan returns Call<MealPlanResponse>
        call.enqueue(object : Callback<MealPlanResponse> {
            override fun onResponse(call: Call<MealPlanResponse>, response: Response<MealPlanResponse>) {
                if (response.isSuccessful) {
                    val mealPlan = response.body()
                    mealPlan?.let {
                        val dietPlanJson = Gson().toJson(it)

//                        sharedPreferences.edit().putString(DIET_PLAN_KEY, dietPlanJson).apply()
//                        sharedPreferences.edit().putString(SAVED_DATE_KEY, currentDate).apply()

                        val newMealPlan=SavedMeal(currentDate,dietPlanJson)
                        db.collection(Constants.SAVED_MEAL_COLLECTION)
                            .document(userId).set(newMealPlan)
                            .addOnSuccessListener { Log.d("thanks", "DocumentSnapshot successfully written!") }
                            .addOnFailureListener { e -> Log.w("sorry", "Error writing document", e) }
                        displayNutrients(view, it.nutrients)
                        mealsList.addAll(it.meals ?: emptyList())
                        adapter.notifyDataSetChanged()
                    }
//
                } else {
                    // Handle API error
                }

            }

            override fun onFailure(call: Call<MealPlanResponse>, t: Throwable) {
                // Handle network or request failure
            }
        })

    }//end of function

    private fun dataBaseCall(view: View) {
        CoroutineScope(Dispatchers.IO).launch {
            // Fetch data from Firebase asynchronously
            val userDeferred = async { fetchUser() }
            val userNameDeferred = async { fetchUserName() }
            val savedMealDeferred=async{savedMealPlan()}

            // Wait for both deferred values to complete
            val userResult = userDeferred.await()
            val userNameResult = userNameDeferred.await()
            val savedMealResult = savedMealDeferred.await()


            // Update UI on the main thread with the fetched data
            withContext(Dispatchers.Main) {
                user = userResult
                userName = userNameResult
                savedMeal=savedMealResult
                setRecyclerView(view)
                // Call generateDietPlan only after user object is initialized
                generateDietPlan(view)

                // Now you can also call other functions that depend on user and userName objects
                displayInfo(view)

            }
        }
    }//eof
    private suspend fun fetchUser(): User? {
        val docRef = db.collection(Constants.SAVED_MEAL_COLLECTION).document(userId).get().await()
        return docRef.toObject<User>()
    }
    private suspend fun savedMealPlan(): SavedMeal? {
        val docRef = db.collection(Constants.SAVED_MEAL_COLLECTION).document(userId).get().await()
        return docRef.toObject<SavedMeal>()
    }
    private suspend fun fetchUserName(): UserName? {
        val ref = db.collection("User Name").document(userId).get().await()
        return ref.toObject<UserName>()
    }
}


//class HomeFragment : Fragment(R.layout.fragment_home) {
////    private var mealTime=MealTime()
//    // ViewModel to handle data and business logic
//    val viewModel : HomeViewModel by viewModels()
//    private val mealsList: MutableList<Meal> = mutableListOf()
//
//
//    private var _binding: FragmentHomeBinding? = null
//    private val binding get() = _binding!!
//
//    private val adapter: MealAdapter = MealAdapter(emptyList(), emptyList())
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        _binding = FragmentHomeBinding.bind(view)
//
//        setupRecyclerView()
//        viewModel.initData()
//        viewModel.getUserData.observe(viewLifecycleOwner) { userName ->
//            displayUserInfo(userName)
//        }
//        viewModel.getSavedMeal.observe(viewLifecycleOwner) { savedMeal ->
//            generateDietPlan(savedMeal)
//        }
//    }
//
//    private fun setupRecyclerView() {
//        binding.recyclerView.adapter = adapter
//        binding.recyclerView.layoutManager =
//            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//        val snapHelper = PagerSnapHelper()
//        snapHelper.attachToRecyclerView(binding.recyclerView)
//    }
//    private fun displayUserInfo(userName: UserName?) {
//        val dateFormat = SimpleDateFormat("EEEE ',' dd MMMM", Locale.getDefault())
//        val currentDateAndTime = dateFormat.format(Date())
//        binding.setName.text = "Hi, ${userName?.name}!"
//        binding.setDate.text = currentDateAndTime
//    }
//
//    private fun displayNutrients(nutrients: Nutrients) {
//        binding.textViewCalories.text = nutrients.calories.toString()
//        binding.textViewCarbs.text = nutrients.carbohydrates.toString()
//        binding.textViewFat.text = nutrients.fat.toString()
//        binding.textViewProtein.text = nutrients.protein.toString()
//    }
//
//    private fun generateDietPlan(savedMeal: SavedMeal?) {
//        val lastGeneratedDate = savedMeal?.date.toString()
//        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
//
//        if (lastGeneratedDate == currentDate) {
//            val existingDietPlan = savedMeal?.savedMealPlan.toString()
//            existingDietPlan?.let {
//                mealsList.clear()
//                val mealPlanResponse = Gson().fromJson(it, MealPlanResponse::class.java)
//                displayNutrients(mealPlanResponse.nutrients)
//                mealsList.addAll(mealPlanResponse.meals ?: emptyList())
//                adapter.notifyDataSetChanged()
//            }
//        } else {
//            viewModel.generateNewDietPlan(currentDate)
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}