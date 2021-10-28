package com.example.nasaapplication.ui.fragments.contents

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.nasaapplication.R
import com.example.nasaapplication.controller.navigation.contents.NavigationContent
import com.example.nasaapplication.controller.navigation.dialogs.NavigationDialogs
import com.example.nasaapplication.databinding.FragmentSettingsBinding
import com.example.nasaapplication.ui.ConstantsUi
import com.example.nasaapplication.ui.activities.MainActivity
import com.example.nasaapplication.ui.utils.ViewBindingFragment
import com.google.android.material.chip.Chip

class SettingsFragment: ViewBindingFragment<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {
    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
    // Navigations
    private var navigationDialogs: NavigationDialogs? = null
    private var navigationContent: NavigationContent? = null
    // Buttons (Chip)
    private var buttonStyleChooseDay: Chip? = null
    private var buttonStyleChooseNight: Chip? = null
    //endregion

    companion object {
        fun newInstance() = SettingsFragment()
        private var isMain = true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //region ПОЛУЧЕНИЕ КЛАССОВ НАВИГАТОРОВ
        navigationDialogs = (context as MainActivity).getNavigationDialogs()
        navigationContent = (context as MainActivity).getNavigationContent()
        //endregion
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    //region МЕТОДЫ ДЛЯ РАБОТЫ С BOTTOM NAVIGATION MENU
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Установка слушателей на кнопки выбора тем
        buttonStyleChooseDay = view.findViewById(R.id.button_style_day)
        buttonStyleChooseNight = view.findViewById(R.id.button_style_night)
        buttonStyleChooseDay?.let {
            it.setOnClickListener {
                buttonStyleChooseNight?.let { it.visibility = View.INVISIBLE }
                (requireActivity() as MainActivity).setIsThemeDay(true)
                val sharedPreferences: SharedPreferences =
                    requireActivity().getSharedPreferences(ConstantsUi.SHARED_PREFERENCES_KEY,
                        AppCompatActivity.MODE_PRIVATE
                    )
                var sharedPreferencesEditor: SharedPreferences.Editor = sharedPreferences.edit()
                sharedPreferencesEditor.putBoolean(ConstantsUi.SHARED_PREFERENCES_THEME_KEY, true)
                sharedPreferencesEditor.apply()
                // Перезапуск видео в классе FireView
                binding.settingsFireWall.setIsClick(true)
                binding.settingsFireWall.invalidate()
            }
        }

        buttonStyleChooseNight?.let {
            it.setOnClickListener {
                buttonStyleChooseDay?.let { it.visibility = View.INVISIBLE }
                (requireActivity() as MainActivity).setIsThemeDay(false)
                val sharedPreferences: SharedPreferences =
                    requireActivity().getSharedPreferences(ConstantsUi.SHARED_PREFERENCES_KEY,
                        AppCompatActivity.MODE_PRIVATE
                    )
                var sharedPreferencesEditor: SharedPreferences.Editor = sharedPreferences.edit()
                sharedPreferencesEditor.putBoolean(ConstantsUi.SHARED_PREFERENCES_THEME_KEY, false)
                sharedPreferencesEditor.apply()
                // Перезапуск видео в классе FireView
                binding.settingsFireWall.setIsClick(true)
                binding.settingsFireWall.invalidate()
            }
        }
    }
}