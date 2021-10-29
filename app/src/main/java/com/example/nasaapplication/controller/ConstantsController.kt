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

        // Заголовки фрагментов
        @JvmField
        val DAY_PHOTO_FRAGMENT_TITLE: String = "Фото дня"
        @JvmField
        val SEARCH_WIKI_FRAGMENT_TITLE: String = "Поиск"

        // Индексы фрагментов
        @JvmField
        val DAY_PHOTO_FRAGMENT_INDEX: Int = 0
        val SEARCH_WIKI_FRAGMENT_INDEX: Int = 1
    }
}