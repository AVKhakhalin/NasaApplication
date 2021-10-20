package com.example.nasaapplication.controller

import android.content.Context
import com.example.nasaapplication.BuildConfig
import com.example.nasaapplication.R
import com.example.nasaapplication.ui.activities.MainActivity
import kotlinx.coroutines.withContext

// Класс с константами для Controller
class ConstantsController(
    private val context: Context
) {
    companion object {
        @JvmField
        val ERROR_NO_API_KEY: String = "Вам нужен API-ключ."
        @JvmField
        val ERROR_UNKNOWN: String = "Неизвестная ошибка."
        @JvmField
        val API_KEY: String = BuildConfig.NASA_API_KEY

    }
}