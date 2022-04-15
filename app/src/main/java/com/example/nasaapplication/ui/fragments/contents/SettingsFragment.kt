package com.example.nasaapplication.ui.fragments.contents

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.nasaapplication.R
import com.example.nasaapplication.controller.navigation.contents.NavigationContent
import com.example.nasaapplication.controller.navigation.dialogs.NavigationDialogs
import com.example.nasaapplication.databinding.FragmentSettingsBinding
import com.example.nasaapplication.Constants
import com.example.nasaapplication.ui.activities.MainActivity
import com.example.nasaapplication.ui.utils.ViewBindingFragment
import com.google.android.material.chip.Chip

class SettingsFragment:
    ViewBindingFragment<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {
    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
    // Navigations
    private lateinit var navigationDialogs: NavigationDialogs
    private lateinit var navigationContent: NavigationContent
    // Buttons (Chip)
    private lateinit var buttonStyleChooseDay: Chip
    private lateinit var buttonStyleChooseNight: Chip
    // MainActivity
    private var mainActivity: MainActivity? = null
    //endregion

    companion object {
        fun newInstance() = SettingsFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
        //region ПОЛУЧЕНИЕ КЛАССОВ НАВИГАТОРОВ
        mainActivity?.let {
            navigationDialogs = it.getNavigationDialogs()
            navigationContent = it.getNavigationContent()
        }
        //endregion
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity?.let { mainActivity ->
            // Изменение вида Bottom Navigation Menu
            mainActivity.setIsMain(true)
            mainActivity.getSetBottomNavigationMenu().switchBottomAppBar()

            // Установка слушателей на кнопки выбора тем
            buttonStyleChooseDay = view.findViewById(R.id.button_style_day)
            buttonStyleChooseNight = view.findViewById(R.id.button_style_night)
            buttonStyleChooseDay?.let {
                it.setOnClickListener {
                    if (!mainActivity.getUIObserversManager().getIsBlockingOtherFABButtons()) {
                        buttonStyleChooseNight?.let { it.visibility = View.INVISIBLE }
                        (requireActivity() as MainActivity).setIsThemeDay(true)
                        val sharedPreferences: SharedPreferences =
                            requireActivity().getSharedPreferences(
                                Constants.SHARED_PREFERENCES_KEY,
                                AppCompatActivity.MODE_PRIVATE
                            )
                        var sharedPreferencesEditor: SharedPreferences.Editor = sharedPreferences.edit()
                        sharedPreferencesEditor.putBoolean(
                            Constants.SHARED_PREFERENCES_THEME_KEY,
                            true
                        )
                        sharedPreferencesEditor.apply()
                        // Перезапуск видео в классе FireView
                        binding.settingsFireWall.setIsClick(true)
                        binding.settingsFireWall.invalidate()
                    }
                }
            }

            buttonStyleChooseNight?.let {
                it.setOnClickListener {
                    if (!mainActivity.getUIObserversManager().getIsBlockingOtherFABButtons()) {
                        buttonStyleChooseDay?.let { it.visibility = View.INVISIBLE }
                        (requireActivity() as MainActivity).setIsThemeDay(false)
                        val sharedPreferences: SharedPreferences =
                            requireActivity().getSharedPreferences(
                                Constants.SHARED_PREFERENCES_KEY,
                                AppCompatActivity.MODE_PRIVATE
                            )
                        var sharedPreferencesEditor: SharedPreferences.Editor =
                            sharedPreferences.edit()
                        sharedPreferencesEditor.putBoolean(
                            Constants.SHARED_PREFERENCES_THEME_KEY,
                            false
                        )
                        sharedPreferencesEditor.apply()
                        // Перезапуск видео в классе FireView
                        binding.settingsFireWall.setIsClick(true)
                        binding.settingsFireWall.invalidate()
                    }
                }
            }
        }
    }
}