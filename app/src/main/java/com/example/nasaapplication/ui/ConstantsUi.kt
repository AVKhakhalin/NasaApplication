package com.example.nasaapplication.ui

// Класс с константами для Ui
class ConstantsUi {
    companion object {
        @JvmField
        val WIKI_URL: String = "https://en.wikipedia.org/wiki/"
        @JvmField
        val DAY_PHOTO_TEXT: String ="\"Фотография дня на \""
        @JvmField
        val SHARED_PREFERENCES_KEY: String = "Shared Preferences"
        @JvmField
        val SHARED_PREFERENCES_THEME_KEY: String = "Shared Preferences Is Theme Day"

        // Сообщения об ошибках
        @JvmField
        val ERROR_LINK_EMPTY: String = "Ссылка пуста"
    }
}