package com.example.nasaapplication

import android.util.TypedValue

class Constants {
    companion object {
        //----------------------
        // controller:
        @JvmField
        val API_KEY: String = BuildConfig.NASA_API_KEY
        // Индексы фрагментов
        @JvmField
        val DAY_PHOTO_FRAGMENT_INDEX: Int = 0
        @JvmField
        val SEARCH_WIKI_FRAGMENT_INDEX: Int = 1
        @JvmField
        val SEARCH_NASA_ARCHIVE_FRAGMENT_INDEX: Int = 2

        //----------------------
        // domain:

        //----------------------
        // repository:
        @JvmField
        val POD_BASE_URL: String = "https://api.nasa.gov/"
        @JvmField
        val NASA_ARCHIVE_BASE_URL: String = "https://images-api.nasa.gov/"

        //----------------------
        // ui:
        @JvmField // TODO: Доработать изменение языка запроса в зависимости от языковых настроек пользователя
        val WIKI_URL: String = "https://ru.wikipedia.org/wiki/"
        @JvmField
        val WIKI_ERROR_URL: String = "https://ru.m.wikipedia.org/wiki/wiki"
        @JvmField
        val SHARED_PREFERENCES_KEY: String = "Shared Preferences"
        @JvmField
        val SHARED_PREFERENCES_THEME_KEY: String = "Shared Preferences Is Theme Day"
        @JvmField
        val WEBVIEW_TEXT_HEADER_SUCCESS_BEGIN: String = "<html><head><style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/font/RobotoFlex_Regular.ttf\")}body {color: #"
        @JvmField
        val WEBVIEW_TEXT_HEADER_SUCCESS_END: String = ";font-family: MyFont;font-size: 17;text-align: justify;}</style></head><body>"
        @JvmField
        val WEBVIEW_TEXT_HEADER_NOTSUCCESS_BEGIN: String = "<html><head><style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/font/RobotoFlex_Regular.ttf\")}body {color: #"
        @JvmField
        val WEBVIEW_TEXT_HEADER_NOTSUCCESS_END: String = ";font-family: MyFont;font-size: 17;text-align: left;}</style></head><body><strong>"
        @JvmField
        val WEBVIEW_TEXT_FOOTER: String = "</strong></body></html>"
        private val colorSecondaryVariantValue: TypedValue = TypedValue()
        //endregion

        // Константы для метода ShowUrlInWiki
        @JvmField
        val SHOWURLINWIKI_METHOD_NAME: String = "GET"
        @JvmField
        val SHOWURLINWIKI_READ_TIME_OUT: Int = 10000
        @JvmField
        val SHOWURLINWIKI_TEXT_CHARSER: String = "text/html; charset=utf-8"
        @JvmField
        val SHOWURLINWIKI_ENCODING: String = "utf-8"

        @JvmField
        val SEARCH_FIELD_TEXT_SIZE: Float = 20F
        @JvmField
        val INDEX_ADD_FAVORITE_MENU_ITEM: Int = 0
        @JvmField
        val ANGLE_TO_ROTATE_BOTTOM_FAB: Float = -360f

        // Приоритеты записей в "Избранное"
        @JvmField
        val PRIORITY_LOW: Int = 0
        @JvmField
        val PRIORITY_NORMAL: Int = 1
        @JvmField
        val PRIORITY_HIGH: Int = 2
    }
}