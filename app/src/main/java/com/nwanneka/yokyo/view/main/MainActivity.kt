package com.nwanneka.yokyo.view.main

import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nwanneka.yokyo.R
import com.nwanneka.yokyo.databinding.ActivityMainBinding
import com.nwanneka.yokyo.view.utils.hide
import com.nwanneka.yokyo.view.utils.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }

        val actionBar = (supportActionBar as ActionBar)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        findViewById<BottomNavigationView>(R.id.bottomNavigationMenu)
            .setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.fragmentAbout || destination.id == R.id.mapFragment) {
                actionBar.setDisplayHomeAsUpEnabled(true)
                actionBar.setDefaultDisplayHomeAsUpEnabled(true)
                binding.bottomNavigationMenu.hide()
            } else {
                actionBar.setDisplayHomeAsUpEnabled(false)
                actionBar.setDefaultDisplayHomeAsUpEnabled(false)
                binding.bottomNavigationMenu.show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
                || super.onSupportNavigateUp()
    }
}