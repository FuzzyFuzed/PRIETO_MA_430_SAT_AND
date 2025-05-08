package com.example.prieto_financex.ui.bottom_nav

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.prieto_financex.R
import com.example.prieto_financex.ui.auth.LogoutFragment
import com.example.prieto_financex.ui.exp_and_cat.ExpenseFragment
import com.example.prieto_financex.ui.profile.ProfileFragment
import com.example.prieto_financex.ui.profile.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class AppSummaryActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_summary)

        bottomNavigationView = findViewById(R.id.bottom_nav)

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        initializeBottomNavigation()
    }

    private fun initializeBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener { item ->
            val selectedFragment: Fragment = when (item.itemId) {
                R.id.home -> HomeFragment()
                R.id.expense -> ExpenseFragment()
                R.id.profile -> ProfileFragment()
                R.id.settings -> SettingsFragment()
                R.id.logout -> LogoutFragment()
                else -> return@setOnItemSelectedListener false
            }

            loadFragment(selectedFragment)
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.layout_content, fragment)
            .commit()
    }
}