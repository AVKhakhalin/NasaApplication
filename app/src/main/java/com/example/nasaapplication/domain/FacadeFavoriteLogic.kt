package com.example.nasaapplication.domain

import com.example.nasaapplication.domain.logic.Favorite
import com.example.nasaapplication.domain.logic.FavoriteLogic
import com.example.nasaapplication.repository.facadeuser.room.LocalRoomImpl

class FacadeFavoriteLogic(
    val favoriteLogic: FavoriteLogic,
    val localRoomImpl: LocalRoomImpl,
    val newFavorite: Favorite
) {
    // Обновление списка "Избранное" в базе данных перед закрытием приложения
    fun updateFavoriteDataBase() {
        localRoomImpl.deleteAllFavorite()
        favoriteLogic.setFilterWord("")
        favoriteLogic.getDatesList().forEach {
            localRoomImpl.saveFavorite(it)
        }
    }

    // Считывание данных по списку "Избранное" из базы данных
    fun addListFavoriteData(listFavorite: List<Favorite>) {
        favoriteLogic.addListFavoriteData(listFavorite)
    }

    // Добавление понравившегося содержимого в список "Избранное"
    fun addFavoriteData(newFavorite: Favorite): Int {
        return favoriteLogic.addFavoriteData(newFavorite)
    }

    // Удаление понравившегося содержимого из списка "Избранное"
    fun removeFavoriteData(indexSimilarData: Int) {
        favoriteLogic.removeFavoriteData(indexSimilarData)
    }

    // Получение списка избранных данных
    fun getFavoriteDataList(): MutableList<Favorite> {
        return favoriteLogic.getDatesList()
    }
    // Проверка на то, что новые данные уже есть в списке "Избранное"
    fun checkSimilarFavoriteData(): Boolean {
        return favoriteLogic.checkSimilarFavoriteData(newFavorite)
    }
    // Установка фильтра для выбора нужной информации из списка "Избранное"
    fun setFilterWord(newFilterWord: String) {
        favoriteLogic.setFilterWord(newFilterWord)
    }
    // Удаление данных в списке FullDates
    fun removeFavoriteDataByCorrectedData(indexRemovedFavoriteCorrectedData: Int) {
        favoriteLogic.removeFavoriteDataByCorrectedData(indexRemovedFavoriteCorrectedData)
    }
    // Удаление и добавление данных в списке FullDates
    fun removeAndAddFavoriteDataByCorrectedData(indexRemovedFavoriteCorrectedData: Int,
                                                indexAddedFavoriteCorrectedData: Int) {
        favoriteLogic.removeAndAddFavoriteDataByCorrectedData(
            indexRemovedFavoriteCorrectedData, indexAddedFavoriteCorrectedData)
    }

    // Ранжирование списка fullDatesList по приоритетам
    fun priorityRangeFullDatesList() {
        favoriteLogic.priorityRangeFullDatesList()
    }
}