package com.example.dietifinic.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.airbnb.lottie.LottieAnimationView
import com.example.dietifinic.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val animationView = findViewById<LottieAnimationView>(R.id.animationView)
        animationView.playAnimation()
        // Initialize Firebase Auth
        auth = Firebase.auth

        val pref=getSharedPreferences("Preference",MODE_PRIVATE)
        val firstTime=pref.getBoolean("firstStart",true)
        val filledQuestionnaire=pref.getBoolean("Filled",false)
        //if first time
        if(firstTime){
            intentCall(Intro::class.java)
            val editor=pref.edit()
            editor.putBoolean("firstStart",false)
            editor.apply()
        }
        else{

                val currentUser = auth.currentUser
                val targetActivity=
                    if(currentUser != null) {
                        if(filledQuestionnaire) MainActivity::class.java
                        else QuestionnaireActivity::class.java
                    }

                    else LoginActivity::class.java

                val handler: Handler = Handler()
                handler.postDelayed({ intentCall(targetActivity) }, 3000)


        }

    }
    private fun intentCall(targetActivity: Class<out AppCompatActivity>) {
        val intent= Intent(this,targetActivity)
        startActivity(intent)
        finish()
    }
}