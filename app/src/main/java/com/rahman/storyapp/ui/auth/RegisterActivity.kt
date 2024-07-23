package com.rahman.storyapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.rahman.storyapp.R
import com.rahman.storyapp.databinding.ActivityRegisterBinding
import com.rahman.storyapp.di.Injection

class RegisterActivity : AppCompatActivity() {
    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModelFactory = ViewModelFactory(Injection.provideRepository(this))
        val registerViewModel = ViewModelProvider(this, viewModelFactory)[RegisterViewModel::class.java]

        with(binding) {
            viewModel(registerViewModel)
            topAppBarRegister.setNavigationOnClickListener { finish() }
            registerProgressbar.visibility = View.GONE
            cbShowPassRegister.setOnCheckedChangeListener { _, isChecked ->
                edRegisterPassword.inputType =
                    if (isChecked) InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    else InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
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
    }

    private fun ActivityRegisterBinding.viewModel(registerViewModel: RegisterViewModel) {
        registerViewModel.clearMsg()
        registerViewModel.isLoading.observe(this@RegisterActivity) {
            registerProgressbar.visibility = if (it) View.VISIBLE else View.GONE
        }
        registerViewModel.message.observe(this@RegisterActivity) { msg ->
            if (msg != null) Toast.makeText(this@RegisterActivity, msg, Toast.LENGTH_SHORT).show()
        }
        registerViewModel.registerResult.observe(this@RegisterActivity) { result ->
            if (result.error == false) {
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun ActivityRegisterBinding.showSnackbar(text: String) {
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