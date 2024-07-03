package com.rahman.storyapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.rahman.storyapp.R
import com.rahman.storyapp.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {
    private var _binding: ActivityWelcomeBinding? = null
    private val binding get() = _binding!!
    var splashOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        splashScreen.setKeepOnScreenCondition { false }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPref = getSharedPreferences("login", Context.MODE_PRIVATE)
        val isLogin = sharedPref.getBoolean("isLogin", false)
        val startActiviti = {
            startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
            finish()
        }

        val content = binding.root
        content.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                return if (splashOpen) { /*isLoading view model disini?*/
                    content.viewTreeObserver.removeOnPreDrawListener(this)

                    if (isLogin) startActiviti()
                    true
                } else {
                    false
                }
            }
        })

        binding.btnLogin.setOnClickListener {
            sharedPref.edit().putBoolean("isLogin", true).apply()
            startActiviti()
        }
        binding.btnRegister.setOnClickListener { view ->
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Snackbar.make(view, "Aktifkan Night mode", Snackbar.LENGTH_LONG)
                .setAction("Apply") {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }.show()
        }

        splashOpen = true
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}