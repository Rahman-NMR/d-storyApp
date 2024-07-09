package com.rahman.storyapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.rahman.storyapp.databinding.ActivityLoginBinding
import com.rahman.storyapp.ui.stories.MainActivity

class LoginActivity : AppCompatActivity() {
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBarLogin.setNavigationOnClickListener { finish() }
        binding.loginProgressbar.visibility = View.GONE
        binding.actionLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString().trim()
            val password = binding.edLoginPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (binding.edLoginEmail.error == null && binding.edLoginPassword.error == null) {
                    binding.loginProgressbar.visibility = View.VISIBLE
                    Handler(Looper.getMainLooper()).postDelayed({
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }, 1500)
                } else {
                    val snackbar = Snackbar.make(binding.root, "Pastikan tidak ada pesan error", Snackbar.LENGTH_SHORT)
                    snackbar.setAction("Oke") { snackbar.dismiss() }.show()
                }
            } else {
                val snackbar = Snackbar.make(binding.root, "Harap mengisi semua form", Snackbar.LENGTH_SHORT)
                snackbar.setAction("Oke") { snackbar.dismiss() }.show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}