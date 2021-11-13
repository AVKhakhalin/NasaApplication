package com.example.nasaapplication.domain.logic

import android.util.Log
import android.widget.Toast

// Класс с логикой проекта - построение и сохранение списка избранных данных
class FavoriteLogic {
    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
    private var fullDatesList: MutableList<Favorite> = mutableListOf()
    private var correctedDatesList: MutableList<Favorite> = mutableListOf()
    private var filterWord: String = ""
    //endregion

    //region МЕТОДЫ ДОБАВЛЕНИЯ ДАННЫХ В СПИСОК ИЗБРАННЫХ ДАННЫХ
    fun addFavoriteData(newFavorite: Favorite): Int {
        var indexSimilarData: Int = -1
        for (counter in 0 until fullDatesList.size) {
            if (fullDatesList[counter].getTypeSource() == newFavorite.getTypeSource()) {
                if ((fullDatesList[counter].getLinkSource() == newFavorite.getLinkSource())
                    && (fullDatesList[counter].getTitle() == newFavorite.getTitle())
                    && (fullDatesList[counter].getDescription() == newFavorite.getDescription())
                    && (fullDatesList[counter].getSearchRequest() == newFavorite.getSearchRequest())
                    && (fullDatesList[counter].getLinkImage() == newFavorite.getLinkImage())
                ) {
                    indexSimilarData = counter
                    break
                }
            }
        }
        if (indexSimilarData == -1) fullDatesList.add(newFavorite)
        return indexSimilarData
    }
    //endregion

    //region МЕТОДЫ УДАЛЕНИЯ ДАННЫХ ИЗ СПИСКА ИЗБРАННЫХ ДАННЫХ
    fun removeFavoriteData(indexRemovedFavoriteData: Int) {
        fullDatesList.removeAt(indexRemovedFavoriteData)
    }
    fun removeFavoriteData(typeSource: Int, delLinkSource: String) {
        fullDatesList.forEach {
            if ((it.getTypeSource() == typeSource) && (it.getLinkSource() == delLinkSource)) {
                fullDatesList.remove(it)
            }
        }
    }
    fun removeFavoriteDataByCorrectedData(indexRemovedFavoriteCorrectedData: Int) {
        fullDatesList.remove(correctedDatesList[indexRemovedFavoriteCorrectedData])
    }
    fun removeAndAddFavoriteDataByCorrectedData(indexRemovedFavoriteCorrectedData: Int,
                                                indexAddedFavoriteCorrectedData: Int) {
        fullDatesList.remove(correctedDatesList[indexRemovedFavoriteCorrectedData])
        var addedIndex: Int =
            searchElementInCorrectedDatesList(correctedDatesList[indexAddedFavoriteCorrectedData])
        if (addedIndex > -1) {
            fullDatesList.add(addedIndex, correctedDatesList[indexRemovedFavoriteCorrectedData])
        }
    }
    //endregion
    
    //region МЕТОДЫ РЕДАКТИРОВАНИЯ ИЗБРАННЫХ ДАННЫХ
    fun editFavoriteData(indexEditedFavoriteData: Int, newPriority: Int, newTitle: String, newDescription: String) {
        if (fullDatesList.size >= indexEditedFavoriteData + 1) {
            fullDatesList[indexEditedFavoriteData].setPriority(newPriority)
            fullDatesList[indexEditedFavoriteData].setTitle(newTitle)
            fullDatesList[indexEditedFavoriteData].setDescription(newDescription)
        }
    }
    fun editFavoriteData(titleEditedFavoriteData: String, newPriority: Int, newTitle: String, newDescription: String) {
        fullDatesList.forEach {
            if (it.getTitle() == titleEditedFavoriteData) {
                it.setPriority(newPriority)
                it.setTitle(newTitle)
                it.setDescription(newDescription)
            }
        }
    }
    //endregion

    // Получение списка избранных данных
    fun getDatesList(): MutableList<Favorite> {
        correctedDatesList = mutableListOf()
        if (filterWord.isNotEmpty()) {
            fullDatesList.forEach {
                if (it.getTitle().lowercase().indexOf(filterWord.lowercase()) > -1)
                    correctedDatesList.add(it)
            }
        } else {
            fullDatesList.forEach {
                correctedDatesList.add(it)
            }
        }
        return correctedDatesList
    }

    // Проверка на то, что новые данные уже есть в списке "Избранное"
    fun checkSimilarFavoriteData(newFavorite: Favorite): Boolean {
        for (counter in 0 until fullDatesList.size) {
            if (fullDatesList[counter].getTypeSource() == newFavorite.getTypeSource()) {
                if ((fullDatesList[counter].getLinkSource() == newFavorite.getLinkSource())
                    && (fullDatesList[counter].getTitle() == newFavorite.getTitle())
                    && (fullDatesList[counter].getDescription() == newFavorite.getDescription())
                    && (fullDatesList[counter].getSearchRequest() == newFavorite.getSearchRequest())
                    && (fullDatesList[counter].getLinkImage() == newFavorite.getLinkImage())
                ) {
                    return true
                }
            }
        }
        return false
    }

    // Установка фильтра для выбора нужной информации из списка "Избранное"
    fun setFilterWord(newFilterWord: String) {
        this.filterWord = newFilterWord
    }

    // Поиск элемента в списке correctedDatesList
    fun searchElementInCorrectedDatesList(searchedFavoriteData: Favorite): Int {
        for (counter in 0 until correctedDatesList.size) {
            if (fullDatesList[counter].getTypeSource() == searchedFavoriteData.getTypeSource()) {
                if ((fullDatesList[counter].getLinkSource() ==
                            searchedFavoriteData.getLinkSource())
                    && (fullDatesList[counter].getTitle() ==
                            searchedFavoriteData.getTitle())
                    && (fullDatesList[counter].getDescription() ==
                            searchedFavoriteData.getDescription())
                    && (fullDatesList[counter].getSearchRequest() ==
                            searchedFavoriteData.getSearchRequest())
                    && (fullDatesList[counter].getLinkImage() ==
                            searchedFavoriteData.getLinkImage())
                ) {
                    return counter
                }
            }
        }
        return -1 // Элемент не найден
    }
}