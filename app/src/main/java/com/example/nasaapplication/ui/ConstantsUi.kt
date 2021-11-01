package com.example.nasaapplication.ui

import android.os.Handler
import android.os.Looper
import java.io.BufferedReader
import java.io.InputStreamReader

// Класс с константами для Ui
class ConstantsUi {
    companion object {
        @JvmField // TODO: Доработать изменение языка запроса в зависимости от языковых настроек пользователя
        val WIKI_URL: String = "https://ru.wikipedia.org/wiki/"
        @JvmField
        val WIKI_ERROR_URL: String = "https://ru.m.wikipedia.org/wiki/wiki"
        @JvmField
        val DAY_PHOTO_TEXT: String ="\"Фотография дня\" от "
        @JvmField
        val SHARED_PREFERENCES_KEY: String = "Shared Preferences"
        @JvmField
        val SHARED_PREFERENCES_THEME_KEY: String = "Shared Preferences Is Theme Day"

        // Сообщения об ошибках
        @JvmField
        val ERROR_LINK_EMPTY: String = "Ссылка пуста"

        // Константы для метода ShowUrlInWiki
        @JvmField
        val SHOWURLINWIKI_METHOD_NAME: String = "GET"
        @JvmField
        val SHOWURLINWIKI_READ_TIME_OUT: Int = 10000
        @JvmField
        val SHOWURLINWIKI_TEXT_CHARSER: String = "text/html; charset=utf-8"
        @JvmField
        val SHOWURLINWIKI_ENCODING: String = "utf-8"
    }
}