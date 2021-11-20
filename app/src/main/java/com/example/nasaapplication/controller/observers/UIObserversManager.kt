package com.example.nasaapplication.controller.observers

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.util.Log
import android.view.Menu
import android.view.View
import com.example.nasaapplication.Constants
import com.example.nasaapplication.R
import com.example.nasaapplication.controller.navigation.contents.NavigationContent
import com.example.nasaapplication.controller.navigation.dialogs.NavigationDialogs
import com.example.nasaapplication.domain.FacadeFavoriteLogic
import com.example.nasaapplication.domain.logic.Favorite
import com.example.nasaapplication.domain.logic.FavoriteLogic
import com.example.nasaapplication.repository.facadeuser.room.LocalRoomImpl
import com.example.nasaapplication.ui.activities.MainActivity

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
    // 1) нажатие на кнопку "Позавчера" (кнопка назад);
    // 2) нажатие на кнопку "Вчера" (кнопка вперёд);
    // 3) нажатие на кнопку "Сегодня".

    // ФРАГМЕНТ "Поиск"
    // События:
    // 1) появление;
    // 2) нажатие на кнопку поиска в "Википедии".

    // ФРАГМЕНТ "Архив"
    // События:
    // 1) появление;
    // 2) нажатие на кнопку поиска в архиве "NASA";
    // 3) нажатие на кнопку вызова/скрытия списка найденной в архиве "NASA" информации;
    // 4) нажатие на элемент списка с найденной в архиве "NASA" информацией.

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
    // 1) нажатие на кнопку "Позавчера" (кнопка назад);
    fun clickOnBackDayButton() {

    }
    // 2) нажатие на кнопку "Вчера" (кнопка вперёд);
    fun clickOnForwardDayButton() {

    }
    // 3) нажатие на кнопку "Сегодня".
    fun clickOnTodayButton() {

    }
    //endregion

    //region МЕТОДЫ ФРАГМЕНТА "Поиск"
    // События:
    // 1) появление;
    fun showSearchWikiFragment() {

    }
    // 2) нажатие на кнопку поиска в "Википедии".
    fun clickOnSearchInWIKI() {

    }
    //endregion

    //region МЕТОДЫ ФРАГМЕНТА "Архив"
    // События:
    // 1) появление;
    fun showSearchNASAArchiveFragment() {

    }
    // 2) нажатие на кнопку поиска в архиве "NASA";
    fun clickOnSearchInNASA() {

    }
    // 3) нажатие на кнопку вызова/скрытия списка найденной в архиве "NASA" информации;
    fun clickOnShowAndHideFoundedInNASAInformation() {

    }
    // 4) нажатие на элемент списка с найденной в архиве "NASA" информацией.
    fun clickOnFoundedInNASAInformationItem() {

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
    fun clickOnFragmentImageFavoriteItem() {

    }
    //    содержащейся в элементе списка;
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
        Log.d("mylogs", "newTypeSource: $newTypeSource\n")
        newFavorite.setTypeSource(newTypeSource)
    }
    fun setListFavoriteDataPriority(newPriority: Int) {
        Log.d("mylogs", "newPriority: $newPriority\n")
        newFavorite.setPriority(newPriority)
    }
    fun setListFavoriteDataLinkSource(newLinkSource: String) {
        Log.d("mylogs", "newLinkSource: $newLinkSource\n")
        newFavorite.setLinkSource(newLinkSource)
    }
    fun setListFavoriteDataTitle(newTitle: String) {
        Log.d("mylogs", "newTitle: $newTitle\n")
        newFavorite.setTitle(newTitle)
    }
    fun setListFavoriteDataDescription(newDescription: String) {
        Log.d("mylogs", "newDescription: $newDescription\n")
        newFavorite.setDescription(newDescription)
    }
    fun setListFavoriteDataSearchRequest(newSearchRequest: String) {
        Log.d("mylogs", "newSearchRequest: $newSearchRequest\n")
        newFavorite.setSearchRequest(newSearchRequest)
    }
    fun setListFavoriteDataLinkImage(newLinkImage: String) {
        Log.d("mylogs", "newLinkImage: $newLinkImage\n")
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
}