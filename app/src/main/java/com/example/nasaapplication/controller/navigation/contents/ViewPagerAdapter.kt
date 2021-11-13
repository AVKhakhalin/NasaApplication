package com.example.nasaapplication.controller.navigation.contents

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.nasaapplication.controller.ConstantsController
import com.example.nasaapplication.ui.activities.MainActivity
import com.example.nasaapplication.ui.fragments.contents.DayPhotoFragment
import com.example.nasaapplication.ui.fragments.contents.SearchNASAArchiveFragment
import com.example.nasaapplication.ui.fragments.contents.SearchWikiFragment
import com.example.nasaapplication.ui.utils.ViewBindingFragment

class ViewPagerAdapter(
    private val fragmentActivity: FragmentActivity,
    private val mainActivity: MainActivity
): FragmentStateAdapter(fragmentActivity) {
    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
    private var fragments = arrayOf(
        DayPhotoFragment(mainActivity),
        SearchWikiFragment(mainActivity),
        SearchNASAArchiveFragment(mainActivity)
    )
    //endregion

    //region БАЗОВЫЕ МЕТОДЫ ДЛЯ РАБОТЫ VIEWPAGER2
    override fun getItemCount(): Int {
        return fragments.size
    }
    override fun createFragment(position: Int): Fragment {
        return when(position){
            ConstantsController.DAY_PHOTO_FRAGMENT_INDEX ->
                fragments[ConstantsController.DAY_PHOTO_FRAGMENT_INDEX]
            ConstantsController.SEARCH_WIKI_FRAGMENT_INDEX ->
                fragments[ConstantsController.SEARCH_WIKI_FRAGMENT_INDEX]
            ConstantsController.SEARCH_NASA_ARCHIVE_FRAGMENT_INDEX ->
                fragments[ConstantsController.SEARCH_NASA_ARCHIVE_FRAGMENT_INDEX]
            else -> fragments[ConstantsController.DAY_PHOTO_FRAGMENT_INDEX]
        }
    }
    //endregion

    // Получение фрагментов
    fun getFragments(): Array<ViewBindingFragment <out ViewBinding>> {
        return fragments
    }
}