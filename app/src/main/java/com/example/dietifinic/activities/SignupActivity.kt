package com.example.dietifinic.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dietifinic.R
import com.example.dietifinic.databinding.ActivitySignupBinding
import com.example.dietifinic.models.SpoonacularService
import com.example.dietifinic.models.User
import com.example.dietifinic.models.UserName
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var mAuth: FirebaseAuth
    lateinit var userId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()

        binding.btnSignUp.setOnClickListener {
            val email=binding.etEmail.text.toString()
            if(isValidEmail(email)){
                signUp()
            }

            else
                binding.tilSignupEmail.error="Enter valid email"

        }
        binding.tvAlreadyHaveAccount.setOnClickListener { goToLogin() }

    }

    private fun signUp() {
        val name = binding.etName.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user: FirebaseUser? = mAuth.currentUser
                    userId=user?.uid.toString()

                    val pref=getSharedPreferences("Preference",MODE_PRIVATE)
                    val editor=pref.edit()
                    editor.putBoolean("Filled",false)
                    editor.apply()
                    saveUserData(userId,name)
                    goToQuestionnaire()
                } else {
                    var errorMessage = "Registration failed: ${task.exception?.message}"
                    if (task.exception is FirebaseAuthException) {
                        errorMessage = (task.exception as FirebaseAuthException).message ?: errorMessage
                    }
                    // Registration failed
                    showToast(errorMessage)
                }
            }
    }

    fun saveUserData(userId:String,name:String){
        var userName=UserName(name)
        val db = Firebase.firestore
        db.collection("User Name")
            .document(userId).set(userName)
            .addOnSuccessListener { Log.d("thanks", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("sorry", "Error writing document", e) }
    }
    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun goToQuestionnaire() {
        val intent=Intent(this, QuestionnaireActivity::class.java)
//        intent.putExtra("userName",binding.etName.text.toString())
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}

