package com.example.nasaapplication.ui.fragments.contents

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.ChangeImageTransform
import android.transition.TransitionManager
import android.transition.TransitionSet
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import coil.load
import com.example.nasaapplication.R
import com.example.nasaapplication.controller.ConstantsController
import com.example.nasaapplication.controller.navigation.contents.NavigationContent
import com.example.nasaapplication.controller.navigation.dialogs.NavigationDialogs
import com.example.nasaapplication.controller.observers.viewmodels.POD.PODData
import com.example.nasaapplication.controller.observers.viewmodels.POD.PODViewModel
import com.example.nasaapplication.databinding.FragmentDayPhotoBinding
import com.example.nasaapplication.ui.ConstantsUi
import com.example.nasaapplication.ui.activities.MainActivity
import com.example.nasaapplication.ui.utils.ViewBindingFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import java.util.*

class DayPhotoFragment:
    ViewBindingFragment<FragmentDayPhotoBinding>(FragmentDayPhotoBinding::inflate) {
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
    // Анимация изменения размеров картики
    private var typeChangeImage: Int = 0
    // Анимация появления результирующих данных
    private val durationAnimation: Long = 800
    private val transparientValue: Float = 0f
    private val notTransparientValue: Float = 1f
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

    override fun onResume() {
        // Очистка текущей информации для "Избранное" при переключении на данный фрагмент
        mainActivity.setListFavoriteEmptyData()
        if (mainActivity.getIsFavorite()) mainActivity.changeHeartIconState(mainActivity)
        // Метод проверки наличия текущей информации в списке "Избранное"
        // и отрисовка соответствующего значка сердца (контурная или с заливкой)
        // TODO: ДОДЕЛАТЬ
        super.onResume()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getData(curDate)
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
                    // Сохранение данных для списка "Избранное"
                    mainActivity.setListFavoriteDataLinkSource(viewModel.getRequestUrl())
                    mainActivity.setListFavoriteDataTitle(serverResponseData.title ?: "")
                    mainActivity.setListFavoriteDataDescription(
                        serverResponseData.explanation ?: "")
                    mainActivity.setListFavoriteDataLinkImage(url)
                    mainActivity.setListFavoriteDataTypeSource(
                        ConstantsController.DAY_PHOTO_FRAGMENT_INDEX)
                    mainActivity.setListFavoriteDataPriority(ConstantsUi.PRIORITY_LOW)
                    // Отображение результатов запроса
                    binding.pODImageView.alpha = transparientValue
                    binding.pODImageView.load(url) {
                        lifecycle(this@DayPhotoFragment)
                        error(R.drawable.ic_load_error_vector)
                    }
                    // Анимированное появление картинки дня
                    binding.pODImageView.visibility = View.VISIBLE
                    binding.pODImageView.animate()
                        .alpha(notTransparientValue)
                        .setDuration(durationAnimation)
                        .setListener(object: AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                binding.pODImageView.isClickable = true
                            }
                        })

                    // Анимированное появление заголовка фотографии дня
                    bottomSheetDescriptionTitle.alpha = transparientValue
                    bottomSheetDescriptionTitle.text = serverResponseData.title
                    bottomSheetDescriptionTitle.animate()
                        .alpha(notTransparientValue)
                        .setDuration(durationAnimation)
                        .setListener(object: AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                bottomSheetDescriptionTitle.isClickable = true
                            }
                        })

                    // Анимированное появление
                    bottomSheetDescriptionText.alpha = transparientValue
                    bottomSheetDescriptionText.text = serverResponseData.explanation
                    bottomSheetDescriptionText.animate()
                        .alpha(notTransparientValue)
                        .setDuration(durationAnimation)
                        .setListener(object: AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                bottomSheetDescriptionText.isClickable = true
                            }
                        })

                    // Сброс типа анимации для изменения размера фотографии
                    typeChangeImage = 0
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
                    if (!mainActivity.getIsBlockingOtherFABButtons()) {
                        currentDateTextView!!.text =
                            "${ConstantsUi.DAY_PHOTO_TEXT} ${getDate(0)}"
                        viewModel.getData(curDate)
                            .observe(viewLifecycleOwner, Observer<PODData> { renderData(it) })
                        // Сохранение запроса в "Избранное"
                        mainActivity.setListFavoriteDataSearchRequest(curDate)
                    }
                }
            }
            buttonChipYesterday = binding.buttonYesterdayPhoto
            buttonChipYesterday?.let {
                it.setOnClickListener {
                    if (!mainActivity.getIsBlockingOtherFABButtons()) {
                        currentDateTextView!!.text =
                            "${ConstantsUi.DAY_PHOTO_TEXT} ${getDate(-1)}"
                        viewModel.getData(curDate)
                            .observe(viewLifecycleOwner, Observer<PODData> { renderData(it) })
                        // Сохранение запроса в "Избранное"
                        mainActivity.setListFavoriteDataSearchRequest(curDate)
                    }
                }
            }
            buttonChipBeforeYesterday = binding.buttonBeforeYesterdayPhoto
            buttonChipBeforeYesterday?.let {
                it.setOnClickListener {
                    if (!mainActivity.getIsBlockingOtherFABButtons()) {
                        currentDateTextView!!.text =
                            "${ConstantsUi.DAY_PHOTO_TEXT} ${getDate(-2)}"
                        viewModel.getData(curDate)
                            .observe(viewLifecycleOwner, Observer<PODData> { renderData(it) })
                        // Сохранение запроса в "Избранное"
                        mainActivity.setListFavoriteDataSearchRequest(curDate)
                    }
                }
            }
        }

        // Установка слушателя на картинку для изменения её размеров по желанию пользователя
        binding.pODImageView.setOnClickListener {
            if (!mainActivity.getIsBlockingOtherFABButtons()) {
                val set = TransitionSet()
                    .addTransition(ChangeBounds())
                    .addTransition(ChangeImageTransform())
                TransitionManager.beginDelayedTransition(binding.mainConstraintLayout, set)
                when (typeChangeImage++) {
                    0 -> binding.pODImageView.scaleType = ImageView.ScaleType.CENTER_CROP
                    1 -> binding.pODImageView.scaleType = ImageView.ScaleType.FIT_XY
                    2 -> binding.pODImageView.scaleType = ImageView.ScaleType.MATRIX
                    3 -> binding.pODImageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
                    4 -> binding.pODImageView.scaleType = ImageView.ScaleType.FIT_END
                    5 -> binding.pODImageView.scaleType = ImageView.ScaleType.FIT_START
                    6 -> binding.pODImageView.scaleType = ImageView.ScaleType.CENTER
                    7 -> binding.pODImageView.scaleType = ImageView.ScaleType.FIT_CENTER
                    else -> binding.pODImageView.scaleType = ImageView.ScaleType.FIT_CENTER
                }
                if (typeChangeImage > 7) typeChangeImage = 0
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