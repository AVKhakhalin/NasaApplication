package com.example.nasaapplication.ui.fragments.contents

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProviders
import com.example.nasaapplication.R
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

        // Установка прозрачного фона для элемента Webview
        binding.webViewContainer.setBackgroundColor(Color.TRANSPARENT)
        // Установка слушателя при нажатии на кнопку поиска в "Википедии"
        binding.inputWikiField.setEndIconOnClickListener {
            if ((binding.inputWikiFieldText.text != null) &&
                (binding.inputWikiFieldText.text!!.length <=
                        binding.inputWikiField.counterMaxLength)) {
                    showUrlInWiki("${ConstantsUi.WIKI_URL}${
                                binding.inputWikiFieldText.text.toString()}")
            }
        }
    }

    fun showUrlInWiki(urlString:String){
        val url = URL(urlString)
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
}