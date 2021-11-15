package com.example.nasaapplication.ui.fragments.contents

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProviders
import com.example.nasaapplication.R
import com.example.nasaapplication.controller.ConstantsController
import com.example.nasaapplication.controller.navigation.contents.NavigationContent
import com.example.nasaapplication.controller.navigation.dialogs.NavigationDialogs
import com.example.nasaapplication.controller.observers.viewmodels.POD.PODViewModel
import com.example.nasaapplication.databinding.FragmentSearchInWikiBinding
import com.example.nasaapplication.domain.logic.Favorite
import com.example.nasaapplication.ui.ConstantsUi
import com.example.nasaapplication.ui.activities.MainActivity
import com.example.nasaapplication.ui.utils.ViewBindingFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import okhttp3.internal.toHexString
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
    // Анимация появления результирующих данных
    private val durationAnimation: Long = 1000
    private val transparientValue: Float = 0f
    private val notTransparientValue: Float = 1f
    // Данные для списка "Избранное"
    private var searchWikiFavorite: Favorite = Favorite()
    // MainActivity
    private var mainActivity: MainActivity? = null
    //endregion

    companion object {
        fun newInstance() = SearchWikiFragment()
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

    override fun onResume() {
        // Начальная настройка фрагмента
        initialSettingFragment()

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
                if (!mainActivity.getIsBlockingOtherFABButtons()) {
                    // Очистка текущей информации для добавления в список "Избранное"
                    mainActivity.setListFavoriteEmptyData()
                    // Изменение вида иконки сердца на контурное
                    mainActivity.changeHeartIconState(mainActivity, false, true)
                    // Получение новой информации из "Википедии"
                    if ((binding.inputWikiFieldText.text != null) &&
                    (binding.inputWikiFieldText.text!!.length <=
                            binding.inputWikiField.counterMaxLength)) {
                        showUrlInWiki(
                            "${ConstantsUi.WIKI_URL}${
                                binding.inputWikiFieldText.text.toString()}")
                    }
                }
            }
            // Установка слушателя на завершение загрузки результирующих данных в Web View
            binding.webViewContainer.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    // Анимация появления Web View с результатами поиска
                    animatedShowWebView()
                    // Метод проверки наличия текущей информации в списке "Избранное"
                    // и отрисовка соответствующего значка сердца (контурная или с заливкой)
                    checkAndChangeHeartIconState()
                }
            }
        }
    }

    fun showUrlInWiki(urlString: String){
        mainActivity?.let { mainActivity ->
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
            searchWikiFavorite.setSearchRequest(
                "${binding.inputWikiFieldText.text.toString()}")
            searchWikiFavorite.setTypeSource(
                ConstantsController.SEARCH_WIKI_FRAGMENT_INDEX)
            searchWikiFavorite.setPriority(ConstantsUi.PRIORITY_LOW)
            searchWikiFavorite.setLinkSource("${ConstantsUi.WIKI_URL}${
                binding.inputWikiFieldText.text.toString()}")
            searchWikiFavorite.setTitle(
                "${binding.inputWikiFieldText.text.toString()}")
            // Отображение результата запроса
            val url = URL(urlString)
            binding.webViewContainer.alpha = transparientValue
            Thread {
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
                    val result = "${ConstantsUi.WEBVIEW_TEXT_HEADER_SUCCESS_BEGIN}${
                        mainActivity.getColorPrimaryVariantTypedValue().data.toHexString()
                            .subSequence(2, 8)}${ConstantsUi.WEBVIEW_TEXT_HEADER_SUCCESS_END}${
                                getLines(reader)}${ConstantsUi.WEBVIEW_TEXT_FOOTER}"
                    // Сохранение результата запроса в "Избранное"
                    mainActivity.setListFavoriteDataDescription(result)
                    searchWikiFavorite.setDescription(result)

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
                        resources.getString(R.string.error_wiki_empty_request)
                            .replace("<Br><Br>"," "))
                    searchWikiFavorite.setDescription(
                        resources.getString(R.string.error_wiki_empty_request)
                            .replace("<Br><Br>"," "))
                    // Отображение сообщения об отсутствии результата по запросу
                    val handler = Handler(Looper.getMainLooper())
                    val result = "${ConstantsUi.WEBVIEW_TEXT_HEADER_NOTSUCCESS_BEGIN}${
                        mainActivity.getColorPrimaryVariantTypedValue().data.toHexString()
                            .subSequence(2, 8)}${ConstantsUi.WEBVIEW_TEXT_HEADER_NOTSUCCESS_END}${
                                resources.getString(R.string.error_wiki_empty_request)}${
                                    ConstantsUi.WEBVIEW_TEXT_FOOTER}"
                    handler.post {
                        binding.webViewContainer.loadDataWithBaseURL(
                            null,
                            result,
                            ConstantsUi.SHOWURLINWIKI_TEXT_CHARSER,
                            ConstantsUi.SHOWURLINWIKI_ENCODING,
                            null)
                    }
                }
                urlConnection.disconnect()
            }.start()
        }
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

    // Метод установки элемента из списка "Избранное" для просмотра в данном фрагменте
    fun setAndShowFavoriteData(favoriteData: Favorite) {
        favoriteData?.let {
            binding.inputWikiFieldText.setText(it.getSearchRequest())
            showUrlInWiki(it.getLinkSource())
        }
    }

    // Метод проверки наличия текущей информации в списке "Избранное"
    // и отрисовка соответствующего значка сердца (контурная или с заливкой)
    private fun checkAndChangeHeartIconState() {
        mainActivity?.let { mainActivity ->
            if (mainActivity.checkSimilarFavoriteData())
                mainActivity.changeHeartIconState(mainActivity, true, false)
            else
                mainActivity.changeHeartIconState(mainActivity, false, true)
        }
    }

    // Метод с начальной настройкой фрагмента
    fun initialSettingFragment() {
        mainActivity?.let { mainActivity ->
            // Очистка текущей информации для "Избранное" при переключении на данный фрагмент
            mainActivity.setListFavoriteDataTypeSource(searchWikiFavorite.getTypeSource())
            mainActivity.setListFavoriteDataTitle(searchWikiFavorite.getTitle())
            mainActivity.setListFavoriteDataDescription(searchWikiFavorite.getDescription())
            mainActivity.setListFavoriteDataLinkSource(searchWikiFavorite.getLinkSource())
            mainActivity.setListFavoriteDataPriority(searchWikiFavorite.getPriority())
            mainActivity.setListFavoriteDataSearchRequest(searchWikiFavorite.getSearchRequest())
            mainActivity.setListFavoriteDataLinkImage(searchWikiFavorite.getLinkImage())
            // Метод проверки наличия текущей информации в списке "Избранное"
            // и отрисовка соответствующего значка сердца (контурная или с заливкой)
            checkAndChangeHeartIconState()
        }
    }
}