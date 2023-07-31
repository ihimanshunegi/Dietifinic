package com.example.dietifinic.activities


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.example.dietifinic.R
import com.example.dietifinic.databinding.ActivityQuestionnaireBinding
import com.example.dietifinic.models.User
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class QuestionnaireActivity : AppCompatActivity() {
    lateinit var binding:ActivityQuestionnaireBinding
    lateinit var selectedGender:String
    var targetCalories:Int=0
    var weight:Int=0
    var height:Int=0
    var age:Int=23
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityQuestionnaireBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.submitBtn.setOnClickListener {
            genderSelectedOption()
            if(binding.dietPreferenceTV.text.toString()=="") binding.dietPreferenceLayout.error="Select diet type"
            if(binding.activityLevelTV.text.toString()=="") binding.activityLevelTV.error="Select diet type"
            else{
                calculateTargetCalories()
                showToast(targetCalories)
                submitListener()
            }

        }
    }

    private fun genderSelectedOption() {
        val selectedGenderId = binding.radioGroup.checkedRadioButtonId
        val selectedGenderRadioButton = findViewById<RadioButton>(selectedGenderId)
        selectedGender = selectedGenderRadioButton.text.toString()
    }

    private fun submitListener() {
        val diet=binding.dietPreferenceTV.text.toString()
        val optionMap = mapOf("Gluter free" to "Gluter free", "Ketogenic" to "Ketogenic", "Eggetarian" to "Vegetarian",
            "Vegetarian" to "Lacto-Vegetarian", "Vegan" to "Vegan", "Non-vegetarian" to "Primal")
        val dietType=optionMap.get(diet).toString()
        val userId= FirebaseAuth.getInstance().currentUser?.uid.toString() //fetching current userId
        saveUserData(userId.toString(),dietType)
        val pref=getSharedPreferences("Preference", MODE_PRIVATE)
        val editor=pref.edit()
        editor.putBoolean("Filled",true)
        editor.apply()
        goToMainActivity()
    }

    private fun goToMainActivity() {
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }

    private fun showToast(checkedId: Int) {
        Toast.makeText(this,checkedId.toString(),Toast.LENGTH_SHORT).show()
    }

    fun saveUserData(userId:String,dietType:String){

        val user= User(selectedGender,targetCalories,dietType)
        val db = Firebase.firestore
        db.collection("users")
            .document(userId).set(user)
            .addOnSuccessListener { Log.d("thanks", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("sorry", "Error writing document", e) }
    }

    private fun calculateTargetCalories(){
        weight=binding.weight.text.toString().toInt();
        height=binding.height.text.toString().toInt();

        val activityLevels = resources.getStringArray(R.array.activity_level)
        // Find the index of "No exercise" in the array
        val activityLevelChoice:Int = activityLevels.indexOf(binding.activityLevelTV.text.toString())+1
        var activityFactor:Double
        var bmr:Double
        if(activityLevelChoice==1){
            activityFactor=1.2
        }
        else if(activityLevelChoice==2){
            activityFactor=1.375
        }
        else if(activityLevelChoice==3){
            activityFactor=1.55
        }
        else if(activityLevelChoice==4){
            activityFactor=1.725

        }else if(activityLevelChoice==5){
            activityFactor=1.9
        }
        else activityFactor=1.375
        if(selectedGender.equals("Male"))
            bmr=88.362+(13.397*weight)+(4.799*height)-(5.677*age)
        else
            bmr=447.593+(9.247*weight)+(3.098*height)-(4.330*age)

        targetCalories=(bmr*activityFactor).toInt()
    }
}