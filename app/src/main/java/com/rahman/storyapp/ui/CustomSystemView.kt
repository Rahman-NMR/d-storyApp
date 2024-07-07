package com.rahman.storyapp.ui

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

object CustomSystemView {
    fun edgeToEdge(rootId: View) {
        ViewCompat.setOnApplyWindowInsetsListener(rootId) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}