package com.example.nasaapplication.ui.fragments.contents

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.nasaapplication.Constants
import com.example.nasaapplication.controller.observers.viewmodels.WIKI.WIKIViewModel
import com.example.nasaapplication.databinding.FragmentSearchInWikiBinding
import com.example.nasaapplication.domain.logic.Favorite
import com.example.nasaapplication.ui.activities.MainActivity
import com.example.nasaapplication.ui.utils.ViewBindingFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior

class SearchWikiFragment: ViewBindingFragment<FragmentSearchInWikiBinding>(
    FragmentSearchInWikiBinding::inflate) {
    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
    // BottomSheet
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    // Анимация появления результирующих данных
    private val durationAnimation: Long = 1000
    private val transparientValue: Float = 0f
    private val notTransparientValue: Float = 1f
    // MainActivity
    private var mainActivity: MainActivity? = null
    // ViewModel
    private val viewModel: WIKIViewModel =
        WIKIViewModel(this, transparientValue)
    //endregion

    companion object {
        fun newInstance() = SearchWikiFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
        // Задание searchWikiFavorite в классе viewModel
        mainActivity?.let {
            viewModel.setSearchWikiFavorite(it.getUIObserversManager().getSearchWikiFavorite())
        }
    }

    override fun onResume() {
        // Начальная настройка фрагмента
        mainActivity?.let { it.getUIObserversManager().showSearchWikiFragment() }
        super.onResume()
    }

    //region МЕТОДЫ РАБОТЫ С BottomSheet
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity?.let { mainActivity ->
            // Установка прозрачного фона для элемента WebView
            binding.webViewContainer.setBackgroundColor(Color.TRANSPARENT)
            // Установка слушателя при нажатии на кнопку поиска в "Википедии"
            binding.inputWikiField.setEndIconOnClickListener {
                if (!mainActivity.getUIObserversManager().getIsBlockingOtherFABButtons()) {
                    mainActivity.getUIObserversManager().clickOnSearchInWIKI()
                    // Получение новой информации из "Википедии"
                    binding.inputWikiFieldText.text?.let { text ->
                        if ((text != null) &&
                            (text.length <= binding.inputWikiField.counterMaxLength)) {
                                viewModel.showUrlInWiki("${Constants.WIKI_URL}${
                                    binding.inputWikiFieldText.text.toString()}", mainActivity)
                        }
                    }
                }
            }
            // Установка слушателя на завершение загрузки результирующих данных в WebView
            binding.webViewContainer.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    // Анимация появления Web View с результатами поиска
                    animatedShowWebView()
                    // Метод проверки наличия текущей информации в списке "Избранное"
                    // и отрисовка соответствующего значка сердца (контурная или с заливкой)
                    mainActivity.getUIObserversManager().checkAndChangeHeartIconState()
                }
            }
        }
    }

    private fun animatedShowWebView() {
        // Анимация появления Web View с результатами поиска
        binding.webViewContainer.animate()
            .alpha(notTransparientValue)
            .setDuration(durationAnimation)
            .setListener(object: AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    binding.webViewContainer.isClickable = true
                }
            })
    }

    // Метод установки элемента из списка "Избранное" для просмотра в данном фрагменте
    fun setAndShowFavoriteData(favoriteData: Favorite) {
        favoriteData?.let {
            binding.inputWikiFieldText.setText(it.getSearchRequest())
            viewModel.showUrlInWiki(it.getLinkSource(), mainActivity)
        }
    }
}