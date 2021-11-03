package com.example.nasaapplication.ui.fragments.contents

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nasaapplication.R
import com.example.nasaapplication.controller.ConstantsController
import com.example.nasaapplication.controller.navigation.contents.NavigationContent
import com.example.nasaapplication.controller.navigation.dialogs.NavigationDialogs
import com.example.nasaapplication.controller.observers.viewmodels.NASAArchive.NASAArchiveData
import com.example.nasaapplication.controller.observers.viewmodels.NASAArchive.NASAArchiveViewModel
import com.example.nasaapplication.databinding.FragmentSearchInNasaArchiveBinding
import com.example.nasaapplication.repository.facadeuser.NASAArchive.NASAArchiveServerResponseItems
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
    // Recycler View
    private var recyclerView: RecyclerView? = null
    private var newNASAArchiveEntityList: MutableList<String> = mutableListOf()
    private var entitiesLinks: MutableList<String> = mutableListOf()
    private var entitiesTexts: MutableList<String> = mutableListOf()
    private var isRecyclerViewWindowHide: Boolean = true
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

    //region МЕТОДЫ РАБОТЫ С RECYCLER VIEW (СПИСКОМ НАЙДЕННЫХ В АРХИВЕ NASA ПО ЗАПРОСУ ЗАПИСЕЙ)
    private fun initViewRecyclerViewList(view: View) {
        recyclerView = view.findViewById(R.id.fragment_search_in_nasa_archive_recycler_view)
        recyclerView?.let {
            it.layoutManager = LinearLayoutManager(requireActivity())
            it.adapter =
                SearchNASAArchiveFragmentAdapter(
                    newNASAArchiveEntityList,
                    entitiesLinks,
                    entitiesTexts,
                    isRecyclerViewWindowHide,
                    this)
        }
    }
    private fun updateRecycleViewList(
        newFoundedItemsInNASAArchive: List<NASAArchiveServerResponseItems>) {
        newNASAArchiveEntityList.clear()
        newFoundedItemsInNASAArchive.forEach {
            newNASAArchiveEntityList.add(it.data[0].title.toString())
            entitiesLinks.add(it.links[0].href.toString())
            entitiesTexts.add(it.data[0].description.toString())
        }
        if (newFoundedItemsInNASAArchive.isNotEmpty()) {
            // Отображение контейнера с результатами отображения Recycler View списка найденной
            // в архиве NASA информации
            binding.nasaArchiveEntityListContainer.visibility = View.VISIBLE
            showRecyclerViewWindowWithResults()
        }
        recyclerView?.adapter?.notifyDataSetChanged()
    }
    fun setIsRecyclerViewWindowHide(isRecyclerViewWindowHide: Boolean) {
        this.isRecyclerViewWindowHide = isRecyclerViewWindowHide
    }
    //endregion

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

        // Настройка Recycler View списка найденной в архиве NASA информации
        initViewRecyclerViewList(view)

        // Скрытие контейнера с результатами отображения Recycler View списка найденной
        // в архиве NASA информации
        binding.nasaArchiveEntityListContainer.visibility = View.INVISIBLE

        // Настройка кнопки отображения Recycler View списка найденной в архиве NASA информации
        binding.nasaArchiveEntityListContainerTouchableBorder.setOnClickListener {
            showRecyclerViewWindowWithResults()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun showRecyclerViewWindowWithResults() {
        val constraintLayout = binding.nasaArchiveEntityListContainer
        val timeLayoutParams: (ConstraintLayout.LayoutParams) =
            constraintLayout.layoutParams as ConstraintLayout.LayoutParams
        timeLayoutParams.constrainedWidth = !isRecyclerViewWindowHide
        isRecyclerViewWindowHide = !isRecyclerViewWindowHide
        if (isRecyclerViewWindowHide) {
            binding.fragmentSearchInNasaArchiveGroupElements.visibility = View.VISIBLE
            binding.searchInNasaArchiveLoadingLayout.visibility = View.INVISIBLE
        } else {
            binding.fragmentSearchInNasaArchiveGroupElements.visibility = View.INVISIBLE
            binding.searchInNasaArchiveLoadingLayout.visibility = View.INVISIBLE
        }
        constraintLayout.layoutParams = timeLayoutParams
        binding.fragmentSearchInNasaArchiveRecyclerView.visibility = View.VISIBLE
    }

    private fun renderData(data: NASAArchiveData) {
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
                        // Обновление списка найденных элементов в Recycler View
                        updateRecycleViewList(serverResponseData.collection.items)
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