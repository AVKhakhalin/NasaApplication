package com.example.nasaapplication.controller.navigation.contents

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.nasaapplication.controller.ConstantsController
import com.example.nasaapplication.ui.fragments.contents.DayPhotoFragment

private const val SYSTEM = 0

class ViewPagerAdapter(private val fragmentManager: FragmentManager):FragmentStatePagerAdapter(fragmentManager) {
    private val fragments = arrayOf(DayPhotoFragment())

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getItem(position: Int): Fragment {
        return when(position){
            0-> fragments[SYSTEM]
            else ->fragments[SYSTEM]
        }
    }

    override fun getPageTitle(position: Int): String {
        return when(position){
            0 -> ConstantsController.DAY_PHOTO_TEXT
            else -> ConstantsController.DAY_PHOTO_TEXT
        }
    }
}