package com.yosua.authentication.model.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun String.withDateFormat(): String {
    try {
        // Parse tanggal dari format ISO 8601
        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        val date = isoFormat.parse(this) ?: return "Tanggal tidak valid"

        // Format tanggal menjadi format yang diinginkan (contoh: dd/MM/yyyy)
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        return format.format(date)
    } catch (e: Exception) {
        return "Tanggal tidak valid"
    }
}