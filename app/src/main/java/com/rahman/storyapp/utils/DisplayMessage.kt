package com.rahman.storyapp.utils

import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

object DisplayMessage {
    fun showToast(context: Context, msg: String) {
        return Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun showSnackbar(view: View, text: String, actionText: String) {
        val snackbar = Snackbar.make(view, text, Snackbar.LENGTH_SHORT)
        snackbar.setAction(actionText) { snackbar.dismiss() }.show()
    }
}