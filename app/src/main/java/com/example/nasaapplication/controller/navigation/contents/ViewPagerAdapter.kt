package com.example.nasaapplication.controller.navigation.contents

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.nasaapplication.controller.ConstantsController
import com.example.nasaapplication.ui.activities.MainActivity
import com.example.nasaapplication.ui.fragments.contents.DayPhotoFragment
import com.example.nasaapplication.ui.fragments.contents.SearchWikiFragment

class ViewPagerAdapter(private val fragmentManager: FragmentManager):FragmentStatePagerAdapter(fragmentManager) {
    private val fragments = arrayOf(DayPhotoFragment(), SearchWikiFragment())

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> fragments[ConstantsController.DAY_PHOTO_FRAGMENT]
            1 -> fragments[ConstantsController.SEARCH_WIKI_FRAGMENT]
            else -> fragments[ConstantsController.DAY_PHOTO_FRAGMENT]
        }
    }

    override fun getPageTitle(position: Int): String {
        return when(position){
            0 -> ConstantsController.DAY_PHOTO_FRAGMENT_TITLE
            1 -> ConstantsController.SEARCH_WIKI_FRAGMENT_TITLE
            else -> ConstantsController.DAY_PHOTO_FRAGMENT_TITLE
        }
    }
}