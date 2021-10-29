package com.example.nasaapplication.ui.activities

import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.*
import android.widget.FrameLayout
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
import com.google.android.material.tabs.TabLayout

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

        // Установка настроек видимости элементов макета MainActivity
        binding.viewPager.visibility = View.VISIBLE
        binding.tabLayout.visibility = View.VISIBLE
        binding.activityFragmentsContainer.visibility = View.INVISIBLE

        // Установка Bottom Navigation Menu
        setBottomAppBar()

        // Отображение содержимого макета
        setContentView(binding.root)
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

    override fun getNavigationDialogs(): NavigationDialogs {
        return navigationDialogs
    }

    override fun getNavigationContent(): NavigationContent {
        return navigationContent
    }

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
            R.id.action_bottom_bar_settings -> navigationContent?.let{
                // Отобразить фрагмент с настройками приложения
                this.findViewById<ViewPager>(R.id.view_pager).visibility =
                    View.INVISIBLE
                this.findViewById<TabLayout>(R.id.tab_layout).visibility =
                    View.INVISIBLE
                this.findViewById<FrameLayout>(R.id.activity_fragments_container)
                    .visibility = View.VISIBLE
                it.showSettingsFragment(false)
            }
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

    // Метод получения ViewPager
    fun getViewPager(): ViewPager {
        return binding.viewPager
    }
}