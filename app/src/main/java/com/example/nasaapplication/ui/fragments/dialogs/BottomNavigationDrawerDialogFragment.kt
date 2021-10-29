package com.example.nasaapplication.ui.fragments.dialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.viewpager.widget.ViewPager
import com.example.nasaapplication.R
import com.example.nasaapplication.controller.navigation.contents.NavigationContent
import com.example.nasaapplication.databinding.BottomNavigationLayoutBinding
import com.example.nasaapplication.ui.activities.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout

class BottomNavigationDrawerDialogFragment: BottomSheetDialogFragment() {
    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
    // Navigators
    private var navigationContent: NavigationContent? = null
    // Bindig
    private var _binding: BottomNavigationLayoutBinding? = null
    val binding: BottomNavigationLayoutBinding
        get() = _binding!!
    // ViewPager
    var viewPager: ViewPager? = null
    //endregion

    companion object {
        fun newInstance() = BottomNavigationDrawerDialogFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomNavigationLayoutBinding.inflate(inflater)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //region ПОЛУЧЕНИЕ КЛАССОВ НАВИГАТОРОВ
        navigationContent = (context as MainActivity).getNavigationContent()
        //endregion
        // Получение ViewPager
        viewPager = (context as MainActivity).getViewPager()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.navigationView.setNavigationItemSelectedListener { it->
            when(it.itemId) {
                R.id.action_bottom_bar_photo_of_day -> {
                    viewPager?.let { it.currentItem = 0 }
                    // Установка настроек видимости элементов макета
                    requireActivity().findViewById<ViewPager>(R.id.view_pager).visibility =
                        View.VISIBLE
                    requireActivity().findViewById<TabLayout>(R.id.tab_layout).visibility =
                        View.VISIBLE
                    requireActivity().findViewById<FrameLayout>(R.id.activity_fragments_container)
                        .visibility = View.INVISIBLE
                    dismiss()
                }
                R.id.action_bottom_bar_search_to_wiki -> {
                    viewPager?.let { it.currentItem = 1 }
                    // Установка настроек видимости элементов макета
                    requireActivity().findViewById<ViewPager>(R.id.view_pager).visibility =
                        View.VISIBLE
                    requireActivity().findViewById<TabLayout>(R.id.tab_layout).visibility =
                        View.VISIBLE
                    requireActivity().findViewById<FrameLayout>(R.id.activity_fragments_container)
                        .visibility = View.INVISIBLE
                    dismiss()
                }
            }
            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}