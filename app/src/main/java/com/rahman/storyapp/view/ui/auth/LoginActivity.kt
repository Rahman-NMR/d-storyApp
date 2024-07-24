package com.rahman.storyapp.view.ui.auth

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.rahman.storyapp.R
import com.rahman.storyapp.databinding.ActivityLoginBinding
import com.rahman.storyapp.di.Injection
import com.rahman.storyapp.view.viewmodel.LoginViewModel
import com.rahman.storyapp.view.viewmodel.ViewModelFactoryUser

class LoginActivity : AppCompatActivity() {
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModelFactory = ViewModelFactoryUser(Injection.provideRepository(this))
        val loginViewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]

        with(binding) {
            viewModel(loginViewModel)

            topAppBarLogin.setNavigationOnClickListener { finish() }
            cbShowPassLogin.setOnCheckedChangeListener { _, isChecked ->
                edLoginPassword.inputType =
                    if (isChecked) InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    else InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            actionLogin.setOnClickListener {
                hideKeyboard(currentFocus ?: View(this@LoginActivity))
                currentFocus?.clearFocus()

                val email = edLoginEmail.text.toString().trim()
                val password = edLoginPassword.text.toString()
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    if (edLoginEmail.error == null && edLoginPassword.error == null) {
                        loginViewModel.login(email, password)
                    } else showSnackbar(getString(R.string.msg_error_form))
                } else showSnackbar(getString(R.string.msg_empty_form))
            }
        }
    }

    private fun ActivityLoginBinding.viewModel(loginViewModel: LoginViewModel) {
        loginViewModel.clearMsg()
        loginViewModel.isLoading.observe(this@LoginActivity) {
            loginProgressbar.visibility = if (it) View.VISIBLE else View.GONE
        }
        loginViewModel.message.observe(this@LoginActivity) { msg ->
            if (msg != null) Toast.makeText(this@LoginActivity, msg, Toast.LENGTH_SHORT).show()
        }
        loginViewModel.loginResult.observe(this@LoginActivity) { result ->
            if (result.error == false) {
                if (result.loginResult != null) finish()
            }
        }
    }

    private fun ActivityLoginBinding.showSnackbar(text: String) {
        val snackbar = Snackbar.make(root, text, Snackbar.LENGTH_SHORT)
        snackbar.setAction(getString(R.string.oke)) { snackbar.dismiss() }.show()
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}