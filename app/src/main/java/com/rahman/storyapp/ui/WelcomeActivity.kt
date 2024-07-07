package com.rahman.storyapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.rahman.storyapp.R
import com.rahman.storyapp.databinding.ActivityWelcomeBinding
import com.rahman.storyapp.ui.auth.LoginActivity
import com.rahman.storyapp.ui.auth.RegisterActivity

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
        CustomSystemView.edgeToEdge(findViewById(R.id.main_welcome))

        val content = binding.root
        content.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                return if (splashOpen) { /*isLoading view model disini?*/
                    content.viewTreeObserver.removeOnPreDrawListener(this)

                    loginSession()
                    true
                } else {
                    false
                }
            }
        })

        binding.btnLogin.setOnClickListener { startActivity(Intent(this, LoginActivity::class.java)) }
        binding.btnRegister.setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }

        splashOpen = true
    }

    private fun loginSession() {
        /*val sharedPref = getSharedPreferences("login", Context.MODE_PRIVATE)
        val isLogin = sharedPref.getBoolean("isLogin", false)

        if (isLogin) {
            startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
            finish()
        }*/
    }

    override fun onResume() {
        super.onResume()
        loginSession()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}