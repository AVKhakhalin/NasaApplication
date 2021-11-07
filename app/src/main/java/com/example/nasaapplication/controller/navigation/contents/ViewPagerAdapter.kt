package com.example.nasaapplication.controller.navigation.contents

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.nasaapplication.controller.ConstantsController
import com.example.nasaapplication.ui.fragments.contents.DayPhotoFragment
import com.example.nasaapplication.ui.fragments.contents.SearchNASAArchiveFragment
import com.example.nasaapplication.ui.fragments.contents.SearchWikiFragment

class ViewPagerAdapter(
    private val fragmentActivity: FragmentActivity
): FragmentStateAdapter(fragmentActivity) {

    private var fragments = arrayOf(
        DayPhotoFragment(),
        SearchWikiFragment(),
        SearchNASAArchiveFragment()
    )

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
}