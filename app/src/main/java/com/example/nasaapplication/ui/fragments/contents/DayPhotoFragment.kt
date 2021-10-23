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
import com.example.nasaapplication.ui.ConstantsUi
import com.example.nasaapplication.ui.activities.MainActivity
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import java.util.*

class DayPhotoFragment: Fragment() {
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
    // Binding
    private var _binding: FragmentDayPhotoBinding? = null
    private val binding: FragmentDayPhotoBinding
        get() {
            return _binding!!
        }
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
        _binding = FragmentDayPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    //region МЕТОДЫ РАБОТЫ С BottomSheet
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBottomSheetBehavior(view.findViewById(R.id.bottom_sheet_container))
        bottomSheetDescriptionTitle = view.findViewById(R.id.bottom_sheet_description_title)
        bottomSheetDescriptionText = view.findViewById(R.id.bottom_sheet_description_text)

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

        // Инициализация текстового блока для отображения текущей даты
        currentDateTextView = view.findViewById(R.id.fragment_day_photo_current_date_text_view)

        // Инициализация и настройка Chip-кнопок
        if (currentDateTextView != null) {
            buttonChipToday = view.findViewById(R.id.button_today_photo)
            buttonChipToday?.let {
                it.setOnClickListener {
                    currentDateTextView!!.text =
                        "${ConstantsUi.DAY_PHOTO_TEXT} ${getDate(0)}"
                    viewModel.getData(curDate)
                        .observe(viewLifecycleOwner, Observer<PODData> { renderData(it) })

                }
            }
            buttonChipYesterday = view.findViewById(R.id.button_yesterday_photo)
            buttonChipYesterday?.let {
                it.setOnClickListener {
                    currentDateTextView!!.text =
                        "${ConstantsUi.DAY_PHOTO_TEXT} ${getDate(-1)}"
                    viewModel.getData(curDate)
                        .observe(viewLifecycleOwner, Observer<PODData> { renderData(it) })
                }
            }
            buttonChipBeforeYesterday = view.findViewById(R.id.button_before_yesterday_photo)
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