package com.rahman.storyapp.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.rahman.storyapp.R

class CustomEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs), View.OnTouchListener {
    private var visibilityOn: Drawable = ContextCompat.getDrawable(context, R.drawable.icon_visibility_24px) as Drawable
    private var visibilityOff: Drawable = ContextCompat.getDrawable(context, R.drawable.icon_visibility_off_24px) as Drawable
    private var person: Drawable = ContextCompat.getDrawable(context, R.drawable.icon_person_24px) as Drawable
    private var mail: Drawable = ContextCompat.getDrawable(context, R.drawable.icon_mail_24px) as Drawable
    private var lock: Drawable = ContextCompat.getDrawable(context, R.drawable.icon_lock_24px) as Drawable
    private var isPasswordVisible: Boolean = false

    init {
        setOnTouchListener(this)

        val paddingMed = resources.getDimensionPixelSize(R.dimen.medium_dimens)
        val paddingNor = resources.getDimensionPixelSize(R.dimen.normal_dimens)
        setPadding(paddingNor, paddingNor, paddingNor, paddingNor)
        compoundDrawablePadding = paddingMed

        when (inputType) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PERSON_NAME or InputType.TYPE_TEXT_FLAG_CAP_WORDS -> {
                setButtonDrawables(startOfTheText = person)
            }

            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS -> {
                setButtonDrawables(startOfTheText = mail)
            }

            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD -> {
                setButtonDrawables(startOfTheText = lock, endOfTheText = if (isPasswordVisible) visibilityOff else visibilityOn)
            }
        }

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                when (inputType) {
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PERSON_NAME or InputType.TYPE_TEXT_FLAG_CAP_WORDS -> {
                        if (s.isNotEmpty() && s.length < 3) setError(context.getString(R.string.error_name), null)
                        else error = null
                    }

                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS -> {
                        if (s.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(s).matches()) setError(context.getString(R.string.error_email), null)
                        else error = null
                    }

                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD -> {
                        if (s.isNotEmpty() && s.length < 8) setError(context.getString(R.string.error_password), null)
                        else error = null
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        minHeight = resources.getDimensionPixelSize(R.dimen.min_height)
        background = ContextCompat.getDrawable(context, R.drawable.bg_edittext_stroke2_8)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(startOfTheText, topOfTheText, endOfTheText, bottomOfTheText)
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val visibilityBtnStart: Float
            val visibilityBtnEnd: Float
            var isBtnVisibilityClicked = false

            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                visibilityBtnEnd = (visibilityOn.intrinsicWidth + paddingStart).toFloat()
                when {
                    event.x < visibilityBtnEnd -> isBtnVisibilityClicked = true
                }
            } else {
                visibilityBtnStart = (width - paddingEnd - visibilityOn.intrinsicWidth).toFloat()
                when {
                    event.x > visibilityBtnStart -> isBtnVisibilityClicked = true
                }
            }

            if (isBtnVisibilityClicked) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        isPasswordVisible = !isPasswordVisible
                        inputType =
                            if (isPasswordVisible) InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                            else InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

                        setSelection(text!!.length)
                        setButtonDrawables(startOfTheText = lock, endOfTheText = if (isPasswordVisible) visibilityOff else visibilityOn)

                        return true
                    }

                    else -> return false
                }
            } else return false
        }
        return false
    }
}