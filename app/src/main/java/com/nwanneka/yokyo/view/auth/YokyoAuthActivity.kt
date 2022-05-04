package com.nwanneka.yokyo.view.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.nwanneka.yokyo.MainActivity
import com.nwanneka.yokyo.R
import com.nwanneka.yokyo.databinding.ActivityYokyoAuthBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class YokyoAuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityYokyoAuthBinding

    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYokyoAuthBinding.inflate(layoutInflater).apply {
            val currentUser = authViewModel.auth?.currentUser
            if (currentUser != null) {
                startActivity(Intent(applicationContext, MainActivity::class.java))
            } else
                setContentView(root)
        }

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
                || super.onSupportNavigateUp()
    }
}