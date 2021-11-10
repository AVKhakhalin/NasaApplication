package com.example.nasaapplication.controller.navigation.contents

import androidx.fragment.app.FragmentManager
import com.example.nasaapplication.R
import com.example.nasaapplication.ui.activities.MainActivity
import com.example.nasaapplication.ui.fragments.contents.FavoriteRecyclerListFragment
import com.example.nasaapplication.ui.fragments.contents.SettingsFragment

class NavigationContent(
    private val fragmentManager: FragmentManager,
    private val mainActivity: MainActivity
) {
    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
    private var settingsFragment: SettingsFragment? = null
    private var favoriteRecyclerListFragment: FavoriteRecyclerListFragment? = null
    //endregion

    // Получение settingsFragment
    fun getSettingsFragment(): SettingsFragment? {
        return settingsFragment
    }

    // Отображение фрагмента с настройками приложения
    fun showSettingsFragment(useBackStack: Boolean) {
        settingsFragment = SettingsFragment.newInstance(mainActivity)
        // Открыть транзакцию
        fragmentManager?.let {
            if (settingsFragment != null) {
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
        favoriteRecyclerListFragment = FavoriteRecyclerListFragment.newInstance(mainActivity)
        // Открыть транзакцию
        fragmentManager?.let {
            if (favoriteRecyclerListFragment != null) {
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