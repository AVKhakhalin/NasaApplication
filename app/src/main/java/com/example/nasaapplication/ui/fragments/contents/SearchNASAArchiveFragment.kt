package com.example.nasaapplication.ui.fragments.contents

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.nasaapplication.controller.observers.viewmodels.NASAArchive.NASAArchiveData
import com.example.nasaapplication.controller.observers.viewmodels.NASAArchive.NASAArchiveViewModel
import com.example.nasaapplication.controller.observers.viewmodels.POD.PODData
import com.example.nasaapplication.databinding.FragmentSearchInNasaArchiveBinding
import com.example.nasaapplication.ui.ConstantsUi
import com.example.nasaapplication.ui.activities.MainActivity
import com.example.nasaapplication.ui.utils.ViewBindingFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior

class SearchNASAArchiveFragment: ViewBindingFragment<FragmentSearchInNasaArchiveBinding>(
    FragmentSearchInNasaArchiveBinding::inflate) {
    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
    // Navigations
    private lateinit var navigationDialogs: NavigationDialogs
    private lateinit var navigationContent: NavigationContent
    // ViewModel
    private val viewModel: NASAArchiveViewModel by lazy {
        ViewModelProviders.of(this).get(NASAArchiveViewModel::class.java)
    }
    // BottomSheet
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    // MainActivity
    private lateinit var mainActivity: MainActivity
    //endregion

    companion object {
        fun newInstance() = SearchNASAArchiveFragment()
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
        // Установка слушателя при нажатии на кнопку поиска в "Википедии"
        binding.inputNasaField.setEndIconOnClickListener {
            if ((binding.inputNasaFieldText.text != null) &&
                (binding.inputNasaFieldText.text!!.length <=
                        binding.inputNasaField.counterMaxLength)) {
                            sendRequestToNASAArchive(
                                "${binding.inputNasaFieldText.text.toString()}")
            }
        }

        // Скрыть все графические элементы до получения запроса
        binding.searchInNasaArchiveLoadingLayout.visibility = View.INVISIBLE
        binding.searchInNasaArchiveImageView.visibility = View.INVISIBLE

        super.onViewCreated(view, savedInstanceState)
    }

    private fun renderData(data: NASAArchiveData) {
        // TODO: Доработать вывод списка полученных данных
        //  и предложение пользователю на выбор, что просмотреть
        when (data) {
            is NASAArchiveData.Success -> {
                val serverResponseData = data.serverResponseData
                if ((serverResponseData.collection != null)
                    && (serverResponseData.collection.items.isNotEmpty())
                    && (serverResponseData.collection.items[0].links.isNotEmpty())) {
                    val url = serverResponseData.collection.items[0].links[0].href
                    if (url.isNullOrEmpty()) {
                        //showError("Сообщение, что ссылка пустая")
                        toast(ConstantsUi.ERROR_LINK_EMPTY)
                    } else {
                        //showSuccess()
                        binding.searchInNasaArchiveImageView.load(url) {
                            lifecycle(this@SearchNASAArchiveFragment)
                            error(R.drawable.ic_load_error_vector)
                        }
                        // Показать описание фотографии по запрошенному событию
                        binding.searchInNasaArchiveTitleTextView.text =
                            serverResponseData.collection.items[0].data[0].title
                        binding.searchInNasaArchiveDescriptionTextView.text =
                            serverResponseData.collection.items[0].data[0].description

                        binding.fragmentSearchInNasaArchiveGroupElements.visibility = View.VISIBLE
                        binding.searchInNasaArchiveLoadingLayout.visibility = View.INVISIBLE
                    }
                } else {
                    binding.searchInNasaArchiveLoadingLayout.visibility = View.INVISIBLE
                    binding.searchInNasaArchiveTitleTextView.text =
                        ConstantsController.ERROR_EMPTY_DOWNLOAD_DATES
                }
            }
            is NASAArchiveData.Loading -> {
                // Показать картинку загрузки
                binding.searchInNasaArchiveLoadingLayout.visibility = View.VISIBLE
                binding.searchInNasaArchiveImageView.visibility = View.INVISIBLE
            }
            is NASAArchiveData.Error -> {
                // Показать ошибку в случае загрузки картинки (showError(data.error.message))
                toast(data.error.message)
            }
        }
    }

    // Метод для отображения сообщения в виде Toast
    private fun Fragment.toast(string: String?) {
        Toast.makeText(context, string, Toast.LENGTH_LONG).apply {
            setGravity(Gravity.BOTTOM, 0, 250)
            show()
        }
    }

    // Отправка запроса в NASA-архив
    private fun sendRequestToNASAArchive(request: String) {
        val twoSpaces: String = "  "
        val oneSpace: String = " "
        val replaceSpaceSymbol: String = "%"
        var finalRequest: String = request
        while (finalRequest.indexOf(twoSpaces) >= 0) {
            finalRequest = finalRequest.replace(twoSpaces, oneSpace)
        }
        finalRequest = finalRequest.trimStart()
        finalRequest = finalRequest.trimEnd()
        finalRequest = finalRequest.replace(oneSpace, replaceSpaceSymbol)
        if (finalRequest.isNotEmpty()) {
            viewModel.getData(finalRequest)
                .observe(viewLifecycleOwner, Observer<NASAArchiveData> { renderData(it) })
        }
    }
}