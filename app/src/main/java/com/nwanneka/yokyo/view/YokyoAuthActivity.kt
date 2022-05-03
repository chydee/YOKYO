package com.nwanneka.yokyo.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.nwanneka.yokyo.R
import com.nwanneka.yokyo.databinding.ActivityYokyoAuthBinding

class YokyoAuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityYokyoAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYokyoAuthBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp()
                || super.onSupportNavigateUp()
    }
}