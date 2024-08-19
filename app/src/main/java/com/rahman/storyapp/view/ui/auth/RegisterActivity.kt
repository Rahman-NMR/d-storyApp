package com.rahman.storyapp.view.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.Selection.setSelection
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.rahman.storyapp.R
import com.rahman.storyapp.databinding.ActivityRegisterBinding
import com.rahman.storyapp.utils.DisplayMessage
import com.rahman.storyapp.view.viewmodel.ViewModelFactory
import com.rahman.storyapp.view.viewmodel.user.RegisterViewModel

class RegisterActivity : AppCompatActivity() {
    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!
    private val registerViewModel: RegisterViewModel by viewModels { ViewModelFactory.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            viewModel()
            uiAction()
        }
    }

    private fun ActivityRegisterBinding.uiAction() {
        topAppBarRegister.setNavigationOnClickListener { finish() }
        cbShowPassRegister.setOnCheckedChangeListener { _, isChecked ->
            edRegisterPassword.inputType =
                if (isChecked) InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                else InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            setSelection(edRegisterPassword.text, edRegisterPassword.text?.length ?: 0)
        }
        actionRegister.setOnClickListener {
            hideKeyboard(currentFocus ?: View(this@RegisterActivity))
            currentFocus?.clearFocus()

            val name = edRegisterName.text.toString().trim()
            val email = edRegisterEmail.text.toString().trim()
            val password = edRegisterPassword.text.toString()
            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                if (edRegisterName.error == null && edRegisterEmail.error == null && edRegisterPassword.error == null) {
                    registerViewModel.register(name, email, password)
                } else showSnackbar(getString(R.string.msg_error_form))
            } else showSnackbar(getString(R.string.msg_empty_form))
        }
    }

    private fun ActivityRegisterBinding.viewModel() {
        registerViewModel.clearMsg()
        registerViewModel.isLoading.observe(this@RegisterActivity) { registerProgressbar.isVisible = it }
        registerViewModel.message.observe(this@RegisterActivity) { msg ->
            if (msg != null) DisplayMessage.showToast(this@RegisterActivity, msg)
        }
        registerViewModel.registerResult.observe(this@RegisterActivity) { result ->
            if (result.error == false) {
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun ActivityRegisterBinding.showSnackbar(text: String) {
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