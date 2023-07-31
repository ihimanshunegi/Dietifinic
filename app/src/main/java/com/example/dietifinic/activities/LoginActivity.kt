package com.example.dietifinic.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.dietifinic.R
import com.example.dietifinic.databinding.ActivityLoginBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    lateinit var binding:ActivityLoginBinding
    lateinit var auth:FirebaseAuth
    lateinit var email:String
    lateinit var password:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth=Firebase.auth

        binding.btnLogin.setOnClickListener{
            email= binding.etLoginEmail.text.toString()
            password= binding.etLoginPassword.text.toString()
            if(isValidEmail(email))
                signIn(email,password)
            else emailErrorHandling()
        }
        binding.tvCreateAccount.setOnClickListener{
            intentCall(SignupActivity::class.java)}
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    intentCall(MainActivity::class.java)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
//                    updateUI(null)
                }
            }
    }

    fun emailErrorHandling(){
        val isEmailValid = isValidEmail(email)
        if (isEmailValid) {
            // Email format is valid, remove error
            binding.layoutLoginEmail.error = null
        } else {
            // Email format is invalid, set error message
            binding.layoutLoginEmail.error = "Invalid email format"
        }
    }

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private fun intentCall(targetActivity: Class<out AppCompatActivity>) {
        val intent= Intent(this,targetActivity)
        startActivity(intent)
        finish()
    }

}