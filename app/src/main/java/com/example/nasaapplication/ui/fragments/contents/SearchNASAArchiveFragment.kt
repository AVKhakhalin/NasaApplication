package com.example.nasaapplication.ui.fragments.contents

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.ChangeImageTransform
import android.transition.TransitionManager
import android.transition.TransitionSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.nasaapplication.R
import com.example.nasaapplication.controller.ConstantsController
import com.example.nasaapplication.controller.navigation.contents.NavigationContent
import com.example.nasaapplication.controller.navigation.dialogs.NavigationDialogs
import com.example.nasaapplication.controller.observers.viewmodels.NASAArchive.NASAArchiveData
import com.example.nasaapplication.controller.observers.viewmodels.NASAArchive.NASAArchiveDataViewModel
import com.example.nasaapplication.controller.recyclers.SearchNASAArchiveFragmentAdapter
import com.example.nasaapplication.databinding.FragmentSearchInNasaArchiveBinding
import com.example.nasaapplication.domain.logic.Favorite
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
    private val dataViewModel: NASAArchiveDataViewModel by lazy {
        ViewModelProviders.of(this).get(NASAArchiveDataViewModel::class.java)
    }
    // BottomSheet
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    // Recycler View
    private var recyclerView: RecyclerView? = null
    private var newNASAArchiveEntityList: MutableList<String> = mutableListOf()
    private var entitiesLinks: MutableList<String> = mutableListOf()
    private var entitiesTexts: MutableList<String> = mutableListOf()
    private var isRecyclerViewWindowHide: Boolean = true
    // Анимация изменения размеров картики
    private var typeChangeImage: Int = 0
    // Анимация появления результирующих данных
    private val durationAnimation: Long = 800
    private val transparientValue: Float = 0f
    private val notTransparientValue: Float = 1f
    // Данные для списка "Избранное"
    private var searchNASAArchiveFavorite: Favorite = Favorite()
    // MainActivity
    private var mainActivity: MainActivity? = null
    //endregion

    companion object {
        fun newInstance() = SearchNASAArchiveFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
        //region ПОЛУЧЕНИЕ КЛАССОВ НАВИГАТОРОВ
        mainActivity?.let { it
            navigationDialogs = it.getNavigationDialogs()
            navigationContent = it.getNavigationContent()
        }
        //endregion
    }

    override fun onResume() {
        // Начальная настройка фрагмента
        initialSettingFragment()
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
    private fun updateRecyclerViewList(
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
            showRecyclerViewWindowWithResults(false)
        }
        recyclerView?.adapter?.notifyDataSetChanged()
    }
    fun setIsRecyclerViewWindowHide(isRecyclerViewWindowHide: Boolean) {
        this.isRecyclerViewWindowHide = isRecyclerViewWindowHide
    }
    //endregion

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mainActivity?.let { mainActivity ->
            // Установка слушателя при нажатии на кнопку поиска в архиве NASA
            binding.inputNasaField.setEndIconOnClickListener {
                if (!mainActivity.getIsBlockingOtherFABButtons()) {
                    // Очистка текущей информации для добавления в список "Избранное"
                    mainActivity.setListFavoriteEmptyData()
                    // Изменение вида иконки сердца на контурное
                    mainActivity.changeHeartIconState(mainActivity, false, true)
                    // Получение информации из архива NASA
                    if ((binding.inputNasaFieldText.text != null) &&
                    (binding.inputNasaFieldText.text!!.length <=
                            binding.inputNasaField.counterMaxLength)) {
                        sendRequestToNASAArchive("${binding.inputNasaFieldText.text}")
                    }
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
                if (!mainActivity.getIsBlockingOtherFABButtons())
                    showRecyclerViewWindowWithResults(false)
            }

            // Установка слушателя на картинку для изменения её размеров по желанию пользователя
            binding.searchInNasaArchiveImageView.setOnClickListener {
                if (!mainActivity.getIsBlockingOtherFABButtons()) {
                    val set = TransitionSet()
                        .addTransition(ChangeBounds())
                        .addTransition(ChangeImageTransform())
                    TransitionManager.beginDelayedTransition(
                        binding.searchInNasaArchiveResultContainer, set)
                    when (typeChangeImage++) {
                        0 -> binding.searchInNasaArchiveImageView.scaleType =
                            ImageView.ScaleType.CENTER_CROP
                        1 -> binding.searchInNasaArchiveImageView.scaleType =
                            ImageView.ScaleType.FIT_XY
                        2 -> binding.searchInNasaArchiveImageView.scaleType =
                            ImageView.ScaleType.MATRIX
                        3 -> binding.searchInNasaArchiveImageView.scaleType =
                            ImageView.ScaleType.CENTER_INSIDE
                        4 -> binding.searchInNasaArchiveImageView.scaleType =
                            ImageView.ScaleType.FIT_END
                        5 -> binding.searchInNasaArchiveImageView.scaleType =
                            ImageView.ScaleType.FIT_START
                        6 -> binding.searchInNasaArchiveImageView.scaleType =
                            ImageView.ScaleType.CENTER
                        7 -> binding.searchInNasaArchiveImageView.scaleType =
                            ImageView.ScaleType.FIT_CENTER
                        else -> binding.searchInNasaArchiveImageView.scaleType =
                            ImageView.ScaleType.FIT_CENTER
                    }
                    if (typeChangeImage > 7) typeChangeImage = 0
                }
            }
            // Программная установка нового шрифта для описания найденной информации
            binding.searchInNasaArchiveDescriptionTextView.typeface =
                Typeface.createFromAsset(mainActivity.assets, "font/RobotoFlex_Regular.ttf")
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun showRecyclerViewWindowWithResults(forceHideWindow: Boolean) {
        if (forceHideWindow) isRecyclerViewWindowHide = false
        val constraintLayout = binding.nasaArchiveEntityListContainer
        val timeLayoutParams: (ConstraintLayout.LayoutParams) =
            constraintLayout.layoutParams as ConstraintLayout.LayoutParams
        timeLayoutParams.constrainedWidth = !isRecyclerViewWindowHide
        isRecyclerViewWindowHide = !isRecyclerViewWindowHide
        if (isRecyclerViewWindowHide) {
            binding.fragmentSearchInNasaArchiveGroupElements.visibility = View.VISIBLE
        } else {
            binding.fragmentSearchInNasaArchiveGroupElements.visibility = View.INVISIBLE
        }
        constraintLayout.layoutParams = timeLayoutParams
        binding.searchInNasaArchiveLoadingLayout.visibility = View.INVISIBLE
        binding.fragmentSearchInNasaArchiveRecyclerView.visibility = View.VISIBLE
        // Анимация появления списка с результатами поиска в архиве NASA
        binding.nasaArchiveEntityListContainer.alpha = transparientValue
        binding.nasaArchiveEntityListContainer.animate()
            .alpha(notTransparientValue)
            .setDuration(durationAnimation)
            .setListener(object: AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    binding.nasaArchiveEntityListContainer.isClickable = true
                }
            })
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
                        // Удаление сообщения об отсутствии найденной информации по запросу
                        binding.searchInNasaArchiveTitleTextView.text = ""
                        // Обновление списка найденных элементов в Recycler View
                        isRecyclerViewWindowHide = true
                        updateRecyclerViewList(serverResponseData.collection.items)
                        // Сброс типа анимации для изменения размера фотографии
                        typeChangeImage = 0
                        // Сохранение в "Избранное" найденных ответов от сервера NASA
                        var tempDescription: String = ""
                        serverResponseData.collection.items.forEach {
                            tempDescription += "$it\n"
                        }
                        mainActivity?.let { it.setListFavoriteDataDescription(tempDescription) }
                        searchNASAArchiveFavorite.setDescription(tempDescription)
                    }
                } else {
                    // Скрытие неиспользуемых полей
                    binding.searchInNasaArchiveLoadingLayout.visibility = View.INVISIBLE
                    binding.searchInNasaArchiveImageView.visibility = View.INVISIBLE
                    binding.searchInNasaArchiveDescriptionTextView.visibility = View.INVISIBLE
                    binding.nasaArchiveEntityListContainer.visibility = View.INVISIBLE
                    // Отображение информации об отсутствии найденной информации по запросу
                    binding.searchInNasaArchiveTitleTextView.text =
                        ConstantsController.ERROR_EMPTY_DOWNLOAD_DATES
                    binding.searchInNasaArchiveTitleTextView.visibility = View.VISIBLE
                    // Сохранение в "Избранное" ответа об отсутствии информации на сервере NASA
                    mainActivity?.let { it.setListFavoriteDataDescription(
                        ConstantsController.ERROR_EMPTY_DOWNLOAD_DATES)
                    }
                    searchNASAArchiveFavorite.setDescription(
                        ConstantsController.ERROR_EMPTY_DOWNLOAD_DATES)
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
    private fun sendRequestToNASAArchive(request: String): String {
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
            dataViewModel.getData(finalRequest)
                .observe(viewLifecycleOwner, Observer<NASAArchiveData> { renderData(it) })
        }
        return finalRequest
    }

    // Получение признака блокировки всех кнопок, кроме появившихся из контекстного меню
    fun getIsBlockingOtherFABButtons(): Boolean {
        mainActivity?.let {
            return it.getIsBlockingOtherFABButtons()
        }
        return false
    }

    // Передача MainActivity
    fun getMainActivity(): MainActivity? {
        return mainActivity
    }

    // Метод установки элемента из списка "Избранное" для просмотра в данном фрагменте
    fun setAndShowFavoriteData(favoriteData: Favorite) {
        favoriteData?.let {
            // Скрытие контейнера с результатами отображения Recycler View списка найденной
            // в архиве NASA информации
            showRecyclerViewWindowWithResults(true)

            // Анимированное появление найденной картинки по запросу в архиве NASA
            binding.searchInNasaArchiveImageView.alpha = transparientValue
            binding.searchInNasaArchiveImageView
                .load(it.getLinkImage()) {
                    lifecycle(this@SearchNASAArchiveFragment)
                    error(R.drawable.ic_load_error_vector)
                    // Анимация появления картинки
                    binding.searchInNasaArchiveImageView.animate()
                        .alpha(notTransparientValue)
                        .setDuration(durationAnimation)
                        .setListener(object: AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                binding.searchInNasaArchiveImageView.isClickable = true
                            }
                        })
                }

            // Анимационный показ заголовка и описания фотографии по запрошенному событию
            binding.searchInNasaArchiveTitleTextView.alpha = transparientValue
            binding.searchInNasaArchiveTitleTextView.text = it.getTitle()
            binding.searchInNasaArchiveDescriptionTextView.alpha = transparientValue
            binding.searchInNasaArchiveDescriptionTextView.text = it.getDescription()
            // Анимация появления заголовка картинки
            binding.searchInNasaArchiveTitleTextView.animate()
                .alpha(notTransparientValue)
                .setDuration(durationAnimation)
                .setListener(object: AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        binding.searchInNasaArchiveTitleTextView.isClickable = true
                    }
                })
            // Анимация появления описания картинки
            binding.searchInNasaArchiveDescriptionTextView
                .animate()
                .alpha(notTransparientValue)
                .setDuration(durationAnimation)
                .setListener(object: AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        binding.searchInNasaArchiveDescriptionTextView.isClickable = true
                    }
                })

            // Отобразить элементы View для вывода полученной информации
            binding.fragmentSearchInNasaArchiveGroupElements.visibility = View.VISIBLE
            binding.searchInNasaArchiveLoadingLayout.visibility = View.INVISIBLE
        }
    }

    // Получение текущих данных для "Избранное" на фрагменте с поиском в архиве NASA
    fun getSearchNASAArchiveFavorite(): Favorite {
        return searchNASAArchiveFavorite
    }

    // Получение dataViewModel
    @JvmName("getDataViewModel1")
    fun getDataViewModel(): NASAArchiveDataViewModel {
        return dataViewModel
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
            mainActivity.setListFavoriteDataTypeSource(searchNASAArchiveFavorite.getTypeSource())
            mainActivity.setListFavoriteDataTitle(searchNASAArchiveFavorite.getTitle())
            mainActivity.setListFavoriteDataDescription(searchNASAArchiveFavorite.getDescription())
            mainActivity.setListFavoriteDataLinkSource(searchNASAArchiveFavorite.getLinkSource())
            mainActivity.setListFavoriteDataPriority(searchNASAArchiveFavorite.getPriority())
            mainActivity.setListFavoriteDataSearchRequest(
                searchNASAArchiveFavorite.getSearchRequest())
            mainActivity.setListFavoriteDataLinkImage(searchNASAArchiveFavorite.getLinkImage())
            // Метод проверки наличия текущей информации в списке "Избранное"
            // и отрисовка соответствующего значка сердца (контурная или с заливкой)
            checkAndChangeHeartIconState()
        }
    }
}