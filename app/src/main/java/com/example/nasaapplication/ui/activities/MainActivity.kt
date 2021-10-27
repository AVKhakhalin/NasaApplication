package com.example.nasaapplication.ui.activities

import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import com.example.nasaapplication.R
import com.example.nasaapplication.controller.navigation.contents.NavigationContent
import com.example.nasaapplication.controller.navigation.contents.NavigationContentGetter
import com.example.nasaapplication.controller.navigation.contents.ViewPagerAdapter
import com.example.nasaapplication.controller.navigation.dialogs.NavigationDialogs
import com.example.nasaapplication.controller.navigation.dialogs.NavigationDialogsGetter
import com.example.nasaapplication.databinding.ActivityMainBinding
import com.example.nasaapplication.ui.ConstantsUi

class MainActivity: AppCompatActivity(), NavigationDialogsGetter, NavigationContentGetter {
    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
    // Навигационных переменны
    private val navigationContent: NavigationContent = NavigationContent(supportFragmentManager)
    private val navigationDialogs: NavigationDialogs = NavigationDialogs()
    // Установка темы приложения
    private var isThemeDay: Boolean = true
    // Binding
    lateinit var binding: ActivityMainBinding
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Применение темы к приложению
        // Считывание системных настроек
        if (savedInstanceState != null) {
            val sharedPreferences: SharedPreferences =
                getSharedPreferences(ConstantsUi.SHARED_PREFERENCES_KEY, MODE_PRIVATE)
            isThemeDay = sharedPreferences.getBoolean(
                ConstantsUi.SHARED_PREFERENCES_THEME_KEY, true)
            if (isThemeDay) {
                setTheme(R.style.Theme_NasaApplication_Day)
            } else {
                setTheme(R.style.Theme_NasaApplication_Night)
            }
        } else {
            // Применение тёмной темы при первом запуске приложения на девайсах на 10+ Android
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                setTheme(R.style.Theme_NasaApplication_Night)
            }
        }

        // Подключение Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setContentView(R.layout.activity_main)

        // Запуск фрагмента с картинкой дня
//        navigationContent.showDayPhotoFragment(false)

        // Подключение ViewPagerAdapter для запуска фрагментов
        binding.viewPager.adapter = ViewPagerAdapter(supportFragmentManager)
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }


    //region СЕТТЕР И ГЕТТЕР ДЛЯ ПАРАМЕТРА ТЕМЫ ПРИЛОЖЕНИЯ
    fun getIsThemeDay(): Boolean {
        return isThemeDay
    }
    fun setIsThemeDay(isThemeDay: Boolean) {
        this.isThemeDay = isThemeDay
    }
    //endregion

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        val sharedPreferences: SharedPreferences =
            getSharedPreferences(ConstantsUi.SHARED_PREFERENCES_KEY,
                AppCompatActivity.MODE_PRIVATE
            )
        var sharedPreferencesEditor: SharedPreferences.Editor = sharedPreferences.edit()
        sharedPreferencesEditor.putBoolean(ConstantsUi.SHARED_PREFERENCES_THEME_KEY, isThemeDay)
        sharedPreferencesEditor.apply()
    }

    override fun getNavigationDialogs(): NavigationDialogs {
        return navigationDialogs
    }

    override fun getNavigationContent(): NavigationContent {
        return navigationContent
    }
}