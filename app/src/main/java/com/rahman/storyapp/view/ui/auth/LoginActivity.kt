package com.rahman.storyapp.view.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.Selection.setSelection
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.rahman.storyapp.R
import com.rahman.storyapp.databinding.ActivityLoginBinding
import com.rahman.storyapp.utils.DisplayMessage
import com.rahman.storyapp.view.ui.WelcomeActivity
import com.rahman.storyapp.view.viewmodel.ViewModelFactory
import com.rahman.storyapp.view.viewmodel.user.LoginViewModel

class LoginActivity : AppCompatActivity() {
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by viewModels { ViewModelFactory.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            viewModel()
            uiAction()
        }
    }

    private fun ActivityLoginBinding.uiAction() {
        topAppBarLogin.setNavigationOnClickListener { finish() }
        cbShowPassLogin.setOnCheckedChangeListener { _, isChecked ->
            edLoginPassword.inputType =
                if (isChecked) InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                else InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            setSelection(edLoginPassword.text, edLoginPassword.text?.length ?: 0)
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

    private fun ActivityLoginBinding.viewModel() {
        loginViewModel.clearMsg()
        loginViewModel.isLoading.observe(this@LoginActivity) {
            loginProgressbar.visibility = if (it) View.VISIBLE else View.GONE
        }
        loginViewModel.message.observe(this@LoginActivity) { msg ->
            if (msg != null) DisplayMessage.showToast(this@LoginActivity, msg)
        }
        loginViewModel.loginResult.observe(this@LoginActivity) { result ->
            if (result.error == false) {
                if (result.loginResult != null) {
                    val intent = Intent(this@LoginActivity, WelcomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
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