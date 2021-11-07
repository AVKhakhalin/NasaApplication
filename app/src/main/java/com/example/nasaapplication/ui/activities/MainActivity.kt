package com.example.nasaapplication.ui.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.*
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
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
import com.google.android.material.tabs.TabLayoutMediator
import java.lang.Thread.sleep
import java.util.ArrayList
import kotlin.math.round
import kotlin.math.sqrt


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
    // Признак блокировки кнопок во всем приложении, при появления меню из нижней FAB
    private var isBlockingOtherFABButtons: Boolean = false
    // Переменные для анимации фона
    private val durationAnimation: Long = 300
    private val transparientValue: Float = 1f
    private val notTransparientValue: Float = 0.2f
    // ViewPager2
    private val viewPagerAdapter: ViewPagerAdapter = ViewPagerAdapter(this)
    private var textTabLayouts: List<String> = listOf()
    private var touchableListTabLayot: ArrayList<View> = arrayListOf()
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Считывание системных настроек, применение темы к приложению
        readSettingsAndSetupApplication(savedInstanceState)

        // Подключение Binding
        binding = ActivityMainBinding.inflate(layoutInflater)

        //region ПОДКЛЮЧЕНИЕ И НАСТРОЙКА VIEWPAGER2
        // Подключение ViewPagerAdapter и TabLayout для запуска фрагментов
        binding.viewPager.adapter = viewPagerAdapter
        textTabLayouts = listOf(resources.getString(R.string.tablayout_photo_of_day_icon_text),
            resources.getString(R.string.tablayout_search_in_wiki_icon_text),
            resources.getString(R.string.tablayout_search_in_nasa_archive_text))
        TabLayoutMediator(binding.tabLayout, binding.viewPager, true, true) {tab, position ->
            tab.text = "${textTabLayouts[position]}"
        }.attach()
        // Получение списка View закладок TabLayout
        touchableListTabLayot = binding.tabLayout.touchables
        // Настройка TabLayout (установка на него картинок)
        binding.tabLayout.getTabAt(ConstantsController.DAY_PHOTO_FRAGMENT_INDEX)?.customView =
            layoutInflater.inflate(R.layout.tablayout_photo_of_day, null)
        binding.tabLayout.getTabAt(ConstantsController.SEARCH_WIKI_FRAGMENT_INDEX)?.customView =
            layoutInflater.inflate(R.layout.tablayout_search_in_wiki, null)
        binding.tabLayout.getTabAt(
            ConstantsController.SEARCH_NASA_ARCHIVE_FRAGMENT_INDEX)?.customView =
            layoutInflater.inflate(R.layout.tablayout_search_in_nasa_archive, null)
        //endregion

        // Установка настроек видимости элементов макета MainActivity
        hideAndShowFragmentsContainersAndDismissDialogs()

        // Методы работы с Bottom Navigation Menu
        setBottomNavigationMenu()

        // Отключение блокировки всех кнопок, кроме кнопок, появившихся из FAB
        isBlockingOtherFABButtons = false

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
            getSharedPreferences(ConstantsUi.SHARED_PREFERENCES_KEY, AppCompatActivity.MODE_PRIVATE)
        val sharedPreferencesEditor: SharedPreferences.Editor = sharedPreferences.edit()
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

        switchBottomAppBar(this)
        binding.bottomNavigationMenu.bottomAppBarFab.setOnClickListener {
            if (navigationContent.getSettingsFragment() != null) {
                recreate()
            } else {
                switchBottomAppBar(this)
            }
        }
    }
    // Переключение режима нижней навигационной кнопки BottomAppBar
    // с центрального на крайнее левое положение и обратно
    fun switchBottomAppBar(context: MainActivity) {
        // Отключение блокировки всех кнопок, кроме кнопок, появившихся из FAB
        isBlockingOtherFABButtons = false
        // Установка анимационного просветления фона
        setHideShowBackgroundAnimation(transparientValue, durationAnimation, true)
        // Отображение навигационного меню View Pager
        binding.tabLayout.visibility = View.VISIBLE
        // Анимация вращения картинки на нижней кнопке FAB
        ObjectAnimator.ofFloat(binding.bottomNavigationMenu.bottomAppBarFab,
            "rotation", 0f, -360f).start()

        if (isMain) {
            // Изменение нижего меню, выходящего из FAB
            if (isFABButtonsGroupView) {
                binding.fabButtonsGroup.visibility = View.INVISIBLE
                isFABButtonsGroupView = !isFABButtonsGroupView
            }
            // Изменение нижней кнопки FAB
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
            // Изменение нижего меню, выходящего из FAB
            if (isFABButtonsGroupView) {
                binding.fabButtonsGroup.visibility = View.INVISIBLE
                isFABButtonsGroupView = !isFABButtonsGroupView
            }
            // Изменение нижней кнопки FAB
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
                if (!isBlockingOtherFABButtons) {
                    // Отображение фрагмента с настройками приложения
                    showSettingsFragment()
                }
            android.R.id.home -> {
                if (!isBlockingOtherFABButtons) {
                    // Отображение списка основных содержательных разделов приложения
                    navigationDialogs?.let {
                        it.showBottomNavigationDrawerDialogFragment(
                            this
                        )
                    }
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

    // Метод получения ViewPager2
    fun getViewPager(): ViewPager2 {
        return binding.viewPager
    }

    //region МЕТОДЫ ДЛЯ НАСТРОЙКИ КНОПОК BOTTOM NAVIGATION MENU
    private fun setBottomNavigationMenu() {
        // Установка Bottom Navigation Menu
        setBottomAppBar()
        // Установка слушателя на длительное нажатие на нижнюю кнопку FAB
        binding.fabButtonsGroup.visibility = View.INVISIBLE
        binding.bottomNavigationMenu.bottomAppBarFab.setOnLongClickListener {
            if (isFABButtonsGroupView) {
                // Установка признака блокировки кнопок во всем приложении,
                // при появления меню из нижней FAB
                isBlockingOtherFABButtons = false
                // Разблокировка перелистывания во View Pager 2
                binding.viewPager.setUserInputEnabled(true)
                // Разблокировка кликов по закладкам во View Pager 2
                touchableListTabLayot.forEach { it.isEnabled = true }

                // Скрытие группы кнопок от меню кнопки FAB
                binding.fabButtonsGroup.visibility = View.INVISIBLE
                isFABButtonsGroupView = !isFABButtonsGroupView
            } else {
                // Установка анимационного затенения фона
                setHideShowBackgroundAnimation(
                    notTransparientValue, durationAnimation, false)
                // Установка признака блокировки кнопок во всем приложении,
                // при появления меню из нижней FAB
                isBlockingOtherFABButtons = true
                // Блокировка перелистывания во View Pager 2
                binding.viewPager.setUserInputEnabled(false)
                // Блокировка кликов по закладкам во View Pager 2
                touchableListTabLayot.forEach { it.isEnabled = false }

                // Анимация появления кнопок меню из нижней кнопки FAB
                if (isMain) {
                    val constraintLayout =
                        findViewById<ConstraintLayout>(R.id.fab_buttons_container)
                    val constraintSet = ConstraintSet()
                    constraintSet.clone(constraintLayout)
                    constraintSet.constrainCircle(
                        R.id.fab_button_day_photo,
                        R.id.bottom_fab_maket,
                        0,
                        285f
                    )
                    constraintSet.constrainCircle(
                        R.id.fab_button_search_in_wiki,
                        R.id.bottom_fab_maket,
                        0,
                        330f
                    )
                    constraintSet.constrainCircle(
                        R.id.fab_button_search_in_nasa_archive,
                        R.id.bottom_fab_maket,
                        0,
                        20f
                    )
                    constraintSet.constrainCircle(
                        R.id.fab_button_settings,
                        R.id.bottom_fab_maket,
                        0,
                        73f
                    )
                    constraintSet.applyTo(constraintLayout)
                    binding.fabButtonsGroup.visibility = View.VISIBLE
                    isFABButtonsGroupView = !isFABButtonsGroupView
                    Thread {
                        // Исходные параметры
                        val numberFrames: Int = 30 // numberFrames - ОБЩЕЕ КОЛИЧЕСТВО ШАГОВ (С УЧЁТОМ РЕЛАКСАЦИИ)
                        val deltaTime: Long = 8L // deltaTime - ВАЖНЫЙ ПАРАМТЕР - ДЛИТЕЛЬНОСТЬ ОДНОГО ШАГА
                        val deltaRadius: Int = 8 // deltaRadius - ВАЖНЫЙ ПАРАМЕТР, ОТВЕЧАЕТ ЗА УВЕЛИЧЕНИЕ РАДИУСА НА ОДНОМ ШАГЕ
                        val handler = Handler(Looper.getMainLooper())

                        // Создание релаксации при прохождении через конечную точку
                        val a: Double = 1.0 // a - В ПРИНЦИПЕ, МОЖНО ЭТОТ ПАРАМЕТР ИСКЛЮЧИТЬ, ПРИРАВНЯВ ЕГО К ЕДИНИЦЕ. ОН ОТВЕЧАЕТ В УРАВНЕНИИ y = a * x2 за широту нашей параболы
                        val k: Double = 0.3 // k - ОЧЕНЬ ВАЖНЫЙ ПАРАМЕТР (0 <= k <= 1). ОТВЕЧАЕТ ЗА ТО, КАК ДАЛЕКО ПРОЙДЕТ ОБЪЕКТ, ПО СРАВНЕНИЮ С ПРОЙДЕННЫМ ДО КОНЕЧНОЙ ТОЧКИ ПУТИ. 1 - ПУТЬ ВОЗВРАТА БУДЕТ РАВЕН ПУТИ ДО КОНЕЧНОЙ ТОКИ
                        var y: Double = (numberFrames * deltaRadius).toDouble() // Начальная точка движения для определения maxX и minX. После их определения y корректируется
                        val maxX: Double = sqrt(y / a) // Расстояние до конечной точки
                        val minX: Double = sqrt(y * k / a) // Длина траектории релаксации
                        val deltaX: Double = (maxX + minX) / numberFrames // Смещение на одном шаге
                        y *= (1 + k) // Учёт длины траектории релаксации (нужно увеличить начальный y, чтобы мы в результате удалились только на maxX от начальной точки

                        repeat(numberFrames) {
                            sleep(deltaTime)
                            handler.post {
                                val constraintLayout =
                                    findViewById<ConstraintLayout>(R.id.fab_buttons_container)
                                val constraintSet = ConstraintSet()
                                constraintSet.clone(constraintLayout)
                                constraintSet.constrainCircle(
                                    R.id.fab_button_day_photo,
                                    R.id.bottom_fab_maket,
                                    round(y - a * (maxX - deltaX * it) *
                                            (maxX - deltaX * it)).toInt(),
                                    285f
                                )
                                constraintSet.constrainCircle(
                                    R.id.fab_button_search_in_wiki,
                                    R.id.bottom_fab_maket,
                                    round(y - a * (maxX - deltaX * it) *
                                            (maxX - deltaX * it)).toInt(),
                                    330f
                                )
                                constraintSet.constrainCircle(
                                    R.id.fab_button_search_in_nasa_archive,
                                    R.id.bottom_fab_maket,
                                    round(y - a * (maxX - deltaX * it) *
                                            (maxX - deltaX * it)).toInt(),
                                    20f
                                )
                                constraintSet.constrainCircle(
                                    R.id.fab_button_settings,
                                    R.id.bottom_fab_maket,
                                    round(y - a * (maxX - deltaX * it) *
                                            (maxX - deltaX * it)).toInt(),
                                    73f
                                )
                                constraintSet.applyTo(constraintLayout)
                            }
                        }
                    }.start()
                } else {
                    val constraintLayout =
                        findViewById<ConstraintLayout>(R.id.fab_buttons_container)
                    val constraintSet = ConstraintSet()
                    constraintSet.clone(constraintLayout)
                    constraintSet.constrainCircle(
                        R.id.fab_button_day_photo,
                        R.id.bottom_fab_maket_right,
                        0,
                        285f
                    )
                    constraintSet.constrainCircle(
                        R.id.fab_button_search_in_wiki,
                        R.id.bottom_fab_maket_right,
                        0,
                        310f
                    )
                    constraintSet.constrainCircle(
                        R.id.fab_button_search_in_nasa_archive,
                        R.id.bottom_fab_maket_right,
                        0,
                        345f
                    )
                    constraintSet.constrainCircle(
                        R.id.fab_button_settings,
                        R.id.bottom_fab_maket_right,
                        0,
                        15f
                    )
                    constraintSet.applyTo(constraintLayout)
                    binding.fabButtonsGroup.visibility = View.VISIBLE
                    isFABButtonsGroupView = !isFABButtonsGroupView
                    Thread {
                        // Исходные параметры
                        val numberFrames: Int = 30 // numberFrames - ОБЩЕЕ КОЛИЧЕСТВО ШАГОВ (С УЧЁТОМ РЕЛАКСАЦИИ)
                        val deltaTime: Long = 8L // deltaTime - ВАЖНЫЙ ПАРАМТЕР - ДЛИТЕЛЬНОСТЬ ОДНОГО ШАГА
                        val deltaRadius: Int = 9 // deltaRadius - ВАЖНЫЙ ПАРАМЕТР, ОТВЕЧАЕТ ЗА УВЕЛИЧЕНИЕ РАДИУСА НА ОДНОМ ШАГЕ
                        val handler = Handler(Looper.getMainLooper())

                        // Создание релаксации при прохождении через конечную точку
                        val a: Double = 1.0 // a - В ПРИНЦИПЕ, МОЖНО ЭТОТ ПАРАМЕТР ИСКЛЮЧИТЬ, ПРИРАВНЯВ ЕГО К ЕДИНИЦЕ. ОН ОТВЕЧАЕТ В УРАВНЕНИИ y = a * x2 за широту нашей параболы
                        val k: Double = 0.3 // k - ОЧЕНЬ ВАЖНЫЙ ПАРАМЕТР (0 <= k <= 1). ОТВЕЧАЕТ ЗА ТО, КАК ДАЛЕКО ПРОЙДЕТ ОБЪЕКТ, ПО СРАВНЕНИЮ С ПРОЙДЕННЫМ ДО КОНЕЧНОЙ ТОЧКИ ПУТИ. 1 - ПУТЬ ВОЗВРАТА БУДЕТ РАВЕН ПУТИ ДО КОНЕЧНОЙ ТОКИ
                        var y: Double = (numberFrames * deltaRadius).toDouble() // Начальная точка движения для определения maxX и minX. После их определения y корректируется
                        val maxX: Double = sqrt(y / a) // Расстояние до конечной точки
                        val minX: Double = sqrt(y * k / a) // Длина траектории релаксации
                        val deltaX: Double = (maxX + minX) / numberFrames // Смещение на одном шаге
                        y *= (1 + k) // Учёт длины траектории релаксации (нужно увеличить начальный y, чтобы мы в результате удалились только на maxX от начальной точки

                        repeat(numberFrames) {
                            sleep(deltaTime)
                            handler.post {
                                val constraintLayout =
                                    findViewById<ConstraintLayout>(R.id.fab_buttons_container)
                                val constraintSet = ConstraintSet()
                                constraintSet.clone(constraintLayout)
                                constraintSet.constrainCircle(
                                    R.id.fab_button_day_photo,
                                    R.id.bottom_fab_maket_right,
                                    round(y - a * (maxX - deltaX * it) *
                                            (maxX - deltaX * it)).toInt(),
                                    285f
                                )
                                constraintSet.constrainCircle(
                                    R.id.fab_button_search_in_wiki,
                                    R.id.bottom_fab_maket_right,
                                    round(y - a * (maxX - deltaX * it) *
                                            (maxX - deltaX * it)).toInt(),
                                    310f
                                )
                                constraintSet.constrainCircle(
                                    R.id.fab_button_search_in_nasa_archive,
                                    R.id.bottom_fab_maket_right,
                                    round(y - a * (maxX - deltaX * it) *
                                            (maxX - deltaX * it)).toInt(),
                                    345f
                                )
                                constraintSet.constrainCircle(
                                    R.id.fab_button_settings,
                                    R.id.bottom_fab_maket_right,
                                    round(y - a * (maxX - deltaX * it) *
                                            (maxX - deltaX * it)).toInt(),
                                    15f
                                )
                                constraintSet.applyTo(constraintLayout)
                            }
                        }
                    }.start()
                }
            }
            true
        }
        // Установка слушателя на нажатие кнопки вызова фрагмента с картинкой дня
        binding.fabButtonsContainer.getViewById(R.id.fab_button_day_photo).setOnClickListener {
            binding.fabButtonsGroup.visibility = View.INVISIBLE
            hideAndShowFragmentsContainersAndDismissDialogs()
            isFABButtonsGroupView = false
            binding.viewPager.currentItem = 0
            // Проба анимации кнопки
//            TransitionManager.beginDelayedTransition(binding.fabButtonsContainer, Slide(Gravity.END))
//            binding.fabButtonDayPhoto.visibility = View.GONE
            isBlockingOtherFABButtons = false
            // Установка анимационного просветления фона
            setHideShowBackgroundAnimation(
                transparientValue, durationAnimation, true)
            // Разблокировка перелистывания во View Pager 2
            binding.viewPager.setUserInputEnabled(true)
            // Разблокировка кликов по закладкам во View Pager 2
            touchableListTabLayot.forEach { it.isEnabled = true }
        }
        // Установка слушателя на нажатие кнопки вызова фрагмента с поиском в Википедии
        binding.fabButtonsContainer.getViewById(R.id.fab_button_search_in_wiki)
            .setOnClickListener {
                binding.fabButtonsGroup.visibility = View.INVISIBLE
                hideAndShowFragmentsContainersAndDismissDialogs()
                isFABButtonsGroupView = false
                binding.viewPager.currentItem = 1
                isBlockingOtherFABButtons = false
                // Установка анимационного просветления фона
                setHideShowBackgroundAnimation(
                    transparientValue, durationAnimation, true)
                // Разблокировка перелистывания во View Pager 2
                binding.viewPager.setUserInputEnabled(true)
                // Разблокировка кликов по закладкам во View Pager 2
                touchableListTabLayot.forEach { it.isEnabled = true }
            }
        // Установка слушателя на нажатие кнопки вызова фрагмента с поиском в архиве NASA
        binding.fabButtonsContainer.getViewById(R.id.fab_button_search_in_nasa_archive)
            .setOnClickListener {
                binding.fabButtonsGroup.visibility = View.INVISIBLE
                hideAndShowFragmentsContainersAndDismissDialogs()
                isFABButtonsGroupView = false
                binding.viewPager.currentItem = 2
                isBlockingOtherFABButtons = false
                // Установка анимационного просветления фона
                setHideShowBackgroundAnimation(
                    transparientValue, durationAnimation, true)
                // Разблокировка перелистывания во View Pager 2
                binding.viewPager.setUserInputEnabled(true)
                // Разблокировка кликов по закладкам во View Pager 2
                touchableListTabLayot.forEach { it.isEnabled = true }
            }
        // Установка слушателя на нажатие кнопки вызова настроек приложения
        binding.fabButtonsContainer.getViewById(R.id.fab_button_settings).setOnClickListener {
            binding.fabButtonsGroup.visibility = View.INVISIBLE
            isFABButtonsGroupView = false
            isBlockingOtherFABButtons = false
            showSettingsFragment()
            // Установка анимационного просветления фона
            setHideShowBackgroundAnimation(
                transparientValue, durationAnimation, true)
            // Разблокировка перелистывания во View Pager 2
            binding.viewPager.setUserInputEnabled(true)
            // Разблокировка кликов по закладкам во View Pager 2
            touchableListTabLayot.forEach { it.isEnabled = true }
        }
    }
    //endregion

    // Считывание системных настроек, применение темы к приложению
    private fun readSettingsAndSetupApplication(savedInstanceState: Bundle?) {
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
    }

    // Установка значения переменной isMain
    fun setSsMain(isMain: Boolean) {
        this.isMain = isMain
    }

    override fun onResume() {
        super.onResume()
        // Установка начального значения isMain
        isMain = false
        setBottomAppBar()
    }

    // Получение признака блокировки всех кнопок, кроме появившихся из контекстного меню
    fun getIsBlockingOtherFABButtons(): Boolean {
        return isBlockingOtherFABButtons
    }

    // Установка анимационного затенения/просветления фона
    private fun setHideShowBackgroundAnimation (
        alpha: Float, duration: Long, isClickable: Boolean) {
        binding.transparentBackground.animate()
            .alpha(alpha)
            .setDuration(duration)
            .setListener(object: AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    binding.transparentBackground.isClickable = isClickable
                }
            })
    }
}