package com.rahman.storyapp.view.ui.auth

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.rahman.storyapp.R
import com.rahman.storyapp.databinding.ActivityLoginBinding
import com.rahman.storyapp.utils.DisplayMessage
import com.rahman.storyapp.view.viewmodel.user.LoginViewModel
import com.rahman.storyapp.view.viewmodel.ViewModelFactory

class LoginActivity : AppCompatActivity() {
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModelFactory = ViewModelFactory.getInstance(this)
        val loginViewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]

        with(binding) {
            viewModel(loginViewModel)
            uiAction(loginViewModel)
        }
    }

    private fun ActivityLoginBinding.uiAction(loginViewModel: LoginViewModel) {
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

    private fun ActivityLoginBinding.viewModel(loginViewModel: LoginViewModel) {
        loginViewModel.clearMsg()
        loginViewModel.isLoading.observe(this@LoginActivity) {
            loginProgressbar.visibility = if (it) View.VISIBLE else View.GONE
        }
        loginViewModel.message.observe(this@LoginActivity) { msg ->
            if (msg != null) DisplayMessage.showToast(this@LoginActivity, msg)
        }
        loginViewModel.loginResult.observe(this@LoginActivity) { result ->
            if (result.error == false) {
                if (result.loginResult != null) finish()
            }
        }
    }

    private fun ActivityLoginBinding.showSnackbar(text: String) {
        DisplayMessage.showSnackbar(root, text, getString(R.string.oke))
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