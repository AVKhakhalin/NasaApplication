package com.example.nasaapplication.controller.observers

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import coil.load
import com.example.nasaapplication.Constants
import com.example.nasaapplication.R
import com.example.nasaapplication.controller.navigation.contents.NavigationContent
import com.example.nasaapplication.controller.navigation.dialogs.NavigationDialogs
import com.example.nasaapplication.domain.FacadeFavoriteLogic
import com.example.nasaapplication.domain.logic.Favorite
import com.example.nasaapplication.domain.logic.FavoriteLogic
import com.example.nasaapplication.repository.facadeuser.room.LocalRoomImpl
import com.example.nasaapplication.ui.activities.MainActivity
import com.example.nasaapplication.ui.fragments.contents.DayPhotoFragment
import com.example.nasaapplication.ui.fragments.contents.SearchNASAArchiveFragment
import com.example.nasaapplication.ui.fragments.contents.SearchWikiFragment

class UIObserversManager(
    val mainActivity: MainActivity,
    val localRoomImpl: LocalRoomImpl,
    val navigationContent: NavigationContent,
    val navigationDialogs: NavigationDialogs
) {
    //region ОПИСАНИЕ МЕТОДОВ -----------------------------------
    // АКТИВИТИ "MainActivity"
    // События:
    // 1) нажатие на кнопку "Бургер" для вызова меню переключения на фрагменты;
    // 2) короткое нажатие на кнопку "FAB" для переключения нижнего меню в состояние поиска;
    // 3) длинное нажатие на кнопку "FAB";
    //    для появления навигационных кнопок переключения между фрагментами;
    // 4) нажатие на кнопку с сердцем для занесения/удаления информации в/из список/а "Избранное";
    // 5) нажатие на кнопку с сердцем и списком для вызова списка "Избранное";
    // 6) нажатие на кнопку с шестерёнками для получения доступа к настройкам приложения;
    // 7) нажатие на кнопку для вызова поля ввода текста для создания поискового запроса;
    // 8) нажатие на кнопку для выполнения поиска по поисковому запросу;
    // 9) нажатие на кнопку для скрытия поля ввода текста для создания поискового запроса;
    // 10) работа метода onPause()

    // ФРАГМЕНТ "Фото дня"
    // События:
    // 1) появление;
    // 2) нажатие на кнопки изменения дней.

    // ФРАГМЕНТ "Поиск в Википедии"
    // События:
    // 1) появление;
    // 2) нажатие на кнопку поиска в "Википедии".

    // ФРАГМЕНТ "Поиск в архиве NASA"
    // События:
    // 1) появление;
    // 2) нажатие на кнопку поиска в архиве "NASA";
    // 3) нажатие на элемент списка с найденной в архиве "NASA" информацией.

    // ФРАГМЕНТ "Избранное"
    // События:
    // 1) появление;
    // 2) нажатие на заголовок элемента списка для появления его описания;
    // 3) нажатие на эмблему фрагмента, где создан элемент списка для отображения информации,
    //    содержащейся в элементе списка;
    // 4) нажатие на одну из 3-х кнопок выбора приоритета элемента в списке;
    // 5) нажатие на одну из кнопок изменения порядка элемента в списке;
    // 6) смахивание влево или вправо для удаления элемента из списка;
    // 7) длительное нажатие на элементе списка для его смещения вверх или вниз по списку.
    //endregion

    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
    // Признак блокировки кнопок во всем приложении, при появлении меню из нижней FAB
    private var isBlockingOtherFABButtons: Boolean = false
    // Menu
    private var isFavorite: Boolean = false
    // Данные для сохранения в "Избранное"
    private var newFavorite: Favorite = Favorite()
    private var dayPhotoFavorite: Favorite = Favorite()
    private var searchWikiFavorite: Favorite = Favorite()
    private var searchNASAArchiveFavorite: Favorite = Favorite()
    private var favoriteLogic: FavoriteLogic = FavoriteLogic()
    private var facadeFavoriteLogic: FacadeFavoriteLogic =
        FacadeFavoriteLogic(favoriteLogic, localRoomImpl, this)
    //endregion

    //region МЕТОДЫ КЛАССА "MainActivity"
    // 1) нажатие на кнопку "Бургер" для вызова меню переключения на фрагменты;
    fun clickOnBurgerButton() {
        // Отображение списка основных содержательных разделов приложения
        if (!isBlockingOtherFABButtons) navigationDialogs?.let {
            it.showBottomNavigationDrawerDialogFragment(mainActivity)
        }
    }
    // 2) короткое нажатие на кнопку "FAB" для переключения нижнего меню в состояние поиска;
    fun shortClickOnFABButton() {

    }
    // 3) длинное нажатие на кнопку "FAB";
    //    для появления навигационных кнопок переключения между фрагментами;
    fun longClickOnFABButton() {

    }
    // 4) нажатие на кнопку с сердцем для занесения/удаления информации в/из список/а "Избранное";
    fun clickOnHeartButton() {
        if ((!isBlockingOtherFABButtons) && (newFavorite != Favorite()) &&
            (newFavorite.getTitle().isNotEmpty())) {
            // Добавление понравившегося содержимого в список "Избранное"
            val indexSimilarData: Int = facadeFavoriteLogic.addFavoriteData(newFavorite)
            if (indexSimilarData == -1) {
                // Изменение вида иконки сердца
                changeHeartIconState(mainActivity, true, false)
                // Добавление новой записи "Избранное" в базу данных
                localRoomImpl.saveFavorite(newFavorite)
                // Уведомление пользователя о добавлении новой записи в список "Избранное"
                // TODO: Сделать изменение нового счётчика в закладке фрагмента
            } else {
                // Удаление понравившегося содержимого из списка "Избранное"
                facadeFavoriteLogic.removeFavoriteData(indexSimilarData)
                // Изменение вида иконки сердца
                changeHeartIconState(mainActivity, false, true)
                // Уведомление пользователя об удалении новой записи из списка "Избранное"
                // TODO: Сделать изменение нового счётчика в закладке фрагмента
            }
        }
    }
    // 5) нажатие на кнопку с сердцем и списком для вызова списка "Избранное";
    fun clickOnListHeartButton() {
        if (!isBlockingOtherFABButtons) {
            // Очистка текущей информации для добавления в список "Избранное"
            setListFavoriteEmptyData()
            // Отображение фрагмента со списком "Избранное"
            showFavoriteRecyclerListFragment()
        }
    }
    // 6) нажатие на кнопку с шестерёнками для получения доступа к настройкам приложения;
    fun clickOnSettingButton() {
        if (!isBlockingOtherFABButtons) {
            // Отображение фрагмента с настройками приложения
            showSettingsFragment()
        }
    }
    // 7) нажатие на кнопку для вызова поля ввода текста для создания поискового запроса;
    fun clickOnShowSearchDialogButton() {

    }
    // 8) нажатие на кнопку для выполнения поиска по поисковому запросу;
    fun clickOnStartSearchButton() {

    }
    // 9) нажатие на кнопку для скрытия поля ввода текста для создания поискового запроса.
    fun clickOnHideSearchDialogButton() {

    }
    // 10) работа метода onPause()
    fun onPauseMainActivity() {
        // Обновление списка "Избранное" в базе данных перед закрытием приложения
        facadeFavoriteLogic.updateFavoriteDataBase()
    }
    //endregion

    //region МЕТОДЫ ФРАГМЕНТА "Фото дня"
    // События:
    // 1) появление;
    fun showSearchDayPhotoFragment() {
        // Очистка текущей информации для "Избранное" при переключении на данный фрагмент
        setListFavoriteDataTypeSource(dayPhotoFavorite.getTypeSource())
        setListFavoriteDataTitle(dayPhotoFavorite.getTitle())
        setListFavoriteDataDescription(dayPhotoFavorite.getDescription())
        setListFavoriteDataLinkSource(dayPhotoFavorite.getLinkSource())
        setListFavoriteDataPriority(dayPhotoFavorite.getPriority())
        setListFavoriteDataSearchRequest(dayPhotoFavorite.getSearchRequest())
        setListFavoriteDataLinkImage(dayPhotoFavorite.getLinkImage())
        // Метод проверки наличия текущей информации в списке "Избранное"
        // и отрисовка соответствующего значка сердца (контурная или с заливкой)
        checkAndChangeHeartIconState()
    }
    // 2) нажатие на кнопки изменения дней
    fun clickOnDayButton() {
        clearFavoriteDataAndChangeHeartIconState()
    }
    //endregion

    //region МЕТОДЫ ФРАГМЕНТА "Поиск в Википедии"
    // События:
    // 1) появление;
    fun showSearchWikiFragment() {
        // Очистка текущей информации для "Избранное" при переключении на данный фрагмент
        setListFavoriteDataTypeSource(searchWikiFavorite.getTypeSource())
        setListFavoriteDataTitle(searchWikiFavorite.getTitle())
        setListFavoriteDataDescription(searchWikiFavorite.getDescription())
        setListFavoriteDataLinkSource(searchWikiFavorite.getLinkSource())
        setListFavoriteDataPriority(searchWikiFavorite.getPriority())
        setListFavoriteDataSearchRequest(searchWikiFavorite.getSearchRequest())
        setListFavoriteDataLinkImage(searchWikiFavorite.getLinkImage())
        // Метод проверки наличия текущей информации в списке "Избранное"
        // и отрисовка соответствующего значка сердца (контурная или с заливкой)
        checkAndChangeHeartIconState()
    }
    // 2) нажатие на кнопку поиска в "Википедии".
    fun clickOnSearchInWIKI() {
        clearFavoriteDataAndChangeHeartIconState()

    }
    //endregion

    //region МЕТОДЫ ФРАГМЕНТА "Поиск в архиве NASA"
    // События:
    // 1) появление;
    fun showSearchNASAArchiveFragment() {
            // Очистка текущей информации для "Избранное" при переключении на данный фрагмент
            setListFavoriteDataTypeSource(searchNASAArchiveFavorite.getTypeSource())
            setListFavoriteDataTitle(searchNASAArchiveFavorite.getTitle())
            setListFavoriteDataDescription(searchNASAArchiveFavorite.getDescription())
            setListFavoriteDataLinkSource(searchNASAArchiveFavorite.getLinkSource())
            setListFavoriteDataPriority(searchNASAArchiveFavorite.getPriority())
            setListFavoriteDataSearchRequest(searchNASAArchiveFavorite.getSearchRequest())
            setListFavoriteDataLinkImage(searchNASAArchiveFavorite.getLinkImage())
            // Метод проверки наличия текущей информации в списке "Избранное"
            // и отрисовка соответствующего значка сердца (контурная или с заливкой)
            checkAndChangeHeartIconState()
    }
    // 2) нажатие на кнопку поиска в архиве "NASA";
    fun clickOnSearchInNASA() {
        clearFavoriteDataAndChangeHeartIconState()
    }
    // 3) нажатие на элемент списка с найденной в архиве "NASA" информацией.
    fun clickOnFoundedInNASAInformationItem(searchNASAArchiveFragment: SearchNASAArchiveFragment,
    entityLink: String, newNASAArchiveEntity: String, entityText: String,
    durationAnimation: Long, transparientValue: Float, notTransparientValue: Float) {
        if (!getIsBlockingOtherFABButtons()) {
            clearFavoriteDataAndChangeHeartIconState()
            // Сохранение запроса, ссылки на картинку, заголовка и описания в "Избранное"
            setListFavoriteDataSearchRequest(
                "${searchNASAArchiveFragment.binding.inputNasaFieldText.text}")
            setListFavoriteDataTypeSource(Constants.SEARCH_NASA_ARCHIVE_FRAGMENT_INDEX)
            setListFavoriteDataLinkImage(entityLink)
            setListFavoriteDataTitle(newNASAArchiveEntity)
            setListFavoriteDataDescription(entityText)
            setListFavoriteDataLinkSource(
                searchNASAArchiveFragment.getDataViewModel().getRequestUrl())
            setListFavoriteDataPriority(0)

            searchNASAArchiveFavorite.setSearchRequest(
                    "${searchNASAArchiveFragment.binding.inputNasaFieldText.text}")
            searchNASAArchiveFavorite.setTypeSource(Constants.SEARCH_NASA_ARCHIVE_FRAGMENT_INDEX)
            searchNASAArchiveFavorite.setLinkImage(entityLink)
            searchNASAArchiveFavorite.setTitle(newNASAArchiveEntity)
            searchNASAArchiveFavorite.setDescription(entityText)
            searchNASAArchiveFavorite.setLinkSource(
                    searchNASAArchiveFragment.getDataViewModel().getRequestUrl())
            searchNASAArchiveFavorite.setPriority(0)

            // Анимированное появление найденной картинки по запросу в архиве NASA
            searchNASAArchiveFragment.binding.searchInNasaArchiveImageView.alpha = transparientValue
            searchNASAArchiveFragment.binding.searchInNasaArchiveImageView
                .load(entityLink) {
                    lifecycle(searchNASAArchiveFragment)
                    error(R.drawable.ic_load_error_vector)
                    // Анимация появления картинки
                    searchNASAArchiveFragment.binding.searchInNasaArchiveImageView.animate()
                        .alpha(notTransparientValue)
                        .setDuration(durationAnimation)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                searchNASAArchiveFragment.binding
                                    .searchInNasaArchiveImageView.isClickable = true
                            }
                        })
                }

            // Анимационный показ заголовка и описания фотографии по запрошенному событию
            searchNASAArchiveFragment.binding.searchInNasaArchiveTitleTextView.alpha =
                transparientValue
            searchNASAArchiveFragment.binding.searchInNasaArchiveTitleTextView.text =
                newNASAArchiveEntity
            searchNASAArchiveFragment.binding.searchInNasaArchiveDescriptionTextView.alpha =
                transparientValue
            searchNASAArchiveFragment.binding.searchInNasaArchiveDescriptionTextView.text =
                entityText
            // Анимация появления заголовка картинки
            searchNASAArchiveFragment.binding.searchInNasaArchiveTitleTextView.animate()
                .alpha(notTransparientValue)
                .setDuration(durationAnimation)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        searchNASAArchiveFragment.binding.searchInNasaArchiveTitleTextView
                            .isClickable = true
                    }
                })
            // Анимация появления описания картинки
            searchNASAArchiveFragment.binding.searchInNasaArchiveDescriptionTextView
                .animate()
                .alpha(notTransparientValue)
                .setDuration(durationAnimation)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        searchNASAArchiveFragment.binding
                            .searchInNasaArchiveDescriptionTextView.isClickable = true
                    }
                })

            // Отобразить элементы View для вывода полученной информации
            searchNASAArchiveFragment.binding.fragmentSearchInNasaArchiveGroupElements
                .visibility = View.VISIBLE
            searchNASAArchiveFragment.binding.searchInNasaArchiveLoadingLayout
                .visibility = View.INVISIBLE

            // Скрытие списка Recycler View с результатами поиска в архиве NASA
            val constraintLayout = searchNASAArchiveFragment.binding.nasaArchiveEntityListContainer
            val timeLayoutParams: (ConstraintLayout.LayoutParams) =
                constraintLayout.layoutParams as ConstraintLayout.LayoutParams
            timeLayoutParams.constrainedWidth = true
            constraintLayout.layoutParams = timeLayoutParams
            searchNASAArchiveFragment.setIsRecyclerViewWindowHide(true)
            searchNASAArchiveFragment.binding.fragmentSearchInNasaArchiveRecyclerView
                .visibility = View.INVISIBLE

            // Метод проверки наличия текущей информации в списке "Избранное"
            // и отрисовка соответствующего значка сердца (контурная или с заливкой)
            checkAndChangeHeartIconState()
        }
    }
    //endregion

    //region МЕТОДЫ ФРАГМЕНТА "Избранное"
    // События:
    // 1) появление;
    fun appearanceFavoriteRecyclerListFragment() {

    }
    // 2) нажатие на заголовок элемента списка для появления его описания;
    fun clickOnTitleFavoriteItem() {

    }
    // 3) нажатие на эмблему фрагмента, где создан элемент списка для отображения информации,
    //    содержащейся в элементе списка;
    fun clickOnFragmentImageFavoriteItem(favoriteData: Favorite) {
        when(favoriteData.getTypeSource()) {
            Constants.DAY_PHOTO_FRAGMENT_INDEX -> {
                // Очистка текущей информации для списка "Избранное"
                // при переключении на фрагмент "Картинка дня"
                setListFavoriteEmptyData()
                // Открытие выбранной информации во фрагменте "Картинка дня"
                mainActivity.getViewPager().currentItem =
                    Constants.DAY_PHOTO_FRAGMENT_INDEX
                (mainActivity.getViewPagerAdapter()
                    .getFragments()[Constants.DAY_PHOTO_FRAGMENT_INDEX]
                        as DayPhotoFragment).setAndShowFavoriteData(favoriteData)
                mainActivity.binding.activityFragmentsContainer.visibility = View.INVISIBLE
                mainActivity.binding.transparentBackground.visibility = View.VISIBLE
            }
            Constants.SEARCH_WIKI_FRAGMENT_INDEX -> {
                // Очистка текущей информации для списка "Избранное"
                // при переключении на фрагмент с поиском в Википедии
                setListFavoriteEmptyData()
                // Открытие выбранной информации во фрагменте с поиском в Википедии
                (mainActivity.getViewPagerAdapter()
                    .getFragments()[Constants.SEARCH_WIKI_FRAGMENT_INDEX]
                        as SearchWikiFragment).setAndShowFavoriteData(favoriteData)
                mainActivity.getViewPager().currentItem = Constants.SEARCH_WIKI_FRAGMENT_INDEX
                mainActivity.binding.activityFragmentsContainer.visibility = View.INVISIBLE
                mainActivity.binding.transparentBackground.visibility = View.VISIBLE
            }
            Constants.SEARCH_NASA_ARCHIVE_FRAGMENT_INDEX -> {
                // Очистка текущей информации для списка "Избранное"
                // при переключении на фрагмент с поиском в архиве NASA
                setListFavoriteEmptyData()
                // Открытие выбранной информации во фрагменте с поиском в архиве NASA
                mainActivity.binding.viewPager.visibility = View.VISIBLE
                mainActivity.binding.tabLayout.visibility = View.VISIBLE
                mainActivity.binding.activityFragmentsContainer.visibility = View.INVISIBLE

                mainActivity.getViewPager().currentItem =
                    Constants.SEARCH_NASA_ARCHIVE_FRAGMENT_INDEX
                (mainActivity.getViewPagerAdapter()
                    .getFragments()[Constants.SEARCH_NASA_ARCHIVE_FRAGMENT_INDEX]
                        as SearchNASAArchiveFragment)
                    .setAndShowFavoriteData(favoriteData)
                mainActivity.binding.activityFragmentsContainer.visibility = View.INVISIBLE
                mainActivity.binding.transparentBackground.visibility = View.VISIBLE
                // Подготовка данных для исключения данных из "Избранного"
                setListFavoriteDataSearchRequest(favoriteData.getSearchRequest())
                setListFavoriteDataTypeSource(favoriteData.getTypeSource())
                setListFavoriteDataLinkImage(favoriteData.getLinkImage())
                setListFavoriteDataTitle(favoriteData.getTitle())
                setListFavoriteDataDescription(favoriteData.getDescription())
                setListFavoriteDataLinkSource(favoriteData.getLinkSource())
                setListFavoriteDataPriority(favoriteData.getPriority())

                searchNASAArchiveFavorite.setSearchRequest(favoriteData.getSearchRequest())
                searchNASAArchiveFavorite.setTypeSource(favoriteData.getTypeSource())
                searchNASAArchiveFavorite.setLinkImage(favoriteData.getLinkImage())
                searchNASAArchiveFavorite.setTitle(favoriteData.getTitle())
                searchNASAArchiveFavorite.setDescription(favoriteData.getDescription())
                searchNASAArchiveFavorite.setLinkSource(favoriteData.getLinkSource())
                searchNASAArchiveFavorite.setPriority(favoriteData.getPriority())
                // Изменение иконки сердца на закрашенную
                changeHeartIconState(mainActivity, true, false)
            }
            else -> {
                mainActivity.toast("${mainActivity.getString(R.string.error)}: ${
                    mainActivity.getString(R.string.unknown_type_source_favorite_data)}")
            }
        }
    }
    // 4) нажатие на одну из 3-х кнопок выбора приоритета элемента в списке;
    fun clickOnPriorityButtonFavoriteItem() {

    }
    // 5) нажатие на одну из кнопок изменения порядка элемента в списке;
    fun clickOnOrderButtonFavoriteItem() {

    }
    // 6) смахивание влево или вправо для удаления элемента из списка;
    fun flapHorizontalForDeleteFavoriteItem() {

    }
    // 7) длительное нажатие на элементе списка для его смещения вверх или вниз по списку.
    fun flapVerticalForReorderFavoriteItem() {

    }
    //endregion

    //region ДРУГИЕ ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
    // Установка признака блокировки всех кнопок, кроме появившихся из контекстного меню
    fun setIsBlockingOtherFABButtons(isBlockingOtherFABButtons: Boolean) {
        this.isBlockingOtherFABButtons = isBlockingOtherFABButtons
    }
    // Получение признака блокировки всех кнопок, кроме появившихся из контекстного меню
    fun getIsBlockingOtherFABButtons(): Boolean {
        return isBlockingOtherFABButtons
    }
    // Метод отображения фрагмента с настройками приложения
    fun showSettingsFragment() {
        navigationContent?.let{
            showFragmentContainerHideBackground()
            it.showSettingsFragment(false)
        }
    }
    // Метод отображения фрагмента со списком "Избранное"
    fun showFavoriteRecyclerListFragment() {
        navigationContent?.let{
            showFragmentContainerHideBackground()
            it.showFavoriteRecyclerListFragment(false)
        }
    }
    // Методы по отображению контейнера с фрагментами и скрытию контейнера с ViewPager-фрагментами
    fun showFragmentContainerHideBackground() {
        mainActivity.binding.activityFragmentsContainer.visibility = View.VISIBLE
        mainActivity.binding.transparentBackground.visibility = View.INVISIBLE
    }
    // Получение фасада класса с логикой "FavoriteLogic"
    fun getFacadeFavoriteLogic(): FacadeFavoriteLogic {
        return facadeFavoriteLogic
    }
    //endregion
    //region МЕТОДЫ ДЛЯ ИЗМЕНЕНИЯ ВИДА ИКОНКИ СЕРДЦА
    // Изменение вида иконки сердца
    fun changeHeartIconState(mainActivity: MainActivity, forceOn: Boolean, forceOff: Boolean) {
        if (forceOn) isFavorite = true
        if (forceOff) isFavorite = false
        mainActivity.getBottomMenu()?.let {
            if (it.size() > 0) {
                if (isFavorite) {
                    it.getItem(Constants.INDEX_ADD_FAVORITE_MENU_ITEM)
                        .setIcon(R.drawable.ic_favourite_on)
                } else {
                    it.getItem(Constants.INDEX_ADD_FAVORITE_MENU_ITEM)
                        .setIcon(R.drawable.ic_favourite)
                }
            }
        }
    }
    fun getIsFavorite(): Boolean {
        return isFavorite
    }
    //endregion

    //region МЕТОДЫ ДЛЯ ПОЛУЧЕНИЯ ДАННЫХ ДЛЯ СПИСКА "ИЗБРАННОЕ"
    fun setListFavoriteDataTypeSource(newTypeSource: Int) {
        newFavorite.setTypeSource(newTypeSource)
    }
    fun setListFavoriteDataPriority(newPriority: Int) {
        newFavorite.setPriority(newPriority)
    }
    fun setListFavoriteDataLinkSource(newLinkSource: String) {
        newFavorite.setLinkSource(newLinkSource)
    }
    fun setListFavoriteDataTitle(newTitle: String) {
        newFavorite.setTitle(newTitle)
    }
    fun setListFavoriteDataDescription(newDescription: String) {
        newFavorite.setDescription(newDescription)
    }
    fun setListFavoriteDataSearchRequest(newSearchRequest: String) {
        newFavorite.setSearchRequest(newSearchRequest)
    }
    fun setListFavoriteDataLinkImage(newLinkImage: String) {
        newFavorite.setLinkImage(newLinkImage)
    }
    fun setListFavoriteEmptyData() {
        newFavorite = Favorite()
    }
    //endregion


    // Метод с начальной настройкой фрагмента "Фото дня"
    fun initialSettingDayPhotoFragment() {
        mainActivity?.let { mainActivity ->
            // Очистка текущей информации для "Избранное" при переключении на данный фрагмент
            setListFavoriteDataTypeSource(dayPhotoFavorite.getTypeSource())
            setListFavoriteDataTitle(dayPhotoFavorite.getTitle())
            setListFavoriteDataDescription(dayPhotoFavorite.getDescription())
            setListFavoriteDataLinkSource(dayPhotoFavorite.getLinkSource())
            setListFavoriteDataPriority(dayPhotoFavorite.getPriority())
            setListFavoriteDataSearchRequest(dayPhotoFavorite.getSearchRequest())
            setListFavoriteDataLinkImage(dayPhotoFavorite.getLinkImage())
            // Метод проверки наличия текущей информации в списке "Избранное"
            // и отрисовка соответствующего значка сердца (контурная или с заливкой)
            checkAndChangeHeartIconState()
        }
    }
    // Метод проверки наличия текущей информации в списке "Избранное"
    // и отрисовка соответствующего значка сердца (контурная или с заливкой)
    fun checkAndChangeHeartIconState() {
        mainActivity?.let { mainActivity ->
            if (getFacadeFavoriteLogic().checkSimilarFavoriteData())
                changeHeartIconState(mainActivity, true, false)
            else
                changeHeartIconState(mainActivity, false, true)
        }
    }
    // Метод получения newFavorite
    fun getNewFavorite(): Favorite {
        return newFavorite
    }
    // Метод получения dayPhotoFavorite
    fun getDayPhotoFavorite(): Favorite {
        return dayPhotoFavorite
    }
    // Метод получения searchWikiFavorite
    fun getSearchWikiFavorite(): Favorite {
        return searchWikiFavorite
    }
    // Метод получения searchNASAArchiveFavorite
    fun getSearchNASAArchiveFavorite(): Favorite {
        return searchNASAArchiveFavorite
    }

    // Метод очистки текущей информации для добавления в "Избранное"
    // и изменения вида иконки сердца на контурное
    fun clearFavoriteDataAndChangeHeartIconState() {
        if (!getIsBlockingOtherFABButtons()) {
            // Очистка текущей информации для добавления в "Избранное"
            setListFavoriteEmptyData()
            // Изменение вида иконки сердца на контурное
            changeHeartIconState(mainActivity, false, true)
        }
    }
}