package com.rahman.storyapp.ui.auth

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.rahman.storyapp.R

class AuthActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val fragmentType = intent.getIntExtra("FRAGMENT", 0)

        val fragment: Fragment? =
            when (fragmentType) {
                1 -> LoginFragment()
                2 -> RegisterFragment()
                else -> null
            }
        val title =
            when (fragmentType) {
                1 -> getString(R.string.login)
                2 -> getString(R.string.register)
                else -> ""
            }

        val progressIndicator = findViewById<CircularProgressIndicator>(R.id.progress_auth)
        val toolbar = findViewById<MaterialToolbar>(R.id.top_app_bar_auth)

        toolbar.setOnClickListener(this)

        if (fragment != null) {
            progressIndicator.visibility = View.GONE

            toolbar.title = title
            supportFragmentManager.beginTransaction().add(R.id.fragment_container, fragment).commit()
        } else {
            progressIndicator.visibility = View.VISIBLE
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.top_app_bar_auth -> finish()
        }
    }
}