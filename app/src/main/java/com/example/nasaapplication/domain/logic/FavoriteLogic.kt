package com.example.nasaapplication.domain.logic

import com.example.nasaapplication.Constants

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
        if (indexSimilarData == -1) {
            fullDatesList.add(Favorite())
            val lastIndex: Int = fullDatesList.size - 1
            fullDatesList[lastIndex].setLinkSource(newFavorite.getLinkSource())
            fullDatesList[lastIndex].setDescription(newFavorite.getDescription())
            fullDatesList[lastIndex].setTitle(newFavorite.getTitle())
            fullDatesList[lastIndex].setLinkImage(newFavorite.getLinkImage())
            fullDatesList[lastIndex].setTypeSource(newFavorite.getTypeSource())
            fullDatesList[lastIndex].setSearchRequest(newFavorite.getSearchRequest())
            fullDatesList[lastIndex].setPriority(newFavorite.getPriority())
            fullDatesList[lastIndex].setIsShowDescription(newFavorite.getIsShowDescription())
        }
        return indexSimilarData
    }
    fun addListFavoriteData(newListFavorite: List<Favorite>) {
        newListFavorite.forEach {
            fullDatesList.add(Favorite())
            val lastIndex: Int = fullDatesList.size - 1
            fullDatesList[lastIndex].setLinkSource(it.getLinkSource())
            fullDatesList[lastIndex].setDescription(it.getDescription())
            fullDatesList[lastIndex].setTitle(it.getTitle())
            fullDatesList[lastIndex].setLinkImage(it.getLinkImage())
            fullDatesList[lastIndex].setTypeSource(it.getTypeSource())
            fullDatesList[lastIndex].setSearchRequest(it.getSearchRequest())
            fullDatesList[lastIndex].setPriority(it.getPriority())
            fullDatesList[lastIndex].setIsShowDescription(it.getIsShowDescription())
        }
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
        val removedIndex: Int =
            searchElementInCorrectedDatesList(correctedDatesList[indexRemovedFavoriteCorrectedData])
        val addedIndex: Int =
            searchElementInCorrectedDatesList(correctedDatesList[indexAddedFavoriteCorrectedData])
        val removeFavoriteData: Favorite = fullDatesList.removeAt(removedIndex)
        if (addedIndex > -1) {
            fullDatesList.add(addedIndex, removeFavoriteData)
        }
    }
    //endregion
    
    //region МЕТОДЫ РЕДАКТИРОВАНИЯ ИЗБРАННЫХ ДАННЫХ
    fun editFavoriteData(
        indexEditedFavoriteData: Int, newPriority: Int, newTitle: String, newDescription: String) {
        if (fullDatesList.size >= indexEditedFavoriteData + 1) {
            fullDatesList[indexEditedFavoriteData].setPriority(newPriority)
            fullDatesList[indexEditedFavoriteData].setTitle(newTitle)
            fullDatesList[indexEditedFavoriteData].setDescription(newDescription)
        }
    }
    fun editFavoriteData(titleEditedFavoriteData: String, newPriority: Int,
                         newTitle: String, newDescription: String) {
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
                    && (if (newFavorite.getTypeSource() == Constants.SEARCH_WIKI_FRAGMENT_INDEX)
                            (fullDatesList[counter].getDescription().length ==
                                    newFavorite.getDescription().length) else
                                            (fullDatesList[counter].getDescription() ==
                                                    newFavorite.getDescription()))
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
    // Получение фильтра для выбора нужной информации из списка "Избранное"
    fun getFilterWord(): String {
        return filterWord
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

    // Ранжирование списка fullDatesList по приоритетам
    fun priorityRangeFullDatesList() {
        // Фильтрация записей с приоритетом High
        var highPriorityLastIndex: Int = 0
        for (counter in 0 until fullDatesList.size) {
            when (fullDatesList[counter].getPriority()) {
                Constants.PRIORITY_HIGH -> {
                    if (counter != highPriorityLastIndex) {
                        fullDatesList.removeAt(counter).apply {
                            fullDatesList.add(highPriorityLastIndex++, this)
                        }
                    } else highPriorityLastIndex++
                }
            }
        }
        // Фильтрация записей с приоритетом Normal
        var normalPriorityLastIndex: Int = highPriorityLastIndex
        for (counter in highPriorityLastIndex until fullDatesList.size) {
            when (fullDatesList[counter].getPriority()) {
                Constants.PRIORITY_NORMAL -> {
                    if (counter != normalPriorityLastIndex) {
                        fullDatesList.removeAt(counter).apply {
                            fullDatesList.add(normalPriorityLastIndex++, this)
                        }
                    } else normalPriorityLastIndex++
                }
            }
        }
        // Фильтрация записей с приоритетом Low
        var lowPriorityLastIndex: Int = normalPriorityLastIndex
        for (counter in normalPriorityLastIndex until fullDatesList.size) {
            when (fullDatesList[counter].getPriority()) {
                Constants.PRIORITY_LOW -> {
                    if (counter != lowPriorityLastIndex) {
                        fullDatesList.removeAt(counter).apply {
                            fullDatesList.add(lowPriorityLastIndex++, this)
                        }
                    } else lowPriorityLastIndex++
                }
            }
        }
    }
}