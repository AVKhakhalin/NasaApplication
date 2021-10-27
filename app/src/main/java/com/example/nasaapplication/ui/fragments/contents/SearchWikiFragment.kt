package com.example.nasaapplication.ui.fragments.contents

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import coil.load
import com.example.nasaapplication.R
import com.example.nasaapplication.controller.navigation.contents.NavigationContent
import com.example.nasaapplication.controller.navigation.dialogs.NavigationDialogs
import com.example.nasaapplication.controller.observers.viewmodels.PODData
import com.example.nasaapplication.controller.observers.viewmodels.PODViewModel
import com.example.nasaapplication.databinding.FragmentDayPhotoBinding
import com.example.nasaapplication.databinding.FragmentSearchWikiBinding
import com.example.nasaapplication.ui.ConstantsUi
import com.example.nasaapplication.ui.activities.MainActivity
import com.example.nasaapplication.ui.utils.ViewBindingFragment
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import java.util.*

class SearchWikiFragment: ViewBindingFragment<FragmentSearchWikiBinding>(FragmentSearchWikiBinding::inflate) {
    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
    // Navigations
    private var navigationDialogs: NavigationDialogs? = null
    private var navigationContent: NavigationContent? = null
    // TextView с датой
    private var curDate: String = ""
    // ViewModel
    private val viewModel: PODViewModel by lazy {
        ViewModelProviders.of(this).get(PODViewModel::class.java)
    }
    // BottomSheet
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    //endregion

    companion object {
        fun newInstance() = SearchWikiFragment()
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

    //region МЕТОДЫ РАБОТЫ С BottomSheet
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.inputWikiField.setEndIconOnClickListener {
            if ((binding.inputWikiFieldText.text != null) &&
                (binding.inputWikiFieldText.text!!.length <=
                        binding.inputWikiField.counterMaxLength)) {
                startActivity(Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(
                        "${ConstantsUi.WIKI_URL}${
                            binding.inputWikiFieldText.text.toString()
                        }"
                    )
                })
            }
        }

        // Установка BOTTOM NAVIGATION MENU
        setBottomAppBar(view)
    }

    private fun setBottomSheetBehavior(bottomSheet: ConstraintLayout) {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }
    //endregion

    //region МЕТОДЫ ДЛЯ РАБОТЫ С BOTTOM NAVIGATION MENU
    private fun setBottomAppBar(view: View) {
        val context = activity as MainActivity
        context.setSupportActionBar(binding.bottomAppBar)
        setHasOptionsMenu(true)

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
//        inflater.inflate(R.menu.bottom_menu_bottom_bar, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.app_bar_save -> toast("Сохранение")
            R.id.app_bar_settings -> navigationContent?.let{
                it.showSettingsFragment(false)
            }
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