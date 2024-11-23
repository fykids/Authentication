package com.yosua.authentication.custom

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class EmailEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : TextInputEditText(context, attrs) {

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
                // Tidak perlu implementasi untuk sebelum teks berubah
            }

            override fun onTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
                // Tidak perlu implementasi untuk saat teks berubah
            }

            override fun afterTextChanged(p0: Editable?) {
                val email = p0?.toString()
                if (!isValidEmail(email)) {
                    // Menampilkan error pada TextInputLayout
                    (parent as? TextInputLayout)?.error = "Format email tidak valid"
                } else {
                    // Menghapus error jika format valid
                    (parent as? TextInputLayout)?.error = null
                }
            }
        })
    }

    // Fungsi untuk validasi format email
    private fun isValidEmail(email: String?): Boolean {
        // Regex untuk format email
        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
        return email != null && email.matches(emailPattern.toRegex())
    }
}
