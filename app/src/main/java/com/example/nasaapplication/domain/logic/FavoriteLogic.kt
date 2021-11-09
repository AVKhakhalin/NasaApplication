package com.example.nasaapplication.domain.logic

// Класс с логикой проекта - построение и сохранение списка избранных данных
class FavoriteLogic {
    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
    private var datesList: MutableList<FavoriteData> = mutableListOf()
    //endregion

    //region МЕТОДЫ ДОБАВЛЕНИЯ ДАННЫХ В СПИСОК ИЗБРАННЫХ ДАННЫХ
    fun addFavoriteData(newFavoriteData: FavoriteData): Int {
        var indexSimilarData: Int = -1
        for(counter in 0 until datesList.size) {
            if (datesList[counter].getTypeSource() == newFavoriteData.getTypeSource()) {
                if ((datesList[counter].getLinkSource() == newFavoriteData.getLinkSource())
                    && (datesList[counter].getTitle() == newFavoriteData.getTitle())
                    && (datesList[counter].getDescription() == newFavoriteData.getDescription())
                    && (datesList[counter].getSearchRequest() == newFavoriteData.getSearchRequest())
                    && (datesList[counter].getLinkImage() == newFavoriteData.getLinkImage())) {
                        indexSimilarData = counter
                        break
                    }
                }
        }
        if (indexSimilarData == -1) datesList.add(newFavoriteData)
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
}