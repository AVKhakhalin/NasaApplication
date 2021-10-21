package com.example.nasaapplication.controller

import com.example.nasaapplication.BuildConfig

// Класс с константами для Controller
class ConstantsController(
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