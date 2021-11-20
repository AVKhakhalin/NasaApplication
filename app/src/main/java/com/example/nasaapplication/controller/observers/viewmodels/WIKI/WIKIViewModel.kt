package com.example.nasaapplication.controller.observers.viewmodels.WIKI

import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nasaapplication.R
import com.example.nasaapplication.Constants
import com.example.nasaapplication.domain.logic.Favorite
import com.example.nasaapplication.repository.facadeuser.POD.PODRetrofitImpl
import com.example.nasaapplication.repository.facadeuser.POD.PODServerResponseData
import com.example.nasaapplication.ui.activities.MainActivity
import com.example.nasaapplication.ui.fragments.contents.SearchWikiFragment
import okhttp3.internal.toHexString
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.net.URL
import java.util.*
import java.util.stream.Collectors
import javax.net.ssl.HttpsURLConnection

class WIKIViewModel (
    val searchWikiFragment: SearchWikiFragment,
    var searchWikiFavorite: Favorite,
    val transparientValue: Float
    ) {

    fun showUrlInWiki(urlString: String, mainActivity: MainActivity?){
        mainActivity?.let { mainActivity ->
            // Сохранение запроса в "Избранное"
            mainActivity.getUIObserversManager().setListFavoriteDataSearchRequest(
                "${searchWikiFragment.binding.inputWikiFieldText.text.toString()}")
            mainActivity.getUIObserversManager().setListFavoriteDataTypeSource(
                Constants.SEARCH_WIKI_FRAGMENT_INDEX)
            mainActivity.getUIObserversManager()
                .setListFavoriteDataPriority(Constants.PRIORITY_LOW)
            mainActivity.getUIObserversManager()
                .setListFavoriteDataLinkSource("${Constants.WIKI_URL}${
                searchWikiFragment.binding.inputWikiFieldText.text.toString()}")
            mainActivity.getUIObserversManager().setListFavoriteDataTitle(
                "${searchWikiFragment.binding.inputWikiFieldText.text.toString()}")
            searchWikiFavorite.setSearchRequest(
                "${searchWikiFragment.binding.inputWikiFieldText.text.toString()}")
            searchWikiFavorite.setTypeSource(
                Constants.SEARCH_WIKI_FRAGMENT_INDEX)
            searchWikiFavorite.setPriority(Constants.PRIORITY_LOW)
            searchWikiFavorite.setLinkSource("${Constants.WIKI_URL}${
                searchWikiFragment.binding.inputWikiFieldText.text.toString()}")
            searchWikiFavorite.setTitle(
                "${searchWikiFragment.binding.inputWikiFieldText.text.toString()}")

            // Отображение результата запроса
            val url = URL(urlString)
            searchWikiFragment.binding.webViewContainer.alpha = transparientValue
            Thread {
                val urlConnection = url.openConnection() as HttpsURLConnection
                urlConnection.requestMethod = Constants.SHOWURLINWIKI_METHOD_NAME
                urlConnection.readTimeout = Constants.SHOWURLINWIKI_READ_TIME_OUT
                var reader: BufferedReader? = null
                try {
                    reader = BufferedReader(InputStreamReader(urlConnection.inputStream))
                } catch (exception: FileNotFoundException) {
                    urlConnection.disconnect()
                }
                if (reader != null) {
                    mainActivity.getThemeColor()?.let {
                        val result = "${Constants.WEBVIEW_TEXT_HEADER_SUCCESS_BEGIN}${
                            it.getColorPrimaryVariantTypedValue().toHexString()
                                .subSequence(2, 8)}${Constants.WEBVIEW_TEXT_HEADER_SUCCESS_END}${
                            getLines(reader)}${Constants.WEBVIEW_TEXT_FOOTER}"
                        // Сохранение результата запроса в "Избранное"
                        mainActivity.getUIObserversManager().setListFavoriteDataDescription(result)
                        searchWikiFavorite.setDescription(result)

                        // Отображение результата запроса
                        val handler = Handler(Looper.getMainLooper())
                        handler.post {
                            searchWikiFragment.binding.webViewContainer.loadDataWithBaseURL(
                                null,
                                result,
                                Constants.SHOWURLINWIKI_TEXT_CHARSER,
                                Constants.SHOWURLINWIKI_ENCODING,
                                null)
                        }
                    }
                } else {
                    mainActivity.getThemeColor()?.let {
                        // Сохранение результата запроса в "Избранное"
                        mainActivity.getUIObserversManager().setListFavoriteDataDescription(
                            mainActivity.resources.getString(R.string.error_wiki_empty_request)
                                .replace("<Br><Br>"," "))
                        searchWikiFavorite.setDescription(
                            mainActivity.resources.getString(R.string.error_wiki_empty_request)
                                .replace("<Br><Br>"," "))
                        // Отображение сообщения об отсутствии результата по запросу
                        val handler = Handler(Looper.getMainLooper())
                        val result = "${Constants.WEBVIEW_TEXT_HEADER_NOTSUCCESS_BEGIN}${
                            it.getColorPrimaryVariantTypedValue().toHexString().subSequence(2, 8)}${
                            Constants.WEBVIEW_TEXT_HEADER_NOTSUCCESS_END}${
                            mainActivity.resources.getString(R.string.error_wiki_empty_request)}${
                            Constants.WEBVIEW_TEXT_FOOTER}"
                        handler.post {
                            searchWikiFragment.binding.webViewContainer.loadDataWithBaseURL(
                                null,
                                result,
                                Constants.SHOWURLINWIKI_TEXT_CHARSER,
                                Constants.SHOWURLINWIKI_ENCODING,
                                null)
                        }
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
}