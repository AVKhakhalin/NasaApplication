package com.example.nasaapplication.ui.fragments.contents

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProviders
import com.example.nasaapplication.R
import com.example.nasaapplication.controller.ConstantsController
import com.example.nasaapplication.controller.navigation.contents.NavigationContent
import com.example.nasaapplication.controller.navigation.dialogs.NavigationDialogs
import com.example.nasaapplication.controller.observers.viewmodels.POD.PODViewModel
import com.example.nasaapplication.databinding.FragmentSearchInWikiBinding
import com.example.nasaapplication.ui.ConstantsUi
import com.example.nasaapplication.ui.activities.MainActivity
import com.example.nasaapplication.ui.utils.ViewBindingFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.net.URL
import java.util.stream.Collectors
import javax.net.ssl.HttpsURLConnection


class SearchWikiFragment: ViewBindingFragment<FragmentSearchInWikiBinding>(
    FragmentSearchInWikiBinding::inflate) {
    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
    // Navigations
    private lateinit var navigationDialogs: NavigationDialogs
    private lateinit var navigationContent: NavigationContent
    // ViewModel
    private val viewModel: PODViewModel by lazy {
        ViewModelProviders.of(this).get(PODViewModel::class.java)
    }
    // BottomSheet
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    // MainActivity
    private lateinit var mainActivity: MainActivity
    // Анимация появления результирующих данных
    private val durationAnimation: Long = 1000
    private val transparientValue: Float = 0f
    private val notTransparientValue: Float = 1f
    //endregion

    companion object {
        fun newInstance() = SearchWikiFragment()
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

    //region МЕТОДЫ РАБОТЫ С BottomSheet
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Установка прозрачного фона для элемента Webview
        binding.webViewContainer.setBackgroundColor(Color.TRANSPARENT)
        // Установка слушателя при нажатии на кнопку поиска в "Википедии"
        binding.inputWikiField.setEndIconOnClickListener {
            if (!mainActivity.getIsBlockingOtherFABButtons()) {
                if ((binding.inputWikiFieldText.text != null) &&
                (binding.inputWikiFieldText.text!!.length <=
                        binding.inputWikiField.counterMaxLength)) {
                    showUrlInWiki(
                        "${ConstantsUi.WIKI_URL}${
                            binding.inputWikiFieldText.text.toString()}")
                    // Сохранение запроса в "Избранное"
                    mainActivity.setListFavoriteDataSearchRequest(
                        "${binding.inputWikiFieldText.text.toString()}")
                    mainActivity.setListFavoriteDataTypeSource(
                        ConstantsController.SEARCH_WIKI_FRAGMENT_INDEX)
                    mainActivity.setListFavoriteDataPriority(ConstantsUi.PRIORITY_LOW)
                    mainActivity.setListFavoriteDataLinkSource("${ConstantsUi.WIKI_URL}${
                        binding.inputWikiFieldText.text.toString()}")
                    mainActivity.setListFavoriteDataTitle(
                        "${binding.inputWikiFieldText.text.toString()}")
                }
            }
        }
        // Установка слушателя на завершение загрузки результирующих данных в Web View
        binding.webViewContainer.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                // Анимация появления Web View с результатами поиска
                animatedShowWebView()
            }
        }
    }

    fun showUrlInWiki(urlString: String){
        val url = URL(urlString)
        binding.webViewContainer.alpha = transparientValue
        Thread{
            val urlConnection = url.openConnection() as HttpsURLConnection
            urlConnection.requestMethod = ConstantsUi.SHOWURLINWIKI_METHOD_NAME
            urlConnection.readTimeout = ConstantsUi.SHOWURLINWIKI_READ_TIME_OUT
            var reader: BufferedReader? = null
            try {
                reader = BufferedReader(InputStreamReader(urlConnection.inputStream))
            } catch (exception: FileNotFoundException) {
                urlConnection.disconnect()
            }
            if (reader != null) {
                val result = getLines(reader)
                // Сохранение результата запроса в "Избранное"
                mainActivity.setListFavoriteDataDescription(result)
                // Отображение результата запроса
                val handler = Handler(Looper.getMainLooper())
                handler.post {
                    binding.webViewContainer.loadDataWithBaseURL(
                        null,
                        result,
                        ConstantsUi.SHOWURLINWIKI_TEXT_CHARSER,
                        ConstantsUi.SHOWURLINWIKI_ENCODING,
                        null)
                }

            } else {
                // Сохранение результата запроса в "Избранное"
                mainActivity.setListFavoriteDataDescription(
                    resources.getString(R.string.error_wiki_empty_request))
                // Отображение сообщения об отсутствии результата по запросу
                val handler = Handler(Looper.getMainLooper())
                handler.post {
                    binding.webViewContainer.loadDataWithBaseURL(
                        null,
                        resources.getString(R.string.error_wiki_empty_request),
                        ConstantsUi.SHOWURLINWIKI_TEXT_CHARSER,
                        ConstantsUi.SHOWURLINWIKI_ENCODING,
                        null)
                }
            }
            urlConnection.disconnect()
        }.start()
    }

    @RequiresApi(Build.VERSION_CODES.N) // TODO: Доработать, заменить на метод, независящий от версии
    private fun getLines(reader: BufferedReader): String {
        return reader.lines().collect(Collectors.joining("\n"))
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
}