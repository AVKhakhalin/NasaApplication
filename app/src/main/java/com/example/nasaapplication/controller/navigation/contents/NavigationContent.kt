package com.example.nasaapplication.controller.navigation.contents

import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import com.example.nasaapplication.R
import com.example.nasaapplication.ui.fragments.contents.DayPhotoFragment
import com.example.nasaapplication.ui.fragments.contents.SearchWikiFragment
import com.example.nasaapplication.ui.fragments.contents.SettingsFragment

class NavigationContent(
    private val fragmentManager: FragmentManager
) {
    // Отображение фрагмента с настройками приложения
    fun showSettingsFragment(useBackStack: Boolean) {
        // Открыть транзакцию
        fragmentManager?.let {
            val fragmentTransaction = it.beginTransaction()
            fragmentTransaction.replace(
                R.id.activity_fragments_container, SettingsFragment.newInstance())
            if (useBackStack) {
                fragmentTransaction.addToBackStack(null)
            }
            // Закрыть транзакцию
            fragmentTransaction.commit()
        }
    }
}