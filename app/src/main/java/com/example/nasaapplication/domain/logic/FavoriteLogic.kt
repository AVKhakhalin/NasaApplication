package com.example.nasaapplication.domain.logic

import android.util.Log
import android.widget.Toast
import com.example.nasaapplication.ui.activities.MainActivity

// Класс с логикой проекта - построение и сохранение списка избранных данных
class FavoriteLogic {
    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
    private var datesList: MutableList<Favorite> = mutableListOf()
    //endregion

    //region МЕТОДЫ ДОБАВЛЕНИЯ ДАННЫХ В СПИСОК ИЗБРАННЫХ ДАННЫХ
    fun addFavoriteData(newFavorite: Favorite): Int {
        var indexSimilarData: Int = -1
        for (counter in 0 until datesList.size) {
            if (datesList[counter].getTypeSource() == newFavorite.getTypeSource()) {
                if ((datesList[counter].getLinkSource() == newFavorite.getLinkSource())
                    && (datesList[counter].getTitle() == newFavorite.getTitle())
                    && (datesList[counter].getDescription() == newFavorite.getDescription())
                    && (datesList[counter].getSearchRequest() == newFavorite.getSearchRequest())
                    && (datesList[counter].getLinkImage() == newFavorite.getLinkImage())
                ) {
                    indexSimilarData = counter
                    break
                }
            }
        }
        if (indexSimilarData == -1) datesList.add(newFavorite)
        return indexSimilarData
    }
    //endregion

    //region МЕТОДЫ УДАЛЕНИЯ ДАННЫХ ИЗ СПИСКА ИЗБРАННЫХ ДАННЫХ
    fun removeFavoriteData(indexRemovedFavoriteData: Int) {
        datesList.removeAt(indexRemovedFavoriteData)
    }
    fun removeFavoriteData(typeSource: Int, delLinkSource: String) {
        datesList.forEach {
            if ((it.getTypeSource() == typeSource) && (it.getLinkSource() == delLinkSource)) {
                datesList.remove(it)
            }
        }
    }
    //endregion
    
    //region МЕТОДЫ РЕДАКТИРОВАНИЯ ИЗБРАННЫХ ДАННЫХ
    fun editFavoriteData(indexEditedFavoriteData: Int, newPriority: Int, newTitle: String, newDescription: String) {
        if (datesList.size >= indexEditedFavoriteData + 1) {
            datesList[indexEditedFavoriteData].setPriority(newPriority)
            datesList[indexEditedFavoriteData].setTitle(newTitle)
            datesList[indexEditedFavoriteData].setDescription(newDescription)
        }
    }
    fun editFavoriteData(titleEditedFavoriteData: String, newPriority: Int, newTitle: String, newDescription: String) {
        datesList.forEach {
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
        return datesList
    }

    // Проверка на то, что новые данные уже есть в списке "Избранное"
    fun checkSimilarFavoriteData(newFavorite: Favorite): Boolean {
        for (counter in 0 until datesList.size) {
            if (datesList[counter].getTypeSource() == newFavorite.getTypeSource()) {
                if ((datesList[counter].getLinkSource() == newFavorite.getLinkSource())
                    && (datesList[counter].getTitle() == newFavorite.getTitle())
                    && (datesList[counter].getDescription() == newFavorite.getDescription())
                    && (datesList[counter].getSearchRequest() == newFavorite.getSearchRequest())
                    && (datesList[counter].getLinkImage() == newFavorite.getLinkImage())
                ) {
                    return true
                }
            }
        }
        return false
    }
}