package com.example.nasaapplication.ui.fragments.dialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.nasaapplication.R
import com.example.nasaapplication.Constants
import com.example.nasaapplication.controller.navigation.contents.NavigationContent
import com.example.nasaapplication.databinding.BottomNavigationLayoutBinding
import com.example.nasaapplication.ui.activities.MainActivity
import com.example.nasaapplication.ui.fragments.contents.DayPhotoFragment
import com.example.nasaapplication.ui.fragments.contents.SearchNASAArchiveFragment
import com.example.nasaapplication.ui.fragments.contents.SearchWikiFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomNavigationDrawerDialogFragment: BottomSheetDialogFragment() {
    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
    // Navigators
    private var navigationContent: NavigationContent? = null
    // Bindig
    private var _binding: BottomNavigationLayoutBinding? = null
    val binding: BottomNavigationLayoutBinding
        get() = _binding!!
    // ViewPager
    var viewPager: ViewPager2? = null
    // MainActivity
    lateinit var mainActivity: MainActivity
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
        mainActivity = (context as MainActivity)
        //region ПОЛУЧЕНИЕ КЛАССОВ НАВИГАТОРОВ
        navigationContent = mainActivity.getNavigationContent()
        //endregion
        // Получение ViewPager
        viewPager = mainActivity.getViewPager()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.navigationView.setNavigationItemSelectedListener { it->
            when(it.itemId) {
                R.id.action_bottom_bar_photo_of_day -> {
                    viewPager?.let { it.currentItem = 0 }
                    hideShowDismissElements()
                    // Начальная настройка фрагмента "Картинка дня"
                    mainActivity.getUIObserversManager().initialSettingDayPhotoFragment()
                }
                R.id.action_bottom_bar_search_to_wiki -> {
                    viewPager?.let { it.currentItem = 1 }
                    hideShowDismissElements()
                    // Начальная настройка фрагмента "Поиск в Википедии"
                    mainActivity.getUIObserversManager().showSearchWikiFragment()
                }
                R.id.action_bottom_bar_search_to_nasa_archive -> {
                    viewPager?.let { it.currentItem = 2 }
                    hideShowDismissElements()
                    // Начальная настройка фрагмента "Поиск в архиве NASA"
                    mainActivity.getUIObserversManager().showSearchNASAArchiveFragment()
                }
            }
            true
        }
    }

    fun hideShowDismissElements() {
        // Установка настроек видимости элементов макета
        requireActivity().findViewById<FrameLayout>(R.id.activity_fragments_container)
            .visibility = View.INVISIBLE
        requireActivity().findViewById<FrameLayout>(R.id.transparent_background)
            .visibility = View.VISIBLE
        dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}