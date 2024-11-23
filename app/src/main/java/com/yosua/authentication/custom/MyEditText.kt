package com.yosua.authentication.custom

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText

class MyEditText @JvmOverloads constructor(
    context : Context, attrs : AttributeSet? = null
) : TextInputEditText(context, attrs) {
    init {
        addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(
                p0 : CharSequence?,
                p1 : Int,
                p2 : Int,
                p3 : Int,
            ) {

            }

            override fun onTextChanged(
                p0 : CharSequence?,
                p1 : Int,
                p2 : Int,
                p3 : Int,
            ) {
                if (p0.toString().length < 8){
                    setError("Password tidak boleh kurang dari 8 karakter", null)
                } else {
                    error = null
                }
            }

            override fun afterTextChanged(p0 : Editable?) {
            }
        })
    }
}