package com.example.nasaapplication.ui.fragments.dialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.nasaapplication.R
import com.example.nasaapplication.controller.navigation.contents.NavigationContent
import com.example.nasaapplication.databinding.BottomNavigationLayoutBinding
import com.example.nasaapplication.ui.activities.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomNavigationDrawerDialogFragment: BottomSheetDialogFragment() {
    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
    // Navigators
    private var navigationContent: NavigationContent? = null
    // Bindig
    private var _binding: BottomNavigationLayoutBinding? = null
    val binding: BottomNavigationLayoutBinding
        get() = _binding!!
    //endregion

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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        super.onViewCreated(view, savedInstanceState)
        binding.navigationView.setNavigationItemSelectedListener { it->
            when(it.itemId) {
                R.id.action_navigation_to_archive -> {
                    Toast.makeText(context,"Перейти в архив", Toast.LENGTH_SHORT).show()
                }
                R.id.action_navigation_to_send -> {
                    Toast.makeText(context,"Перейти к отправке", Toast.LENGTH_SHORT).show()
                }
                R.id.action_navigation_to_observe -> {
                    navigationContent?.let { it.showDayPhotoFragment(false) }
                }
            }
            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}