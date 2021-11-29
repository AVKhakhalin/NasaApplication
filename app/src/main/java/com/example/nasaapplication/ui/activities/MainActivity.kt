package com.example.nasaapplication.ui.activities

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.*
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.nasaapplication.FavoriteBaseApp.Companion.getFavoriteDAO
import com.example.nasaapplication.R
import com.example.nasaapplication.Constants
import com.example.nasaapplication.controller.navigation.contents.NavigationContent
import com.example.nasaapplication.controller.navigation.contents.NavigationContentGetter
import com.example.nasaapplication.controller.navigation.contents.ViewPagerAdapter
import com.example.nasaapplication.controller.navigation.dialogs.NavigationDialogs
import com.example.nasaapplication.controller.navigation.dialogs.NavigationDialogsGetter
import com.example.nasaapplication.controller.observers.UIObserversManager
import com.example.nasaapplication.databinding.ActivityMainBinding
import com.example.nasaapplication.repository.facadeuser.room.LocalRoomImpl
import com.example.nasaapplication.ui.utils.ThemeColor
import com.google.android.material.tabs.TabLayoutMediator
import java.util.*

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
    private var isMain: Boolean = false
    private var isFABButtonsGroupView: Boolean = false
    // Переменные для анимации фона
    private val durationAnimation: Long = 300
    private val transparientValue: Float = 1f
    private val notTransparientValue: Float = 0.2f
    // ViewPager2
    private val viewPagerAdapter: ViewPagerAdapter = ViewPagerAdapter(this)
    private var textTabLayouts: List<String> = listOf()
    private var touchableListTabLayout: ArrayList<View> = arrayListOf()
    // Цвета из аттрибутов темы
    private var themeColor: ThemeColor? = null
    // Menu
    private var bottomMenu: Menu? = null
    private val setBottomNavigationMenu: SetBottomNavigationMenu =
        SetBottomNavigationMenu(
            this, durationAnimation, transparientValue, notTransparientValue, themeColor)
    // Room
    private val localRoomImpl: LocalRoomImpl = LocalRoomImpl(getFavoriteDAO())
    // Создание обработчика событий нажатий на элементы UI
    private var uiObserversManager: UIObserversManager = UIObserversManager(
        this, localRoomImpl, navigationContent, navigationDialogs)
    //endregion

    override fun onPause() {
        // Обновление списка "Избранное" в базе данных перед закрытием приложения
        uiObserversManager.onPauseMainActivity()
        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Считывание системных настроек, применение темы к приложению
        readSettingsAndSetupApplication(savedInstanceState)
        
        // Считывание данных по списку "Избранное" из базы данных
        uiObserversManager.getFacadeFavoriteLogic()
            .addListFavoriteData(localRoomImpl.getAllFavorite())
        
        // Получение цветов из аттрибутов темы
        themeColor = ThemeColor(theme)
        themeColor?.let { it.initiateColors() }
        
        // Подключение Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        
        //region ПОДКЛЮЧЕНИЕ И НАСТРОЙКА VIEWPAGER2
        // Подключение ViewPagerAdapter и TabLayout для запуска фрагментов
        binding.viewPager.adapter = viewPagerAdapter
        // Настройка загрузки всех невидимых фрагментов
        binding.viewPager.setOffscreenPageLimit(viewPagerAdapter.getFragments().size)
        // Установка текста на закладки
        textTabLayouts = listOf(resources.getString(R.string.tablayout_photo_of_day_icon_text),
            resources.getString(R.string.tablayout_search_in_wiki_icon_text),
            resources.getString(R.string.tablayout_search_in_nasa_archive_text))
        TabLayoutMediator(binding.tabLayout, binding.viewPager, true, true) {
                tab, position -> tab.text = textTabLayouts[position]
        }.attach()
        // Получение списка View закладок TabLayout
        touchableListTabLayout = binding.tabLayout.touchables
        // Настройка TabLayout (установка на него картинок)
        binding.tabLayout.getTabAt(Constants.DAY_PHOTO_FRAGMENT_INDEX)?.customView =
            layoutInflater.inflate(R.layout.tablayout_photo_of_day, null)
        binding.tabLayout.getTabAt(Constants.SEARCH_WIKI_FRAGMENT_INDEX)?.customView =
            layoutInflater.inflate(R.layout.tablayout_search_in_wiki, null)
        binding.tabLayout.getTabAt(Constants.SEARCH_NASA_ARCHIVE_FRAGMENT_INDEX)?.customView =
            layoutInflater.inflate(R.layout.tablayout_search_in_nasa_archive, null)
        //endregion

        // Установка настроек видимости элементов макета MainActivity
        setBottomNavigationMenu.hideAndShowFragmentsContainersAndDismissDialogs()

        // Методы работы с Bottom Navigation Menu
        setBottomNavigationMenu.setMenu()

        // Отключение блокировки всех кнопок, кроме кнопок, появившихся из FAB
        uiObserversManager.setIsBlockingOtherFABButtons(false)

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
            getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, AppCompatActivity.MODE_PRIVATE)
        val sharedPreferencesEditor: SharedPreferences.Editor = sharedPreferences.edit()
        sharedPreferencesEditor.putBoolean(Constants.SHARED_PREFERENCES_THEME_KEY, isThemeDay)
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
    fun setBottomAppBar() {
        this.setSupportActionBar(binding.bottomNavigationMenu.bottomAppBar)

        setBottomNavigationMenu.switchBottomAppBar()
        binding.bottomNavigationMenu.bottomAppBarFab.setOnClickListener {
            if (navigationContent.getSettingsFragment() != null) {
                recreate()
            } else {
                setBottomNavigationMenu.switchBottomAppBar()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Отобразить справа внизу стартового меню
        menuInflater.inflate(R.menu.bottom_menu_bottom_bar, menu)
        bottomMenu = menu
        return true
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_bottom_bar_settings -> uiObserversManager.clickOnSettingButton()
            R.id.action_bottom_bar_open_favorite_list -> uiObserversManager.clickOnListHeartButton()
            R.id.action_bottom_bar_add_to_favorite -> uiObserversManager.clickOnHeartButton()
            android.R.id.home -> uiObserversManager.clickOnBurgerButton()
        }
        return super.onOptionsItemSelected(item)
    }
    //endregion

    // Метод получения ViewPager2
    fun getViewPager(): ViewPager2 {
        return binding.viewPager
    }

    // Считывание системных настроек, применение темы к приложению
    private fun readSettingsAndSetupApplication(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            val sharedPreferences: SharedPreferences =
                getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, MODE_PRIVATE)
            isThemeDay = sharedPreferences.getBoolean(
                Constants.SHARED_PREFERENCES_THEME_KEY, true)
            if (isThemeDay) {
                setTheme(R.style.Theme_NasaApplication_Day)
            } else {
                setTheme(R.style.Theme_NasaApplication_Night)
            }
        } else {
            // Применение тёмной темы при первом запуске приложения
            // на мобильных устройствах с версией Android 10+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                setTheme(R.style.Theme_NasaApplication_Night)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Установка начального значения isMain
        isMain = false
        setBottomAppBar()
    }

    // Получение нижнего меню для изменения вида иконки сердца
    fun getBottomMenu(): Menu? {
        return bottomMenu
    }

    // Получение viewPagerAdapter
    fun getViewPagerAdapter(): ViewPagerAdapter {
        return viewPagerAdapter
    }

    // Метод для отображения сообщения в виде Toast
    fun toast(string: String?) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).apply {
            setGravity(Gravity.CENTER_VERTICAL, 0, 250)
            show()
        }
    }

    // Получение класса с цветами темы
    fun getThemeColor(): ThemeColor? {
        return themeColor
    }
    
    //region Методы для класса SetBottomNavigationMenu
    // Установка isFABButtonsGroupView
    fun setIsFABButtonsGroupView(isFABButtonsGroupView: Boolean) {
        this.isFABButtonsGroupView = isFABButtonsGroupView
    }
    // Получение isFABButtonsGroupView
    fun getIsFABButtonsGroupView(): Boolean {
        return isFABButtonsGroupView
    }
    // Получение закладок
    fun getTouchableListTabLayout(): ArrayList<View> {
        return touchableListTabLayout
    }
    // Установка значения переменной isMain
    fun setIsMain(isMain: Boolean) {
        this.isMain = isMain
    }
    // Получение значения переменной isMain
    fun getIsMain(): Boolean {
        return isMain
    }
    fun getSetBottomNavigationMenu(): SetBottomNavigationMenu {
        return setBottomNavigationMenu
    }
    //endregion

    // Метод получения обработчика событий нажатий на элементы UI
    fun getUIObserversManager(): UIObserversManager {
        return uiObserversManager
    }
}