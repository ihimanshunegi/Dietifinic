package com.example.dietifinic.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.dietifinic.R
import com.example.dietifinic.activities.LoginActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        var btn: Button= view.findViewById(R.id.logOut)
        btn.setOnClickListener {
            Firebase.auth.signOut()
        intentCall(LoginActivity::class.java)
        }

        // Inflate the layout for this fragment
        return view
    }
    private fun intentCall(targetActivity: Class<out AppCompatActivity>) {
        val intent= Intent(context,targetActivity)
        startActivity(intent)
        activity?.finish()
    }

}