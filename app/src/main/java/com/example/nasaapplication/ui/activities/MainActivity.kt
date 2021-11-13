package com.example.nasaapplication.ui.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.*
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
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
import com.example.nasaapplication.domain.logic.Favorite
import com.example.nasaapplication.domain.logic.FavoriteLogic
import com.example.nasaapplication.ui.ConstantsUi
import com.example.nasaapplication.ui.fragments.contents.DayPhotoFragment
import com.example.nasaapplication.ui.fragments.contents.SearchNASAArchiveFragment
import com.example.nasaapplication.ui.fragments.contents.SearchWikiFragment
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.tabs.TabLayoutMediator
import java.lang.Thread.sleep
import java.util.*
import kotlin.math.round
import kotlin.math.sqrt

class MainActivity: AppCompatActivity(), NavigationDialogsGetter, NavigationContentGetter {
    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
    // Навигационных переменны
    private val navigationContent: NavigationContent =
        NavigationContent(supportFragmentManager, this)
    private val navigationDialogs: NavigationDialogs = NavigationDialogs()
    // Установка темы приложения
    private var isThemeDay: Boolean = true
    // Binding
    lateinit var binding: ActivityMainBinding
    // Bottom navigation menu
    private var isMain: Boolean = false
    private var isFABButtonsGroupView: Boolean = false
    // Признак блокировки кнопок во всем приложении, при появлении меню из нижней FAB
    private var isBlockingOtherFABButtons: Boolean = false
    // Переменные для анимации фона
    private val durationAnimation: Long = 300
    private val transparientValue: Float = 1f
    private val notTransparientValue: Float = 0.2f
    // ViewPager2
    private val viewPagerAdapter: ViewPagerAdapter =
        ViewPagerAdapter(this, this)
    private var textTabLayouts: List<String> = listOf()
    private var touchableListTabLayot: ArrayList<View> = arrayListOf()
    // Menu
    private var bottomMenu: Menu? = null
    private var isFavorite: Boolean = false
    // Данные для сохранения в "Избранное"
    private var newFavorite: Favorite = Favorite()
    private var favoriteListData: FavoriteLogic = FavoriteLogic()
    // Цвета из аттрибутов темы
    private val colorSecondaryTypedValue: TypedValue = TypedValue()
    private val colorTypedValue: TypedValue = TypedValue()
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Считывание системных настроек, применение темы к приложению
        readSettingsAndSetupApplication(savedInstanceState)

        // Установка цветов из аттрибутов темы
        theme.resolveAttribute(R.attr.colorSecondary, colorSecondaryTypedValue, true)
        theme.resolveAttribute(R.attr.color, colorTypedValue, true)

        // Подключение Binding
        binding = ActivityMainBinding.inflate(layoutInflater)

        //region ПОДКЛЮЧЕНИЕ И НАСТРОЙКА VIEWPAGER2
        // Подключение ViewPagerAdapter и TabLayout для запуска фрагментов
        binding.viewPager.adapter = viewPagerAdapter
        textTabLayouts = listOf(resources.getString(R.string.tablayout_photo_of_day_icon_text),
            resources.getString(R.string.tablayout_search_in_wiki_icon_text),
            resources.getString(R.string.tablayout_search_in_nasa_archive_text))
        TabLayoutMediator(binding.tabLayout, binding.viewPager, true, true)
        {tab, position -> tab.text = textTabLayouts[position] }.attach()
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
        binding.transparentBackground.visibility = View.VISIBLE
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
    // с центрального на крайнее правое положение и обратно
    fun switchBottomAppBar(context: MainActivity) {
        // Отключение блокировки всех кнопок, кроме кнопок, появившихся из FAB
        isBlockingOtherFABButtons = false
        // Установка анимационного просветления фона
        setHideShowBackgroundAnimation(transparientValue, durationAnimation, true)
        // Отображение навигационного меню View Pager
        binding.tabLayout.visibility = View.VISIBLE
        // Анимация вращения картинки на нижней кнопке FAB
        ObjectAnimator.ofFloat(binding.bottomNavigationMenu.bottomAppBarFab,
            "rotation", 0f, ConstantsUi.ANGLE_TO_ROTATE_BOTTOM_FAB).start()

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

            //region НАСТРОЙКИ ПОИСКОВОГО ПОЛЯ
            val searchViewActionView = binding.bottomNavigationMenu.bottomAppBar.menu
                .findItem(R.id.action_bottom_bar_search_request_form).actionView
            val searchView = searchViewActionView as SearchView
            // Событие установки поискового запроса
            searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    // Отображение полученного поискового запроса
                    setFilterWord(query)
                    navigationContent.getFavoriteRecyclerListFragment()?.let {
                        it.getAdapter()?.let { adapter ->
                            adapter.setFavoriteData(getFavoriteDataList())
                            adapter.notifyDataSetChanged()
                        }
                    }
                    return false
                }
                // Отслеживание появления каждого символа
                override fun onQueryTextChange(newText: String): Boolean {
                    // Отображение полученного поискового запроса
                    setFilterWord(newText)
                    navigationContent.getFavoriteRecyclerListFragment()?.let {
                        it.getAdapter()?.let { adapter ->
                            adapter.setFavoriteData(getFavoriteDataList())
                            adapter.notifyDataSetChanged()
                        }
                    }
                    return false
                }
            })
            // Событие на закрытие поискового окна (обнуление фильтра)
            searchView.setOnCloseListener {
                // Отображение полученного поискового запроса
                setFilterWord("")
                navigationContent.getFavoriteRecyclerListFragment()?.let {
                    it.getAdapter()?.let {adapter ->
                        adapter.setFavoriteData(getFavoriteDataList())
                        adapter.notifyDataSetChanged()
                    }
                }
                true
            }
            // Получение поискового поля для ввода и редактирования текста поискового
            val searchedEditText =
                searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
            // Установка цветов фона и текста для поискового поля
            searchedEditText.setBackgroundResource(R.drawable.search_view_shape)
            searchedEditText.setTextColor(colorTypedValue.data)
            searchedEditText.setHintTextColor(colorTypedValue.data)
            // Установка размера поискового текста
            searchedEditText.setTextSize(ConstantsUi.SEARCH_FIELD_TEXT_SIZE)
            // Установка значка поиска внутри editText (без исчезновения)
//            editText.setCompoundDrawablesWithIntrinsicBounds(
//                android.R.drawable.ic_menu_search,0,0,0)
            //endregion
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
            // Изменение вида иконки сердца
            changeHeartIconState(this, false, false)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Отобразить справа внизу стартового меню
        menuInflater.inflate(R.menu.bottom_menu_bottom_bar, menu)
        bottomMenu = menu
        return true
//        return super.onCreateOptionsMenu(menu)
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_bottom_bar_settings ->
                if (!isBlockingOtherFABButtons) {
                    // Отображение фрагмента с настройками приложения
                    showSettingsFragment()
                }
            R.id.action_bottom_bar_open_favorite_list ->
                if (!isBlockingOtherFABButtons) {
                    // Очистка текущей информации для добавления в список "Избранное"
                    setListFavoriteEmptyData()
                    // Отображение фрагмента со списком "Избранное"
                    showFavoriteRecyclerListFragment()
                }
            R.id.action_bottom_bar_add_to_favorite ->
                if ((!isBlockingOtherFABButtons) && (newFavorite.getTitle().isNotEmpty())) {
                    // Добавление понравившегося содержимого в список "Избранное"
                    val indexSimilarData: Int = favoriteListData.addFavoriteData(newFavorite)
                    if (indexSimilarData == -1) {
                        // Изменение вида иконки сердца
                        changeHeartIconState(this, true, false)
                        // Уведомление пользователя о добавлении новой записи в список "Избранное"
                        Toast.makeText(this, "${
                            resources.getString(R.string.info_added_item_in_favorite_list)}:\n\"${
                                newFavorite.getTitle()}\"", Toast.LENGTH_LONG
                        ).show()
                    } else {
                        // Удаление понравившегося содержимого из списка "Избранное"
                        favoriteListData.removeFavoriteData(indexSimilarData)
                        // Изменение вида иконки сердца
                        changeHeartIconState(this, false, true)
                        // Уведомление пользователя о добавлении новой записи в список "Избранное"
                        Toast.makeText(this, "${resources.getString(
                            R.string.info_deleted_item_from_favorite_list)}\n\"${
                            newFavorite.getTitle()}\"", Toast.LENGTH_LONG).show()
                    }
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
            binding.activityFragmentsContainer.visibility = View.VISIBLE
            binding.transparentBackground.visibility = View.INVISIBLE
            it.showSettingsFragment(false)
        }
    }

    // Метод отображения фрагмента со списком "Избранное"
    fun showFavoriteRecyclerListFragment() {
        navigationContent?.let{
            binding.activityFragmentsContainer.visibility = View.VISIBLE
            binding.transparentBackground.visibility = View.INVISIBLE
            it.showFavoriteRecyclerListFragment(false)
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
                // Установка анимационного просветления фона
                setHideShowBackgroundAnimation(
                    transparientValue, durationAnimation, false)
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
            binding.viewPager.currentItem = ConstantsController.DAY_PHOTO_FRAGMENT_INDEX
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
            // Начальная настройка фрагмента "Картинка дня"
            (getViewPagerAdapter().getFragments()[ConstantsController.DAY_PHOTO_FRAGMENT_INDEX]
                    as DayPhotoFragment).initialSettingFragment()
        }
        // Установка слушателя на нажатие кнопки вызова фрагмента с поиском в Википедии
        binding.fabButtonsContainer.getViewById(R.id.fab_button_search_in_wiki)
            .setOnClickListener {
                binding.fabButtonsGroup.visibility = View.INVISIBLE
                hideAndShowFragmentsContainersAndDismissDialogs()
                isFABButtonsGroupView = false
                binding.viewPager.currentItem = ConstantsController.SEARCH_WIKI_FRAGMENT_INDEX
                isBlockingOtherFABButtons = false
                // Установка анимационного просветления фона
                setHideShowBackgroundAnimation(
                    transparientValue, durationAnimation, true)
                // Разблокировка перелистывания во View Pager 2
                binding.viewPager.setUserInputEnabled(true)
                // Разблокировка кликов по закладкам во View Pager 2
                touchableListTabLayot.forEach { it.isEnabled = true }
                // Начальная настройка фрагмента "Поиск в Википедии"
                (getViewPagerAdapter()
                    .getFragments()[ConstantsController.SEARCH_WIKI_FRAGMENT_INDEX]
                        as SearchWikiFragment).initialSettingFragment()
            }
        // Установка слушателя на нажатие кнопки вызова фрагмента с поиском в архиве NASA
        binding.fabButtonsContainer.getViewById(R.id.fab_button_search_in_nasa_archive)
            .setOnClickListener {
                binding.fabButtonsGroup.visibility = View.INVISIBLE
                hideAndShowFragmentsContainersAndDismissDialogs()
                isFABButtonsGroupView = false
                binding.viewPager.currentItem =
                    ConstantsController.SEARCH_NASA_ARCHIVE_FRAGMENT_INDEX
                isBlockingOtherFABButtons = false
                // Установка анимационного просветления фона
                setHideShowBackgroundAnimation(
                    transparientValue, durationAnimation, true)
                // Разблокировка перелистывания во View Pager 2
                binding.viewPager.setUserInputEnabled(true)
                // Разблокировка кликов по закладкам во View Pager 2
                touchableListTabLayot.forEach { it.isEnabled = true }
                // Начальная настройка фрагмента "Поиск в архиве NASA"
                (getViewPagerAdapter()
                    .getFragments()[ConstantsController.SEARCH_NASA_ARCHIVE_FRAGMENT_INDEX]
                        as SearchNASAArchiveFragment).initialSettingFragment()
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
            // Применение тёмной темы при первом запуске приложения
            // на мобильных устройствах с версией Android 10+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                setTheme(R.style.Theme_NasaApplication_Night)
            }
        }
    }

    // Установка значения переменной isMain
    fun setIsMain(isMain: Boolean) {
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

    //region МЕТОДЫ ДЛЯ ПОЛУЧЕНИЯ ДАННЫХ ДЛЯ СПИСКА "ИЗБРАННОЕ"
    fun setListFavoriteDataTypeSource(newTypeSource: Int) {
        newFavorite.setTypeSource(newTypeSource)
    }
    fun setListFavoriteDataPriority(newPriority: Int) {
        newFavorite.setPriority(newPriority)
    }
    fun setListFavoriteDataLinkSource(newLinkSource: String) {
        newFavorite.setLinkSource(newLinkSource)
    }
    fun setListFavoriteDataTitle(newTitle: String) {
        newFavorite.setTitle(newTitle)
    }
    fun setListFavoriteDataDescription(newDescription: String) {
        newFavorite.setDescription(newDescription)
    }
    fun setListFavoriteDataSearchRequest(newSearchRequest: String) {
        newFavorite.setSearchRequest(newSearchRequest)
    }
    fun setListFavoriteDataLinkImage(newLinkImage: String) {
        newFavorite.setLinkImage(newLinkImage)
    }
    fun setListFavoriteEmptyData() {
        newFavorite = Favorite()
    }
    //endregion

    //region МЕТОДЫ ДЛЯ ИЗМЕНЕНИЯ ВИДА ИКОНКИ СЕРДЦА
    // Изменение вида иконки сердца
    fun changeHeartIconState(mainActivity: MainActivity, forceOn: Boolean, forceOff: Boolean) {
        if (forceOn) isFavorite = true
        if (forceOff) isFavorite = false
        mainActivity.getBottomMenu()?.let {
            if (it.size() > 0) {
                if (isFavorite) {
                    it.getItem(ConstantsUi.INDEX_ADD_FAVORITE_MENU_ITEM)
                        .setIcon(R.drawable.ic_favourite_on)
                } else {
                    it.getItem(ConstantsUi.INDEX_ADD_FAVORITE_MENU_ITEM)
                        .setIcon(R.drawable.ic_favourite)
                }
//                isFavorite = !isFavorite
            }
        }
    }
    private fun getBottomMenu(): Menu? {
        return bottomMenu
    }
    fun getIsFavorite(): Boolean {
        return isFavorite
    }
    //endregion

    //region МЕТОДЫ ПОЛУЧЕНИЯ ЦВЕТОВ ИЗ АТТРИБУТОВ ТЕМЫ
    fun getColorSecondary(): TypedValue {
        return colorSecondaryTypedValue
    }
    fun getColor(): TypedValue {
        return colorTypedValue
    }
    //endregion

    // Получение viewPagerAdapter
    fun getViewPagerAdapter(): ViewPagerAdapter {
        return viewPagerAdapter
    }

    //region МЕТОДЫ ФАСАДА ЛОГИКИ ПРОЕКТА (РАБОТА С ЛОГИКОЙ, КЛАССОМ FavoriteLogic)
    // Получение списка избранных данных
    fun getFavoriteDataList(): MutableList<Favorite> {
        return favoriteListData.getDatesList()
    }
    // Проверка на то, что новые данные уже есть в списке "Избранное"
    fun checkSimilarFavoriteData(): Boolean {
        Log.d("mylogs",
            "\n\nMainActivity:\n${newFavorite.getLinkImage()}" +
                    "\n${newFavorite.getLinkSource()}" +
                    "\n${newFavorite.getSearchRequest()}" +
                    "\n${newFavorite.getTypeSource()}" +
                    "\n${newFavorite.getDescription()}" +
                    "\n${newFavorite.getTitle()}\n")
        return favoriteListData.checkSimilarFavoriteData(newFavorite)
    }
    // Установка фильтра для выбора нужной информации из списка "Избранное"
    private fun setFilterWord(newFilterWord: String) {
        favoriteListData.setFilterWord(newFilterWord)
    }
    // Удаление данных в списке FullDates
    fun removeFavoriteDataByCorrectedData(indexRemovedFavoriteCorrectedData: Int) {
        favoriteListData.removeFavoriteDataByCorrectedData(indexRemovedFavoriteCorrectedData)
    }
    // Удаление и добавление данных в списке FullDates
    fun removeAndAddFavoriteDataByCorrectedData(indexRemovedFavoriteCorrectedData: Int,
                                                indexAddedFavoriteCorrectedData: Int) {
        favoriteListData.removeAndAddFavoriteDataByCorrectedData(
            indexRemovedFavoriteCorrectedData, indexAddedFavoriteCorrectedData)
    }
    //endregion

    // Ранжирование списка fullDatesList по приоритетам
    fun priorityRangeFullDatesList() {
        favoriteListData.priorityRangeFullDatesList()
    }
}