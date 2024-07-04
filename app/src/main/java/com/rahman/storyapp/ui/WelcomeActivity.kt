package com.rahman.storyapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.rahman.storyapp.R
import com.rahman.storyapp.databinding.ActivityWelcomeBinding
import com.rahman.storyapp.ui.auth.AuthActivity

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

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            intent.putExtra("FRAGMENT", 1)

            startActivity(intent)
        }
        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            intent.putExtra("FRAGMENT", 2)

            startActivity(intent)
        }

        splashOpen = true
    }

    private fun loginSession() {
        val sharedPref = getSharedPreferences("login", Context.MODE_PRIVATE)
        val isLogin = sharedPref.getBoolean("isLogin", false)

        if (isLogin) {
            startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
            finish()
        }
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