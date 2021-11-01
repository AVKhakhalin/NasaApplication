package com.example.nasaapplication.ui.activities

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.example.nasaapplication.R
import com.example.nasaapplication.controller.ConstantsController
import com.example.nasaapplication.controller.navigation.contents.NavigationContent
import com.example.nasaapplication.controller.navigation.contents.NavigationContentGetter
import com.example.nasaapplication.controller.navigation.contents.ViewPagerAdapter
import com.example.nasaapplication.controller.navigation.dialogs.NavigationDialogs
import com.example.nasaapplication.controller.navigation.dialogs.NavigationDialogsGetter
import com.example.nasaapplication.databinding.ActivityMainBinding
import com.example.nasaapplication.ui.ConstantsUi
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import java.lang.Thread.sleep

class MainActivity: AppCompatActivity(), NavigationDialogsGetter, NavigationContentGetter {
    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
    // Навигационных переменны
    private val navigationContent: NavigationContent = NavigationContent(supportFragmentManager)
    private val navigationDialogs: NavigationDialogs = NavigationDialogs()
    // Установка темы приложения
    private var isThemeDay: Boolean = true
    // Binding
    lateinit var binding: ActivityMainBinding
    // Bottom navigation menu
    private var isMain = false
    private var isFABButtonsGroupView = false
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

        // Подключение ViewPagerAdapter и TabLayout для запуска фрагментов
        binding.viewPager.adapter = ViewPagerAdapter(supportFragmentManager)
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        // Настройка TabLayout (установка на него картинок)
        binding.tabLayout.getTabAt(ConstantsController.DAY_PHOTO_FRAGMENT_INDEX)?.customView =
            layoutInflater.inflate(R.layout.tablayout_photo_of_day, null)
        binding.tabLayout.getTabAt(ConstantsController.SEARCH_WIKI_FRAGMENT_INDEX)?.customView =
            layoutInflater.inflate(R.layout.tablayout_search_in_wiki, null)
        binding.tabLayout.getTabAt(
            ConstantsController.SEARCH_NASA_ARCHIVE_FRAGMENT_INDEX)?.customView =
            layoutInflater.inflate(R.layout.tablayout_search_in_nasa_archive, null)

        // Установка настроек видимости элементов макета MainActivity
        hideAndShowFragmentsContainersAndDismissDialogs()

        //region МЕТОДЫ ДЛЯ НАСТРОЙКИ КНОПОК BOTTOM NAVIGATION MENU
        // Установка Bottom Navigation Menu
        setBottomAppBar()
        // Установка слушателя на длительное нажатие на нижнюю кнопку FAB
        binding.fabButtonsGroup.visibility = View.INVISIBLE
        binding.bottomNavigationMenu.bottomAppBarFab.setOnLongClickListener {
            if (isFABButtonsGroupView) {
                binding.fabButtonsGroup.visibility = View.INVISIBLE
                isFABButtonsGroupView = !isFABButtonsGroupView
            } else {
                binding.fabButtonsGroup.visibility = View.VISIBLE
                isFABButtonsGroupView = !isFABButtonsGroupView
            }
            true
        }
        // Установка слушателя на нажатие кнопки вызова фрагмента с картинкой дня
        binding.fabButtonsContainer.getViewById(R.id.fab_button_day_photo).setOnClickListener {
            binding.fabButtonsGroup.visibility = View.INVISIBLE
            hideAndShowFragmentsContainersAndDismissDialogs()
            binding.viewPager.currentItem = 0
        }
        // Установка слушателя на нажатие кнопки вызова фрагмента с поиском в Википедии
        binding.fabButtonsContainer.getViewById(R.id.fab_button_search_in_wiki)
            .setOnClickListener {
            binding.fabButtonsGroup.visibility = View.INVISIBLE
            hideAndShowFragmentsContainersAndDismissDialogs()
            binding.viewPager.currentItem = 1
        }
        // Установка слушателя на нажатие кнопки вызова фрагмента с поиском в архиве NASA
        binding.fabButtonsContainer.getViewById(R.id.fab_button_search_in_nasa_archive)
            .setOnClickListener {
            binding.fabButtonsGroup.visibility = View.INVISIBLE
                hideAndShowFragmentsContainersAndDismissDialogs()
            binding.viewPager.currentItem = 2
        }
        // Установка слушателя на нажатие кнопки вызова настроек приложения
        binding.fabButtonsContainer.getViewById(R.id.fab_button_settings).setOnClickListener {
            binding.fabButtonsGroup.visibility = View.INVISIBLE
            showSettingsFragment()
        }
        //endregion

        // Отображение содержимого макета
        setContentView(binding.root)

    }

    // Скрытие контейнера для фрамгента с установками приложения
    // и отображение элементов viewPager и tabLayout,
    // а также закрытие всех открытых диалоговых фрагментов
    private fun hideAndShowFragmentsContainersAndDismissDialogs() {
        binding.viewPager.visibility = View.VISIBLE
        binding.tabLayout.visibility = View.VISIBLE
        binding.activityFragmentsContainer.visibility = View.INVISIBLE
        navigationDialogs.closeDialogs()
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
                AppCompatActivity.MODE_PRIVATE)
        var sharedPreferencesEditor: SharedPreferences.Editor = sharedPreferences.edit()
        sharedPreferencesEditor.putBoolean(ConstantsUi.SHARED_PREFERENCES_THEME_KEY, isThemeDay)
        sharedPreferencesEditor.apply()
    }

    //region МЕТОДЫ ПОЛУЧЕНИЯ НАВИГАЦИИ
    override fun getNavigationDialogs(): NavigationDialogs {
        return navigationDialogs
    }
    override fun getNavigationContent(): NavigationContent {
        return navigationContent
    }
    //endregion

    //region УСТАНОВКА BOTTOM NAVIGATION MENU
    private fun setBottomAppBar() {
        this.setSupportActionBar(binding.bottomNavigationMenu.bottomAppBar)
//        setHasOptionsMenu(true)

        switchBottomAppBar(this)
        binding.bottomNavigationMenu.bottomAppBarFab.setOnClickListener {
            switchBottomAppBar(this)
        }
    }

    // Переключение режима нижней навигационной кнопки BottomAppBar
    // с центрального на крайнее левое положение и обратно
    private fun switchBottomAppBar(context: MainActivity) {
        if (isMain) {
            isMain = false
            binding.bottomNavigationMenu.bottomAppBar.navigationIcon = null
            binding.bottomNavigationMenu.bottomAppBar.fabAlignmentMode =
                BottomAppBar.FAB_ALIGNMENT_MODE_END
            binding.bottomNavigationMenu.bottomAppBarFab.setImageDrawable(
                ContextCompat.getDrawable(context, R.drawable.ic_back_fab)
            )
            binding.bottomNavigationMenu.bottomAppBar.replaceMenu(
                R.menu.bottom_menu_bottom_bar_other_screen)
        } else {
            isMain = true
            binding.bottomNavigationMenu.bottomAppBar.navigationIcon =
                ContextCompat.getDrawable(context, R.drawable.ic_hamburger_menu_bottom_bar)
            binding.bottomNavigationMenu.bottomAppBar.fabAlignmentMode =
                BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
            binding.bottomNavigationMenu.bottomAppBarFab.setImageDrawable(
                ContextCompat.getDrawable(context, R.drawable.ic_plus_fab)
            )
            binding.bottomNavigationMenu.bottomAppBar.replaceMenu(R.menu.bottom_menu_bottom_bar)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Отобразить справа внизу стартового меню
        menuInflater.inflate(R.menu.bottom_menu_bottom_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_bottom_bar_settings ->
                // Отображение фрагмента с настройками приложения
                showSettingsFragment()
            android.R.id.home -> {
                // Отображение списка основных содержательных разделов приложения
                navigationDialogs?.let {
                    it.showBottomNavigationDrawerDialogFragment(
                        this)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
    //endregion

    // Метод отображения фрагмента с настройками приложения
    fun showSettingsFragment() {
        navigationContent?.let{
            // Отобразить фрагмент с настройками приложения
            binding.viewPager.visibility = View.INVISIBLE
            binding.tabLayout.visibility = View.INVISIBLE
            binding.activityFragmentsContainer.visibility = View.VISIBLE
            it.showSettingsFragment(false)
        }
    }

    // Метод получения ViewPager
    fun getViewPager(): ViewPager {
        return binding.viewPager
    }
}