package com.rahman.storyapp.ui.stories

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.rahman.storyapp.R
import com.rahman.storyapp.databinding.ActivityMainBinding
import com.rahman.storyapp.ui.CustomSystemView
import com.rahman.storyapp.ui.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        CustomSystemView.edgeToEdge(findViewById(R.id.main_main))

        val sharedPref = getSharedPreferences("login", Context.MODE_PRIVATE)

        binding.fabAddStory.setOnClickListener {
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}