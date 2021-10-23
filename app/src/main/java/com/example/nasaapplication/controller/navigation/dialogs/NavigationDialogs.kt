package com.example.nasaapplication.controller.navigation.dialogs

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.example.nasaapplication.ui.fragments.dialogs.BottomNavigationDrawerDialogFragment

class NavigationDialogs {
    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
    private var bottomNavigationDrawerDialogFragment: BottomNavigationDrawerDialogFragment? = null
    //endregion

    // Отображение диалога с контекстным меню, появляющимся при нажатии на кнопку типа "Гамбургер"
    fun showBottomNavigationDrawerDialogFragment(fragmentActivity: FragmentActivity
    ) {
        bottomNavigationDrawerDialogFragment =
            BottomNavigationDrawerDialogFragment()
        bottomNavigationDrawerDialogFragment?.let{
            it.show(fragmentActivity.supportFragmentManager, "")
        }
    }
}