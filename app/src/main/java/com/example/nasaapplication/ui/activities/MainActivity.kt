package com.example.nasaapplication.ui.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import com.example.nasaapplication.R
import com.example.nasaapplication.controller.ConstantsController
import com.example.nasaapplication.controller.navigation.contents.NavigationContent
import com.example.nasaapplication.controller.navigation.contents.NavigationContentGetter
import com.example.nasaapplication.controller.navigation.dialogs.NavigationDialogs
import com.example.nasaapplication.controller.navigation.dialogs.NavigationDialogsGetter

class MainActivity: AppCompatActivity(), NavigationDialogsGetter, NavigationContentGetter {
    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
    private val navigationContent: NavigationContent = NavigationContent(supportFragmentManager)
    private val navigationDialogs: NavigationDialogs = NavigationDialogs()
    private val constantsController: ConstantsController = ConstantsController()
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Применение темы к приложению
        setTheme(R.style.Theme_NasaApplication)
        setContentView(R.layout.activity_main)

        // Запуск фрагмента с картинкой дня
        navigationContent.showDayPhotoFragment(false)
    }

    override fun getNavigationDialogs(): NavigationDialogs {
        return navigationDialogs
    }

    override fun getNavigationContent(): NavigationContent {
        return navigationContent
    }
}