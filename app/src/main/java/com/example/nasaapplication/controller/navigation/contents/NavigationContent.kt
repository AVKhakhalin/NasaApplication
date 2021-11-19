package com.example.nasaapplication.controller.navigation.contents

import androidx.fragment.app.FragmentManager
import com.example.nasaapplication.R
import com.example.nasaapplication.ui.fragments.contents.FavoriteRecyclerListFragment
import com.example.nasaapplication.ui.fragments.contents.SettingsFragment

class NavigationContent(
    private val fragmentManager: FragmentManager
) {
    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
    private var settingsFragment: SettingsFragment? = null
    private var favoriteRecyclerListFragment: FavoriteRecyclerListFragment? = null
    //endregion

    // Получение SettingsFragment
    fun getSettingsFragment(): SettingsFragment? {
        return settingsFragment
    }

    // Получение FavoriteRecyclerListFragment
    fun getFavoriteRecyclerListFragment(): FavoriteRecyclerListFragment? {
        return favoriteRecyclerListFragment
    }

    // Отображение фрагмента с настройками приложения
    fun showSettingsFragment(useBackStack: Boolean) {
        settingsFragment = SettingsFragment.newInstance()
        // Открыть транзакцию
        if (settingsFragment != null) {
            fragmentManager?.let {
                val fragmentTransaction = it.beginTransaction()
                fragmentTransaction.replace(
                    R.id.activity_fragments_container, settingsFragment!!
                )
                if (useBackStack) {
                    fragmentTransaction.addToBackStack(null)
                }
                // Закрыть транзакцию
                fragmentTransaction.commit()
            }
        }
    }

    // Отображение фрагмента со списком "Избранное"
    fun showFavoriteRecyclerListFragment(useBackStack: Boolean) {
        favoriteRecyclerListFragment = FavoriteRecyclerListFragment.newInstance()
        // Открыть транзакцию
        if (favoriteRecyclerListFragment != null) {
            fragmentManager?.let {
                val fragmentTransaction = it.beginTransaction()
                fragmentTransaction.replace(
                    R.id.activity_fragments_container, favoriteRecyclerListFragment!!
                )
                if (useBackStack) {
                    fragmentTransaction.addToBackStack(null)
                }
                // Закрыть транзакцию
                fragmentTransaction.commit()
            }
        }
    }
}