package com.example.dietifinic.activities


import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.dietifinic.R
import com.example.dietifinic.fragments.ChallengeFragment
import com.example.dietifinic.fragments.HomeFragment
import com.example.dietifinic.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var fragment1: Fragment
    lateinit var fragment2: Fragment
    lateinit var fragment3: Fragment
    lateinit var fragment4: Fragment
    lateinit var fragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initialization()
        fragmentManager = supportFragmentManager
        setFragment(fragment1)
        fragmentCall()
    }

    private fun fragmentCall() {
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {
                    setFragment(fragment1)
                    true
                }

                R.id.page_2 -> {
                    setFragment(fragment2)
                    true
                }

                R.id.page_3 -> {
                    setFragment(fragment2)
                    true
                }

                R.id.page_4 -> {
                    setFragment(fragment4)
                    true
                }
                else -> false
            }
        }
    }

    private fun initialization() {
        bottomNavigationView=findViewById(R.id.bottom_navigation)
        fragment1=HomeFragment()
        fragment2=ChallengeFragment()
        fragment3=ChallengeFragment()
        fragment4= ProfileFragment()
    }

    private fun setFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }

}


