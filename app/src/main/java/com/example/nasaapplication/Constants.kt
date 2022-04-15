package com.example.nasaapplication

class Constants {
    companion object {
        //----------------------
        // controller:
        const val API_KEY: String = BuildConfig.NASA_API_KEY
        // Индексы фрагментов
        const val DAY_PHOTO_FRAGMENT_INDEX: Int = 0
        const val SEARCH_WIKI_FRAGMENT_INDEX: Int = 1
        const val SEARCH_NASA_ARCHIVE_FRAGMENT_INDEX: Int = 2

        //----------------------
        // domain:
        //----------------------
        // repository:
        const val POD_BASE_URL: String = "https://api.nasa.gov/"
        const val NASA_ARCHIVE_BASE_URL: String = "https://images-api.nasa.gov/"

        //----------------------
        // ui:
        // TODO: Доработать изменение языка запроса в зависимости от языковых настроек пользователя
        const val WIKI_URL: String = "https://ru.wikipedia.org/wiki/"
        const val WIKI_ERROR_URL: String = "https://ru.m.wikipedia.org/wiki/wiki"
        const val SHARED_PREFERENCES_KEY: String = "Shared Preferences"
        const val SHARED_PREFERENCES_THEME_KEY: String = "Shared Preferences Is Theme Day"
        const val WEBVIEW_TEXT_HEADER_SUCCESS_BEGIN: String = "<html><head><style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/font/RobotoFlex_Regular.ttf\")}body {color: #"
        const val WEBVIEW_TEXT_HEADER_SUCCESS_END: String = ";font-family: MyFont;font-size: 17;text-align: justify;}</style></head><body>"
        const val WEBVIEW_TEXT_HEADER_NOTSUCCESS_BEGIN: String = "<html><head><style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/font/RobotoFlex_Regular.ttf\")}body {color: #"
        const val WEBVIEW_TEXT_HEADER_NOTSUCCESS_END: String = ";font-family: MyFont;font-size: 17;text-align: left;}</style></head><body><strong>"
        const val WEBVIEW_TEXT_FOOTER: String = "</strong></body></html>"
        //endregion

        // Константы для метода ShowUrlInWiki
        const val SHOWURLINWIKI_METHOD_NAME: String = "GET"
        const val SHOWURLINWIKI_READ_TIME_OUT: Int = 10000
        const val SHOWURLINWIKI_TEXT_CHARSER: String = "text/html; charset=utf-8"
        const val SHOWURLINWIKI_ENCODING: String = "utf-8"
        const val SEARCH_FIELD_TEXT_SIZE: Float = 20F
        const val INDEX_ADD_FAVORITE_MENU_ITEM: Int = 0
        const val ANGLE_TO_ROTATE_BOTTOM_FAB: Float = -360f

        // Приоритеты записей в "Избранное"
        const val PRIORITY_LOW: Int = 0
        const val PRIORITY_NORMAL: Int = 1
        const val PRIORITY_HIGH: Int = 2
    }
}