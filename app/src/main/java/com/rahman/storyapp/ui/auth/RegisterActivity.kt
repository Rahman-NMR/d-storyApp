package com.rahman.storyapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.rahman.storyapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBarRegister.setNavigationOnClickListener { finish() }
        binding.registerProgressbar.visibility = View.GONE
        binding.actionRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString().trim()
            val email = binding.edRegisterEmail.text.toString().trim()
            val password = binding.edRegisterPassword.text.toString()
            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                if (binding.edRegisterName.error == null && binding.edRegisterEmail.error == null && binding.edRegisterPassword.error == null) {
                    binding.registerProgressbar.visibility = View.VISIBLE
                    Handler(Looper.getMainLooper()).postDelayed({
                        startActivity(Intent(this, LoginActivity::class.java))
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