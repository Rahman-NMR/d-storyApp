package com.rahman.storyapp.view.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.rahman.storyapp.R

class CustomEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {
    private var person: Drawable = ContextCompat.getDrawable(context, R.drawable.icon_person_24px) as Drawable
    private var mail: Drawable = ContextCompat.getDrawable(context, R.drawable.icon_mail_24px) as Drawable
    private var lock: Drawable = ContextCompat.getDrawable(context, R.drawable.icon_lock_24px) as Drawable
    private var customInputType: Int = -1

    init {
        val paddingMed = resources.getDimensionPixelSize(R.dimen.medium_dimens)
        val paddingNor = resources.getDimensionPixelSize(R.dimen.normal_dimens)
        setPadding(paddingNor, paddingNor, paddingNor, paddingNor)
        compoundDrawablePadding = paddingMed

        attrs?.let {
            val typeArray = context.obtainStyledAttributes(it, R.styleable.CustomEditText, 0, 0)
            customInputType = typeArray.getInt(R.styleable.CustomEditText_customInputType, -1)
            typeArray.recycle()
        }

        when (customInputType) {
            1 -> setButtonDrawables(start = person)
            2 -> setButtonDrawables(start = mail)
            3 -> setButtonDrawables(start = lock)
        }

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                when (customInputType) {
                    1 -> {
                        error = if (s.isNotEmpty() && s.length < 3) context.getString(R.string.error_name)
                        else null
                    }

                    2 -> {
                        error = if (s.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(s).matches()) context.getString(R.string.error_email)
                        else null
                    }

                    3 -> {
                        error = if (s.isNotEmpty() && s.length < 8) context.getString(R.string.error_password)
                        else null
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
        textAlignment = TEXT_ALIGNMENT_VIEW_START
    }

    private fun setButtonDrawables(start: Drawable? = null, top: Drawable? = null, end: Drawable? = null, bottom: Drawable? = null) {
        setCompoundDrawablesWithIntrinsicBounds(start, top, end, bottom)
    }
}