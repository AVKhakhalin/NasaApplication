package com.example.nasaapplication.ui.fragments.contents

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.transition.ChangeBounds
import android.transition.ChangeImageTransform
import android.transition.TransitionManager
import android.transition.TransitionSet
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import coil.load
import com.example.nasaapplication.R
import com.example.nasaapplication.Constants
import com.example.nasaapplication.controller.observers.viewmodels.POD.PODData
import com.example.nasaapplication.controller.observers.viewmodels.POD.PODViewModel
import com.example.nasaapplication.databinding.FragmentDayPhotoBinding
import com.example.nasaapplication.domain.logic.Favorite
import com.example.nasaapplication.ui.activities.MainActivity
import com.example.nasaapplication.ui.utils.ColorUnderlineSpan
import com.example.nasaapplication.ui.utils.ViewBindingFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import java.lang.Exception
import java.util.*

class DayPhotoFragment:
    ViewBindingFragment<FragmentDayPhotoBinding>(FragmentDayPhotoBinding::inflate) {
    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
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
    // Анимация изменения размеров картики
    private var typeChangeImage: Int = 0
    // Анимация появления результирующих данных
    private val durationAnimation: Long = 800
    private val transparientValue: Float = 0f
    private val notTransparientValue: Float = 1f
    // MainActivity
    private var mainActivity: MainActivity? = null
    //endregion

    companion object {
        fun newInstance() = DayPhotoFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
        viewModel.setMainActivity(mainActivity)
    }

    override fun onResume() {
        // Начальная настройка фрагмента
        mainActivity?.let { it.getUIObserversManager().showSearchDayPhotoFragment() }
        super.onResume()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getDate(0)
        viewModel.getData(curDate)
            .observe(viewLifecycleOwner, Observer<PODData> { renderData(it) })
    }

    private fun renderData(data: PODData) {
        mainActivity?.let { mainActivity ->
            when (data) {
                is PODData.Success -> {
                    val serverResponseData = data.serverResponseData
                    val url = serverResponseData.url
                    if (url.isNullOrEmpty()) {
                        //showError("Сообщение, что ссылка пустая")
                        mainActivity.toast("${mainActivity.resources.getString(
                            R.string.error)}: ${mainActivity.resources.getString(R.string.error_empty_link)}")
                    } else {
                        //showSuccess()
                        // Сохранение данных для списка "Избранное"
                        mainActivity.getUIObserversManager()
                            .setListFavoriteDataSearchRequest(curDate)
                        mainActivity.getUIObserversManager()
                            .setListFavoriteDataLinkSource(viewModel.getRequestUrl())
                        mainActivity.getUIObserversManager()
                            .setListFavoriteDataTitle(serverResponseData.title ?: "")
                        mainActivity.getUIObserversManager().setListFavoriteDataDescription(
                            serverResponseData.explanation ?: "")
                        mainActivity.getUIObserversManager().setListFavoriteDataLinkImage(url)
                        mainActivity.getUIObserversManager().setListFavoriteDataTypeSource(
                            Constants.DAY_PHOTO_FRAGMENT_INDEX)
                        mainActivity.getUIObserversManager()
                            .setListFavoriteDataPriority(Constants.PRIORITY_LOW)

                        mainActivity.getUIObserversManager()
                            .getDayPhotoFavorite().setSearchRequest(curDate)
                        mainActivity.getUIObserversManager()
                            .getDayPhotoFavorite().setLinkSource(viewModel.getRequestUrl())
                        mainActivity.getUIObserversManager()
                            .getDayPhotoFavorite().setTitle(serverResponseData.title ?: "")
                        mainActivity.getUIObserversManager()
                            .getDayPhotoFavorite()
                            .setDescription(serverResponseData.explanation ?: "")
                        mainActivity.getUIObserversManager().getDayPhotoFavorite().setLinkImage(url)
                        mainActivity.getUIObserversManager()
                            .getDayPhotoFavorite().setTypeSource(Constants.DAY_PHOTO_FRAGMENT_INDEX)
                        mainActivity.getUIObserversManager()
                            .getDayPhotoFavorite().setPriority(Constants.PRIORITY_LOW)

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
                        // Создание текста с красным подчёркиванием
                        serverResponseData.title?.let {
                            try {
                                val spannable = SpannableString(serverResponseData.title)
                                mainActivity.getThemeColor()?.let {  themeColor ->
                                    spannable.setSpan(ColorUnderlineSpan(mainActivity,
                                        themeColor.getSecondaryVariantTypedValue(),0,
                                            it.length),0, it.length,
                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                }
                                bottomSheetDescriptionTitle.text = spannable
                            } catch (errorMessage: Exception) {
                                bottomSheetDescriptionTitle.text = serverResponseData.title
                                Toast.makeText(mainActivity.applicationContext,
                                    "${mainActivity.resources.getString(R.string.error)}: ${
                                    mainActivity.resources.getString(R.string.
                                    error_underline_creation_for_title)}",
                                    Toast.LENGTH_LONG).show()
                            }
                        }
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

                        // Метод проверки наличия текущей информации в списке "Избранное"
                        // и отрисовка соответствующего значка сердца (контурная или с заливкой)
                        mainActivity.getUIObserversManager().checkAndChangeHeartIconState()
                    }
                }
                is PODData.Loading -> {
                    //showLoading()
                    binding.pODLoadingLayout.visibility = View.VISIBLE
                    binding.pODImageView.visibility = View.INVISIBLE
                }
                is PODData.Error -> {
                    //showError(data.error.message)
                    mainActivity.toast(data.error.message)
                }
            }
        }
    }

    //region МЕТОДЫ РАБОТЫ С BottomSheet
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity?.let { mainActivity ->
            setBottomSheetBehavior(view.findViewById(R.id.bottom_sheet_container))
            bottomSheetDescriptionTitle = view.findViewById(R.id.bottom_sheet_description_title)
            bottomSheetDescriptionText = view.findViewById(R.id.bottom_sheet_description_text)
            // Программная установка нового шрифта для описания новости
            bottomSheetDescriptionText.typeface =
                Typeface.createFromAsset(mainActivity.assets, "font/RobotoFlex_Regular.ttf")

            // Установка текущей даты в заголовке над фотографией
            binding.fragmentDayPhotoCurrentDateTextView.text =
                "${mainActivity.resources.getString(R.string.photo_of_the_day_text)} ${
                    getDate(0)}"

            // Инициализация текстового блока для отображения текущей даты
            currentDateTextView = binding.fragmentDayPhotoCurrentDateTextView

            // Инициализация и настройка Chip-кнопок
            if (currentDateTextView != null) {
                buttonChipToday = binding.buttonTodayPhoto
                buttonChipToday?.let {
                    it.setOnClickListener {
                        if (!mainActivity.getUIObserversManager().getIsBlockingOtherFABButtons()) {
                            mainActivity.getUIObserversManager().clickOnDayButton()
                            // Получение данных о картинке сегодняшего дня
                            currentDateTextView?.let { it.text =
                                "${mainActivity.resources.getString(
                                R.string.photo_of_the_day_text)} ${getDate(0)}" }
                            viewModel.getData(curDate)
                                .observe(viewLifecycleOwner, Observer<PODData> { renderData(it) })
                        }
                    }
                }
                buttonChipYesterday = binding.buttonYesterdayPhoto
                buttonChipYesterday?.let {
                    it.setOnClickListener {
                        if (!mainActivity.getUIObserversManager().getIsBlockingOtherFABButtons()) {
                            mainActivity.getUIObserversManager().clickOnDayButton()
                            // Получение данных о картинке вчерашнего дня
                            currentDateTextView?.let { it.text =
                                "${mainActivity.resources.getString(
                                    R.string.photo_of_the_day_text)} ${getDate(-1)}" }
                            viewModel.getData(curDate)
                                .observe(viewLifecycleOwner, Observer<PODData> { renderData(it) })
                        }
                    }
                }
                buttonChipBeforeYesterday = binding.buttonBeforeYesterdayPhoto
                buttonChipBeforeYesterday?.let {
                    it.setOnClickListener {
                        if (!mainActivity.getUIObserversManager().getIsBlockingOtherFABButtons()) {
                            mainActivity.getUIObserversManager().clickOnDayButton()
                            // Получение данных о картинке позавчерашнего дня
                            currentDateTextView?.let { it.text =
                                "${mainActivity.resources.getString(
                                    R.string.photo_of_the_day_text)} ${getDate(-2)}" }
                            viewModel.getData(curDate)
                                .observe(viewLifecycleOwner, Observer<PODData> { renderData(it) })
                        }
                    }
                }
            }

            // Установка слушателя на картинку для изменения её размеров по желанию пользователя
            binding.pODImageView.setOnClickListener {
                if (!mainActivity.getUIObserversManager().getIsBlockingOtherFABButtons()) {
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

    // Метод установки элемента из списка "Избранное" для просмотра в данном фрагменте
    fun setAndShowFavoriteData(favoriteData: Favorite) {
        mainActivity?.let { mainActivity ->
            // Отображение элемента из списка "Избранное" для просмотра в данном фрагменте
            favoriteData?.let { favoriteData ->
                if (!mainActivity.getUIObserversManager().getIsBlockingOtherFABButtons()) {
                    curDate = favoriteData.getSearchRequest()
                    currentDateTextView?.let {
                        it.text = "${mainActivity.resources.getString(
                            R.string.photo_of_the_day_text)} ${curDate.substring(8, 10)}.${
                            curDate.substring(5, 7)}.${curDate.substring(0, 4)}"
                    }
                    viewModel.getData(favoriteData.getSearchRequest())
                        .observe(viewLifecycleOwner, Observer<PODData> { renderData(it) })
                }
            }
        }
    }
}