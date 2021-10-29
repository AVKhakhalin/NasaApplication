package com.example.nasaapplication.ui.fragments.contents

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
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
import com.example.nasaapplication.ui.utils.ViewBindingFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import java.util.*

class DayPhotoFragment: ViewBindingFragment<FragmentDayPhotoBinding>(FragmentDayPhotoBinding::inflate) {
    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
    // Navigations
    private lateinit var navigationDialogs: NavigationDialogs
    private lateinit var navigationContent: NavigationContent
    // Buttons (Chip)
    private lateinit var buttonChipYesterday: Chip
    private lateinit var buttonChipToday: Chip
    private lateinit var buttonChipBeforeYesterday: Chip
    // TextView с датой
    private lateinit var currentDateTextView: TextView
    private var curDate: String = ""
    // ViewModel
    private val viewModel: PODViewModel by lazy {
        ViewModelProviders.of(this).get(PODViewModel::class.java)
    }
    // BottomSheet
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var bottomSheetDescriptionTitle: TextView
    private lateinit var bottomSheetDescriptionText: TextView
    // MainActivity
    private lateinit var mainActivity: MainActivity
    //endregion

    companion object {
        fun newInstance() = DayPhotoFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = (context as MainActivity)
        //region ПОЛУЧЕНИЕ КЛАССОВ НАВИГАТОРОВ
        navigationDialogs = mainActivity.getNavigationDialogs()
        navigationContent = mainActivity.getNavigationContent()
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