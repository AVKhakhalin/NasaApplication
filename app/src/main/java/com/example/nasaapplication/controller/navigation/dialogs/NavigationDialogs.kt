package com.example.nasaapplication.controller.navigation.dialogs

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import com.example.nasaapplication.ui.fragments.dialogs.BottomNavigationDrawerDialogFragment

class NavigationDialogs {
    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
    private var bottomNavigationDrawerDialogFragment: BottomNavigationDrawerDialogFragment? = null
    //endregion

    // Метод получения bottomNavigationDrawerDialogFragment
    fun getBottomNavigationDrawerDialogFragment(): BottomNavigationDrawerDialogFragment? {
        return bottomNavigationDrawerDialogFragment
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