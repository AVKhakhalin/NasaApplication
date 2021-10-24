package com.example.nasaapplication.ui.fragments.contents

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.nasaapplication.R
import com.example.nasaapplication.controller.navigation.contents.NavigationContent
import com.example.nasaapplication.controller.navigation.dialogs.NavigationDialogs
import com.example.nasaapplication.controller.observers.viewmodels.PODData
import com.example.nasaapplication.databinding.FragmentSettingsBinding
import com.example.nasaapplication.ui.ConstantsUi
import com.example.nasaapplication.ui.activities.MainActivity
import com.example.nasaapplication.ui.utils.FireView
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.chip.Chip

class SettingsFragment: Fragment() {
    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
    // Navigations
    private var navigationDialogs: NavigationDialogs? = null
    private var navigationContent: NavigationContent? = null
    // Buttons (Chip)
    private var buttonStyleChooseDay: Chip? = null
    private var buttonStyleChooseNight: Chip? = null
    // Binding
    private var _binding: FragmentSettingsBinding? = null
    private val binding: FragmentSettingsBinding
        get() {
            return _binding!!
        }
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
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    //region МЕТОДЫ ДЛЯ РАБОТЫ С BOTTOM NAVIGATION MENU
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Установка слушателей на кнопки выбора тем
        buttonStyleChooseDay = view.findViewById(R.id.button_style_day)
        buttonStyleChooseDay?.let {
            it.setOnClickListener {
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
        buttonStyleChooseNight = view.findViewById(R.id.button_style_night)
        buttonStyleChooseNight?.let {
            it.setOnClickListener {
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

        // Установка BOTTOM NAVIGATION MENU
        setBottomAppBar(view)
    }

    private fun setBottomAppBar(view: View) {
        val context = activity as MainActivity
        context.setSupportActionBar(binding.bottomAppBar)
        setHasOptionsMenu(true)
        switchBottomAppBar(context)
        binding.bottomAppBarFab.setOnClickListener {
            switchBottomAppBar(context)
        }
    }

    // Переключение режима нижней навигационной кнопки BottomAppBar
    // с центрального на крайнее левое положение и обратно
    private fun switchBottomAppBar(context: MainActivity) {
        if (isMain) {
            isMain = false
            binding.bottomAppBar.navigationIcon = null
            binding.bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
            binding.bottomAppBarFab.setImageDrawable(
                ContextCompat.getDrawable(
                    context, R.drawable.ic_back_fab
                )
            )
            binding.bottomAppBar.replaceMenu(R.menu.bottom_menu_bottom_bar_other_screen)
        } else {
            isMain = true
            binding.bottomAppBar.navigationIcon =
                ContextCompat.getDrawable(context, R.drawable.ic_hamburger_menu_bottom_bar)
            binding.bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
            binding.bottomAppBarFab.setImageDrawable(
                ContextCompat.getDrawable(
                    context, R.drawable.ic_plus_fab
                )
            )
            binding.bottomAppBar.replaceMenu(R.menu.bottom_menu_bottom_bar)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.app_bar_save -> toast("Сохранение")
            R.id.app_bar_settings -> toast("Настройки")
            R.id.app_bar_search -> toast("Поиск")
            android.R.id.home -> {
                navigationDialogs?.let {
                    it.showBottomNavigationDrawerDialogFragment(requireActivity())
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
    //endregion

    // Метод для отображения сообщения в виде Toast
    private fun Fragment.toast(string: String?) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).apply {
            setGravity(Gravity.BOTTOM, 0, 250)
            show()
        }
    }
}