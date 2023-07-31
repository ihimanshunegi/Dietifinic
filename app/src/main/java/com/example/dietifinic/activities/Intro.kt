package com.example.dietifinic.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.dietifinic.R

class Intro : AppCompatActivity() {
    lateinit var button: Button
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        button=findViewById(R.id.letsGo)
        button.setOnClickListener{
            val intent=Intent(this,SignupActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}