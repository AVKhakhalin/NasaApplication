package com.example.nasaapplication.controller.navigation

import androidx.fragment.app.FragmentManager
import com.example.nasaapplication.R
import com.example.nasaapplication.ui.fragments.DayPhotoFragment

class NavigationContent(
    private val fragmentManager: FragmentManager
) {
    fun showDayPhotoFragment(useBackStack: Boolean) {
        // Открыть транзакцию
        fragmentManager?.let {
            val fragmentTransaction = it.beginTransaction()
            fragmentTransaction.replace(
                R.id.activity_fragments_container, DayPhotoFragment.newInstance())
            if (useBackStack) {
                fragmentTransaction.addToBackStack(null)
            }
            // Закрыть транзакцию
            fragmentTransaction.commit()
        }
    }
}