package com.example.nasaapplication.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.nasaapplication.R
import com.example.nasaapplication.controller.navigation.NavigationContent

class MainActivity : AppCompatActivity() {
    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
    val navigationContent: NavigationContent = NavigationContent(supportFragmentManager)
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Запуск фрагмента с картинкой дня
        navigationContent.showDayPhotoFragment(false)
    }
}