package com.rahman.storyapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.rahman.storyapp.R
import com.rahman.storyapp.databinding.ActivityRegisterBinding
import com.rahman.storyapp.ui.CustomSystemView

class RegisterActivity : AppCompatActivity() {
    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityRegisterBinding.inflate(layoutInflater)

        setContentView(binding.root)
        CustomSystemView.edgeToEdge(findViewById(R.id.main_register))

        binding.topAppBarRegister.setNavigationOnClickListener { finish() }
        binding.registerProgressbar.visibility = View.GONE
        binding.actionRegister.setOnClickListener {
            binding.registerProgressbar.visibility = View.VISIBLE
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }, 1500)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}