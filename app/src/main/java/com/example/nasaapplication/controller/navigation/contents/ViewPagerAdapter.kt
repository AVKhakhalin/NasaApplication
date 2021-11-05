package com.example.nasaapplication.controller.navigation.contents

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.nasaapplication.controller.ConstantsController
import com.example.nasaapplication.ui.activities.MainActivity
import com.example.nasaapplication.ui.fragments.contents.DayPhotoFragment
import com.example.nasaapplication.ui.fragments.contents.SearchNASAArchiveFragment
import com.example.nasaapplication.ui.fragments.contents.SearchWikiFragment

class ViewPagerAdapter(
    private val fragmentManager: FragmentManager,
    private val mainActivity: MainActivity
):FragmentStatePagerAdapter(fragmentManager) {
    private val fragments = arrayOf(
        DayPhotoFragment(),
        SearchWikiFragment(),
        SearchNASAArchiveFragment()
    )

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getItem(position: Int): Fragment {
//        var correctedPosition: Int = position
//        if (mainActivity.getIsBlockingOtherFABButtons()) {
//            correctedPosition = mainActivity.currentViewPagerPosition
//        }
//        return when(correctedPosition){
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

    override fun getPageTitle(position: Int): String? {
        return null
    }
}