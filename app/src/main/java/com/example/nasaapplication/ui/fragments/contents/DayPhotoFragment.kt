package com.example.nasaapplication.ui.fragments.contents

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import coil.load
import com.example.nasaapplication.R
import com.example.nasaapplication.controller.navigation.contents.NavigationContent
import com.example.nasaapplication.controller.navigation.dialogs.NavigationDialogs
import com.example.nasaapplication.controller.observers.viewmodels.PODData
import com.example.nasaapplication.controller.observers.viewmodels.PODViewModel
import com.example.nasaapplication.databinding.FragmentDayPhotoBinding
import com.example.nasaapplication.ui.ConstantsUi
import com.example.nasaapplication.ui.activities.MainActivity
import com.example.nasaapplication.ui.utils.ViewBindingFragment
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayout
import java.util.*

class DayPhotoFragment: ViewBindingFragment<FragmentDayPhotoBinding>(FragmentDayPhotoBinding::inflate) {
    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
    // Navigations
    private var navigationDialogs: NavigationDialogs? = null
    private var navigationContent: NavigationContent? = null
    // Buttons (Chip)
    private var buttonChipYesterday: Chip? = null
    private var buttonChipToday: Chip? = null
    private var buttonChipBeforeYesterday: Chip? = null
    // TextView с датой
    private var currentDateTextView: TextView? = null
    private var curDate: String = ""
    // ViewModel
    private val viewModel: PODViewModel by lazy {
        ViewModelProviders.of(this).get(PODViewModel::class.java)
    }
    // BottomSheet
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var bottomSheetDescriptionTitle: TextView
    private lateinit var bottomSheetDescriptionText: TextView
    //endregion

    companion object {
        fun newInstance() = DayPhotoFragment()
        private var isMain = true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //region ПОЛУЧЕНИЕ КЛАССОВ НАВИГАТОРОВ
        navigationDialogs = (context as MainActivity).getNavigationDialogs()
        navigationContent = (context as MainActivity).getNavigationContent()
        //endregion
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getData(curDate)
//            .observe(this@DayPhotoFragment, Observer<PODData> { renderData(it) })
            .observe(viewLifecycleOwner, Observer<PODData> { renderData(it) })
    }

    private fun renderData(data: PODData) {
        when (data) {
            is PODData.Success -> {
                val serverResponseData = data.serverResponseData
                val url = serverResponseData.url
                if (url.isNullOrEmpty()) {
                    //showError("Сообщение, что ссылка пустая")
                    toast(ConstantsUi.ERROR_LINK_EMPTY)
                } else {
                    //showSuccess()
                    binding.pODImageView.load(url) {
                        lifecycle(this@DayPhotoFragment)
                        error(R.drawable.ic_load_error_vector)
//                        placeholder(R.drawable.ic_downloading)
                    }
                    // Показать описание фотографии дня
                    bottomSheetDescriptionTitle.text = serverResponseData.title
                    bottomSheetDescriptionText.text = serverResponseData.explanation

                    binding.pODImageView.visibility = View.VISIBLE
                    binding.pODLoadingLayout.visibility = View.INVISIBLE
                }
            }
            is PODData.Loading -> {
                //showLoading()
                binding.pODLoadingLayout.visibility = View.VISIBLE
                binding.pODImageView.visibility = View.INVISIBLE
            }
            is PODData.Error -> {
                //showError(data.error.message)
                toast(data.error.message)
            }
        }
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
        setBottomSheetBehavior(view.findViewById(R.id.bottom_sheet_container))
        bottomSheetDescriptionTitle = view.findViewById(R.id.bottom_sheet_description_title)
        bottomSheetDescriptionText = view.findViewById(R.id.bottom_sheet_description_text)

        // Установка текущей даты в заголовке над фотографией
        binding.fragmentDayPhotoCurrentDateTextView.text =
            "${ConstantsUi.DAY_PHOTO_TEXT} ${getDate(0)}"

        // Установка BOTTOM NAVIGATION MENU
        setBottomAppBar(view)

        // Инициализация текстового блока для отображения текущей даты
        currentDateTextView = binding.fragmentDayPhotoCurrentDateTextView

        // Инициализация и настройка Chip-кнопок
        if (currentDateTextView != null) {
            buttonChipToday = binding.buttonTodayPhoto
            buttonChipToday?.let {
                it.setOnClickListener {
                    currentDateTextView!!.text =
                        "${ConstantsUi.DAY_PHOTO_TEXT} ${getDate(0)}"
                    viewModel.getData(curDate)
                        .observe(viewLifecycleOwner, Observer<PODData> { renderData(it) })

                }
            }
            buttonChipYesterday = binding.buttonYesterdayPhoto
            buttonChipYesterday?.let {
                it.setOnClickListener {
                    currentDateTextView!!.text =
                        "${ConstantsUi.DAY_PHOTO_TEXT} ${getDate(-1)}"
                    viewModel.getData(curDate)
                        .observe(viewLifecycleOwner, Observer<PODData> { renderData(it) })
                }
            }
            buttonChipBeforeYesterday = binding.buttonBeforeYesterdayPhoto
            buttonChipBeforeYesterday?.let {
                it.setOnClickListener {
                    currentDateTextView!!.text =
                        "${ConstantsUi.DAY_PHOTO_TEXT} ${getDate(-2)}"
                    viewModel.getData(curDate)
                        .observe(viewLifecycleOwner, Observer<PODData> { renderData(it) })
                }
            }
        }
    }

    private fun setBottomSheetBehavior(bottomSheet: ConstraintLayout) {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }
    //endregion

    //region МЕТОДЫ ДЛЯ РАБОТЫ С BOTTOM NAVIGATION MENU
    private fun setBottomAppBar(view: View) {
        val context = activity as MainActivity
        context.setSupportActionBar(binding.bottomNavigationMenu.bottomAppBar)
        setHasOptionsMenu(true)

        binding.bottomNavigationMenu.bottomAppBarFab.setOnClickListener {
            switchBottomAppBar(context)
        }
    }

    // Переключение режима нижней навигационной кнопки BottomAppBar
    // с центрального на крайнее левое положение и обратно
    private fun switchBottomAppBar(context: MainActivity) {
        if (isMain) {
            isMain = false
            binding.bottomNavigationMenu.bottomAppBar.navigationIcon = null
            binding.bottomNavigationMenu.bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
            binding.bottomNavigationMenu.bottomAppBarFab.setImageDrawable(
                ContextCompat.getDrawable(
                    context, R.drawable.ic_back_fab
                )
            )
            binding.bottomNavigationMenu.bottomAppBar.replaceMenu(R.menu.bottom_menu_bottom_bar_other_screen)
        } else {
            isMain = true
            binding.bottomNavigationMenu.bottomAppBar.navigationIcon =
                ContextCompat.getDrawable(context, R.drawable.ic_hamburger_menu_bottom_bar)
            binding.bottomNavigationMenu.bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
            binding.bottomNavigationMenu.bottomAppBarFab.setImageDrawable(
                ContextCompat.getDrawable(
                    context, R.drawable.ic_plus_fab
                )
            )
            binding.bottomNavigationMenu.bottomAppBar.replaceMenu(R.menu.bottom_menu_bottom_bar)
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
                requireActivity().findViewById<ViewPager>(R.id.view_pager).visibility =
                    View.INVISIBLE
                requireActivity().findViewById<TabLayout>(R.id.tab_layout).visibility =
                    View.INVISIBLE
                requireActivity().findViewById<FrameLayout>(R.id.activity_fragments_container)
                    .visibility = View.VISIBLE
                it.showSettingsFragment(false)
            }
            R.id.app_bar_search -> toast("Поиск")
            android.R.id.home -> {
                toast("DayPhotoFragment Бургер кнопка")
                navigationDialogs?.let {
                    it.showBottomNavigationDrawerDialogFragment(requireActivity())
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
    //endregion

    // Метод для возвращения даты по запросу
    private fun getDate(deltaDayDate: Int): String {
        val calendar: Calendar = Calendar.getInstance(TimeZone.getDefault())
        calendar.add(Calendar.DATE, deltaDayDate)
        val dateYear: Int = calendar.get(Calendar.YEAR)
        val dateMonth: Int = calendar.get(Calendar.MONTH) + 1
        val dateDay: Int = calendar.get(Calendar.DAY_OF_MONTH)
        curDate = "$dateYear-${if (dateMonth < 10) "0$dateMonth" else "$dateMonth"}-${
            if (dateDay < 10) "0$dateDay" else "$dateDay"}"
        return "${if (dateDay < 10) "0$dateDay" else "$dateDay"}.${
            if (dateMonth < 10) "0$dateMonth" else "$dateMonth"}.$dateYear"
    }

    // Метод для отображения сообщения в виде Toast
    private fun Fragment.toast(string: String?) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).apply {
            setGravity(Gravity.BOTTOM, 0, 250)
            show()
        }
    }
}