package com.nwanneka.yokyo.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.nwanneka.yokyo.MainActivity
import com.nwanneka.yokyo.R
import com.nwanneka.yokyo.databinding.ActivityYokyoAuthBinding

class YokyoAuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityYokyoAuthBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYokyoAuthBinding.inflate(layoutInflater).apply {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                startActivity(Intent(applicationContext, MainActivity::class.java))
            } else
                setContentView(root)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp()
                || super.onSupportNavigateUp()
    }
}