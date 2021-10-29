package com.example.nasaapplication.controller.navigation.contents

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.nasaapplication.controller.ConstantsController
import com.example.nasaapplication.ui.fragments.contents.DayPhotoFragment
import com.example.nasaapplication.ui.fragments.contents.SearchWikiFragment

class ViewPagerAdapter(private val fragmentManager: FragmentManager):FragmentStatePagerAdapter(fragmentManager) {
    private val fragments = arrayOf(DayPhotoFragment(), SearchWikiFragment())

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getItem(position: Int): Fragment {
        return when(position){
            ConstantsController.DAY_PHOTO_FRAGMENT_INDEX ->
                fragments[ConstantsController.DAY_PHOTO_FRAGMENT_INDEX]
            ConstantsController.SEARCH_WIKI_FRAGMENT_INDEX ->
                fragments[ConstantsController.SEARCH_WIKI_FRAGMENT_INDEX]
            else -> fragments[ConstantsController.DAY_PHOTO_FRAGMENT_INDEX]
        }
    }

    override fun getPageTitle(position: Int): String? {
        return null
    }
}