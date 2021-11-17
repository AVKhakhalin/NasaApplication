package com.example.nasaapplication.controller.navigation.contents

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.nasaapplication.Constants
import com.example.nasaapplication.ui.fragments.contents.DayPhotoFragment
import com.example.nasaapplication.ui.fragments.contents.SearchNASAArchiveFragment
import com.example.nasaapplication.ui.fragments.contents.SearchWikiFragment
import com.example.nasaapplication.ui.utils.ViewBindingFragment

class ViewPagerAdapter(
    private val fragmentActivity: FragmentActivity
): FragmentStateAdapter(fragmentActivity) {
    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
    private var fragments = arrayOf(
        DayPhotoFragment(),
        SearchWikiFragment(),
        SearchNASAArchiveFragment()
    )
    //endregion

    //region БАЗОВЫЕ МЕТОДЫ ДЛЯ РАБОТЫ VIEWPAGER2
    override fun getItemCount(): Int {
        return fragments.size
    }
    override fun createFragment(position: Int): Fragment {
        return when(position){
            Constants.DAY_PHOTO_FRAGMENT_INDEX ->
                fragments[Constants.DAY_PHOTO_FRAGMENT_INDEX]
            Constants.SEARCH_WIKI_FRAGMENT_INDEX ->
                fragments[Constants.SEARCH_WIKI_FRAGMENT_INDEX]
            Constants.SEARCH_NASA_ARCHIVE_FRAGMENT_INDEX ->
                fragments[Constants.SEARCH_NASA_ARCHIVE_FRAGMENT_INDEX]
            else -> fragments[Constants.DAY_PHOTO_FRAGMENT_INDEX]
        }
    }
    //endregion

    // Получение фрагментов
    fun getFragments(): Array<ViewBindingFragment <out ViewBinding>> {
        return fragments
    }
}