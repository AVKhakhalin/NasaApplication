package com.example.nasaapplication.ui.activities

import android.animation.ObjectAnimator
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.example.nasaapplication.Constants
import com.example.nasaapplication.R
import com.example.nasaapplication.ui.fragments.contents.DayPhotoFragment
import com.example.nasaapplication.ui.fragments.contents.SearchNASAArchiveFragment
import com.example.nasaapplication.ui.fragments.contents.SearchWikiFragment
import com.example.nasaapplication.ui.utils.ThemeColor
import com.google.android.material.bottomappbar.BottomAppBar
import kotlin.math.round
import kotlin.math.sqrt

class SetBottomNavigationMenu(
    private val mainActivity: MainActivity,
    private val durationAnimation: Long,
    private val transparientValue: Float,
    private val notTransparientValue: Float,
    private val themeColor: ThemeColor?
) {
    fun setMenu() {
        // Установка Bottom Navigation Menu
        mainActivity.setBottomAppBar()
        // Установка слушателя на длительное нажатие на нижнюю кнопку FAB
        mainActivity.binding.fabButtonsGroup.visibility = View.INVISIBLE
        mainActivity.binding.bottomNavigationMenu.bottomAppBarFab.setOnLongClickListener {
            if (mainActivity.getIsFABButtonsGroupView()) {
                // Установка анимационного просветления фона
                mainActivity.setHideShowBackgroundAnimation(
                    transparientValue, durationAnimation, false)
                // Установка признака блокировки кнопок во всем приложении,
                // при появления меню из нижней FAB
                mainActivity.setIsBlockingOtherFABButtons(false)
                // Разблокировка перелистывания во View Pager 2
                mainActivity.binding.viewPager.setUserInputEnabled(true)
                // Разблокировка кликов по закладкам во View Pager 2
                mainActivity.getTouchableListTabLayout().forEach { it.isEnabled = true }
                // Скрытие группы кнопок от меню кнопки FAB
                mainActivity.binding.fabButtonsGroup.visibility = View.INVISIBLE
                mainActivity.setIsFABButtonsGroupView(!mainActivity.getIsFABButtonsGroupView())
            } else {
                // Установка анимационного затенения фона
                mainActivity.setHideShowBackgroundAnimation(
                    notTransparientValue, durationAnimation, false)
                // Установка признака блокировки кнопок во всем приложении,
                // при появления меню из нижней FAB
                mainActivity.setIsBlockingOtherFABButtons(true)
                // Блокировка перелистывания во View Pager 2
                mainActivity.binding.viewPager.setUserInputEnabled(false)
                // Блокировка кликов по закладкам во View Pager 2
                mainActivity.getTouchableListTabLayout().forEach { it.isEnabled = false }

                // Анимация появления кнопок меню из нижней кнопки FAB
                if (mainActivity.getIsMain()) {
                    val constraintLayout =
                        mainActivity.findViewById<ConstraintLayout>(R.id.fab_buttons_container)
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
                    mainActivity.binding.fabButtonsGroup.visibility = View.VISIBLE
                    mainActivity.setIsFABButtonsGroupView(!mainActivity.getIsFABButtonsGroupView())
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
                            Thread.sleep(deltaTime)
                            handler.post {
                                val constraintLayout =
                                    mainActivity.findViewById<ConstraintLayout>(R.id.fab_buttons_container)
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
                        mainActivity.findViewById<ConstraintLayout>(R.id.fab_buttons_container)
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
                    mainActivity.binding.fabButtonsGroup.visibility = View.VISIBLE
                    mainActivity.setIsFABButtonsGroupView(!mainActivity.getIsFABButtonsGroupView())
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
                            Thread.sleep(deltaTime)
                            handler.post {
                                val constraintLayout =
                                    mainActivity.findViewById<ConstraintLayout>(R.id.fab_buttons_container)
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
        mainActivity.binding.fabButtonsContainer.getViewById(R.id.fab_button_day_photo).setOnClickListener {
            mainActivity.binding.fabButtonsGroup.visibility = View.INVISIBLE
            mainActivity.hideAndShowFragmentsContainersAndDismissDialogs()
            mainActivity.setIsFABButtonsGroupView(false)
            mainActivity.binding.viewPager.currentItem = Constants.DAY_PHOTO_FRAGMENT_INDEX
            // Проба анимации кнопки
//            TransitionManager.beginDelayedTransition(mainActivity.binding.fabButtonsContainer, Slide(Gravity.END))
//            mainActivity.binding.fabButtonDayPhoto.visibility = View.GONE
            mainActivity.setIsBlockingOtherFABButtons(false)
            // Установка анимационного просветления фона
            mainActivity.setHideShowBackgroundAnimation(
                transparientValue, durationAnimation, true)
            // Разблокировка перелистывания во View Pager 2
            mainActivity.binding.viewPager.setUserInputEnabled(true)
            // Разблокировка кликов по закладкам во View Pager 2
            mainActivity.getTouchableListTabLayout().forEach { it.isEnabled = true }
            // Начальная настройка фрагмента "Картинка дня"
            (mainActivity.getViewPagerAdapter().getFragments()[Constants.DAY_PHOTO_FRAGMENT_INDEX]
                    as DayPhotoFragment).initialSettingFragment()
        }
        // Установка слушателя на нажатие кнопки вызова фрагмента с поиском в Википедии
        mainActivity.binding.fabButtonsContainer.getViewById(R.id.fab_button_search_in_wiki)
            .setOnClickListener {
                mainActivity.binding.fabButtonsGroup.visibility = View.INVISIBLE
                mainActivity.hideAndShowFragmentsContainersAndDismissDialogs()
                mainActivity.setIsFABButtonsGroupView(false)
                mainActivity.binding.viewPager.currentItem = Constants.SEARCH_WIKI_FRAGMENT_INDEX
                mainActivity.setIsBlockingOtherFABButtons(false)
                // Установка анимационного просветления фона
                mainActivity.setHideShowBackgroundAnimation(
                    transparientValue, durationAnimation, true)
                // Разблокировка перелистывания во View Pager 2
                mainActivity.binding.viewPager.setUserInputEnabled(true)
                // Разблокировка кликов по закладкам во View Pager 2
                mainActivity.getTouchableListTabLayout().forEach { it.isEnabled = true }
                // Начальная настройка фрагмента "Поиск в Википедии"
                (mainActivity.getViewPagerAdapter()
                    .getFragments()[Constants.SEARCH_WIKI_FRAGMENT_INDEX]
                        as SearchWikiFragment).initialSettingFragment()
            }
        // Установка слушателя на нажатие кнопки вызова фрагмента с поиском в архиве NASA
        mainActivity.binding.fabButtonsContainer.getViewById(R.id.fab_button_search_in_nasa_archive)
            .setOnClickListener {
                mainActivity.binding.fabButtonsGroup.visibility = View.INVISIBLE
                mainActivity.hideAndShowFragmentsContainersAndDismissDialogs()
                mainActivity.setIsFABButtonsGroupView(false)
                mainActivity.binding.viewPager.currentItem =
                    Constants.SEARCH_NASA_ARCHIVE_FRAGMENT_INDEX
                mainActivity.setIsBlockingOtherFABButtons(false)
                // Установка анимационного просветления фона
                mainActivity.setHideShowBackgroundAnimation(
                    transparientValue, durationAnimation, true)
                // Разблокировка перелистывания во ViewPager 2
                mainActivity.binding.viewPager.setUserInputEnabled(true)
                // Разблокировка кликов по закладкам во ViewPager 2
                mainActivity.getTouchableListTabLayout().forEach { it.isEnabled = true }
                // Начальная настройка фрагмента "Поиск в архиве NASA"
                (mainActivity.getViewPagerAdapter()
                    .getFragments()[Constants.SEARCH_NASA_ARCHIVE_FRAGMENT_INDEX]
                        as SearchNASAArchiveFragment).initialSettingFragment()
            }
        // Установка слушателя на нажатие кнопки вызова настроек приложения
        mainActivity.binding.fabButtonsContainer.getViewById(R.id.fab_button_settings).setOnClickListener {
            mainActivity.binding.fabButtonsGroup.visibility = View.INVISIBLE
            mainActivity.setIsFABButtonsGroupView(false)
            mainActivity.setIsBlockingOtherFABButtons(false)
            mainActivity.showSettingsFragment()
            // Установка анимационного просветления фона
            mainActivity.setHideShowBackgroundAnimation(
                transparientValue, durationAnimation, true)
            // Разблокировка перелистывания во ViewPager 2
            mainActivity.binding.viewPager.setUserInputEnabled(true)
            // Разблокировка кликов по закладкам во ViewPager 2
            mainActivity.getTouchableListTabLayout().forEach { it.isEnabled = true }
        }
    }

    // Переключение режима нижней навигационной кнопки BottomAppBar
    // с центрального на крайнее правое положение и обратно
    fun switchBottomAppBar() {
        // Отключение блокировки всех кнопок, кроме кнопок, появившихся из FAB
        mainActivity.setIsBlockingOtherFABButtons(false)
        // Установка анимационного просветления фона
        mainActivity.setHideShowBackgroundAnimation(
            transparientValue, durationAnimation, true)
        // Отображение навигационного меню View Pager
        mainActivity.binding.tabLayout.visibility = View.VISIBLE
        // Анимация вращения картинки на нижней кнопке FAB
        ObjectAnimator.ofFloat(mainActivity.binding.bottomNavigationMenu.bottomAppBarFab,
            "rotation", 0f, Constants.ANGLE_TO_ROTATE_BOTTOM_FAB).start()

        if (mainActivity.getIsMain()) {
            // Изменение нижего меню, выходящего из FAB
            if (mainActivity.getIsFABButtonsGroupView()) {
                mainActivity.binding.fabButtonsGroup.visibility = View.INVISIBLE
                mainActivity.setIsFABButtonsGroupView(!mainActivity.getIsFABButtonsGroupView())
            }
            // Изменение нижней кнопки FAB
            mainActivity.setIsMain(false)
            mainActivity.binding.bottomNavigationMenu.bottomAppBar.navigationIcon = null
            mainActivity.binding.bottomNavigationMenu.bottomAppBar.fabAlignmentMode =
                BottomAppBar.FAB_ALIGNMENT_MODE_END
            mainActivity.binding.bottomNavigationMenu.bottomAppBarFab.setImageDrawable(
                ContextCompat.getDrawable(mainActivity, R.drawable.ic_back_fab)
            )
            mainActivity.binding.bottomNavigationMenu.bottomAppBar.replaceMenu(
                R.menu.bottom_menu_bottom_bar_other_screen)

            //region НАСТРОЙКИ ПОИСКОВОГО ПОЛЯ
            val searchViewActionView = mainActivity.binding.bottomNavigationMenu.bottomAppBar.menu
                .findItem(R.id.action_bottom_bar_search_request_form).actionView
            val searchView = searchViewActionView as SearchView
            // Событие установки поискового запроса
            searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    // Отображение полученного поискового запроса
                    mainActivity.getFacadeFavoriteLogic().setFilterWord(query)
                    mainActivity.getNavigationContent().getFavoriteRecyclerListFragment()?.let {
                        it.getAdapter()?.let { adapter ->
                            adapter.setFavoriteData(
                                mainActivity.getFacadeFavoriteLogic().getFavoriteDataList())
                            adapter.notifyDataSetChanged()
                        }
                    }
                    return false
                }
                // Отслеживание появления каждого символа
                override fun onQueryTextChange(newText: String): Boolean {
                    // Отображение полученного поискового запроса
                    mainActivity.getFacadeFavoriteLogic().setFilterWord(newText)
                    mainActivity.getNavigationContent().getFavoriteRecyclerListFragment()?.let {
                        it.getAdapter()?.let { adapter ->
                            adapter.setFavoriteData(
                                mainActivity.getFacadeFavoriteLogic().getFavoriteDataList())
                            adapter.notifyDataSetChanged()
                        }
                    }
                    return false
                }
            })
            // Событие на закрытие поискового окна (обнуление фильтра)
            searchView.setOnCloseListener {
                // Отображение полученного поискового запроса
                mainActivity.getFacadeFavoriteLogic().setFilterWord("")
                mainActivity.getNavigationContent().getFavoriteRecyclerListFragment()?.let {
                    it.getAdapter()?.let {adapter ->
                        adapter.setFavoriteData(
                            mainActivity.getFacadeFavoriteLogic().getFavoriteDataList())
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
            themeColor?.let {
                searchedEditText.setTextColor(it.getColorTypedValue())
                searchedEditText.setHintTextColor(it.getColorTypedValue())
            }
            // Установка размера поискового текста
            searchedEditText.setTextSize(Constants.SEARCH_FIELD_TEXT_SIZE)
            // Установка значка поиска внутри editText (без исчезновения)
//            editText.setCompoundDrawablesWithIntrinsicBounds(
//                android.R.drawable.ic_menu_search,0,0,0)
            //endregion
        } else {
            // Изменение нижего меню, выходящего из FAB
            if (mainActivity.getIsFABButtonsGroupView()) {
                mainActivity.binding.fabButtonsGroup.visibility = View.INVISIBLE
                mainActivity.setIsFABButtonsGroupView(!mainActivity.getIsFABButtonsGroupView())
            }
            // Изменение нижней кнопки FAB
            mainActivity.setIsMain(true)
            mainActivity.binding.bottomNavigationMenu.bottomAppBar.navigationIcon =
                ContextCompat.getDrawable(mainActivity, R.drawable.ic_hamburger_menu_bottom_bar)
            mainActivity.binding.bottomNavigationMenu.bottomAppBar.fabAlignmentMode =
                BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
            mainActivity.binding.bottomNavigationMenu.bottomAppBarFab.setImageDrawable(
                ContextCompat.getDrawable(mainActivity, R.drawable.ic_plus_fab)
            )
            mainActivity.binding.bottomNavigationMenu.bottomAppBar
                .replaceMenu(R.menu.bottom_menu_bottom_bar)
            // Изменение вида иконки сердца
            mainActivity.changeHeartIconState(mainActivity, false, false)
        }
    }
}