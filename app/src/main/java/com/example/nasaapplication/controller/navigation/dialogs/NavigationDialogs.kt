package com.example.nasaapplication.controller.navigation.dialogs

import androidx.fragment.app.FragmentActivity
import com.example.nasaapplication.ui.fragments.dialogs.BottomNavigationDrawerDialogFragment

class NavigationDialogs {
    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
    private var bottomNavigationDrawerDialogFragment: BottomNavigationDrawerDialogFragment? = null
    //endregion

    // Метод получения bottomNavigationDrawerDialogFragment
    fun getBottomNavigationDrawerDialogFragment(): BottomNavigationDrawerDialogFragment? {
        return bottomNavigationDrawerDialogFragment
    }

    // Метод закрытия всех открытых диалоговых фрагментов
    fun closeDialogs() {
        bottomNavigationDrawerDialogFragment?.let { it.dismiss() }
    }

    // Отображение диалога с контекстным меню, появляющимся при нажатии на кнопку типа "Гамбургер"
    fun showBottomNavigationDrawerDialogFragment(
        fragmentActivity: FragmentActivity
    ) {
        bottomNavigationDrawerDialogFragment =
            BottomNavigationDrawerDialogFragment.newInstance()
        bottomNavigationDrawerDialogFragment?.let {
            it.show(fragmentActivity.supportFragmentManager, "")
        }
    }
}