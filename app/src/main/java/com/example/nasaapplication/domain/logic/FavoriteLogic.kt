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
        if (datesList.size == 5) {
            Log.d("mylogs",
                "\n\n${datesList[0].getLinkImage()}" +
                        "\n${datesList[0].getLinkSource()}" +
                        "\n${datesList[0].getSearchRequest()}" +
                        "\n${datesList[0].getTypeSource()}" +
                        "\n${datesList[0].getDescription()}" +
                        "\n${datesList[0].getTitle()}" +
                        "\n" +
                        "\n${datesList[1].getLinkImage()}" +
                        "\n${datesList[1].getLinkSource()}" +
                        "\n${datesList[1].getSearchRequest()}" +
                        "\n${datesList[1].getTypeSource()}" +
                        "\n${datesList[1].getDescription()}" +
                        "\n${datesList[1].getTitle()}" +
                        "\n" +
                        "\n${datesList[2].getLinkImage()}" +
                        "\n${datesList[2].getLinkSource()}" +
                        "\n${datesList[2].getSearchRequest()}" +
                        "\n${datesList[2].getTypeSource()}" +
                        "\n${datesList[2].getDescription()}" +
                        "\n${datesList[2].getTitle()}" +
                        "\n" +
                        "\n${datesList[3].getLinkImage()}" +
                        "\n${datesList[3].getLinkSource()}" +
                        "\n${datesList[3].getSearchRequest()}" +
                        "\n${datesList[3].getTypeSource()}" +
                        "\n${datesList[3].getDescription()}" +
                        "\n${datesList[3].getTitle()}" +
                        "\n" +
                        "\n${datesList[4].getLinkImage()}" +
                        "\n${datesList[4].getLinkSource()}" +
                        "\n${datesList[4].getSearchRequest()}" +
                        "\n${datesList[4].getTypeSource()}" +
                        "\n${datesList[4].getDescription()}" +
                        "\n${datesList[4].getTitle()}" +
                        "\n" +
                        "\n$indexSimilarData")
        }
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