package com.rahman.storyapp.utils

import android.content.Context
import android.widget.Toast

object ShowToast {
    fun short(context: Context, msg: String) {
        return Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}