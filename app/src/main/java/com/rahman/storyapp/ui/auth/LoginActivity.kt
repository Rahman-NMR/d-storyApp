package com.rahman.storyapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.rahman.storyapp.R
import com.rahman.storyapp.databinding.ActivityLoginBinding
import com.rahman.storyapp.ui.CustomSystemView
import com.rahman.storyapp.ui.stories.MainActivity

class LoginActivity : AppCompatActivity() {
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)
        CustomSystemView.edgeToEdge(findViewById(R.id.main_login))

        binding.topAppBarLogin.setNavigationOnClickListener { finish() }
        binding.loginProgressbar.visibility = View.GONE
        binding.actionLogin.setOnClickListener {
            binding.loginProgressbar.visibility = View.VISIBLE
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }, 1500)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}