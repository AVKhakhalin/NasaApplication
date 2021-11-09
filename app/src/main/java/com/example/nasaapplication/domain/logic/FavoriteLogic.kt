package com.example.nasaapplication.domain.logic

// Класс с логикой проекта - построение и сохранение списка избранных данных
class FavoriteLogic {
    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
    private var datesList: MutableList<FavoriteData> = mutableListOf()
    //endregion

    //region МЕТОДЫ ДОБАВЛЕНИЯ ДАННЫХ В СПИСОК ИЗБРАННЫХ ДАННЫХ
    fun addFavoriteData(newTypeSource: Int,
                        newPriority: Int,
                        newLinkSource: String,
                        newTitle: String,
                        newDescription: String,
                        newSearchRequest: String,
                        newLinkImage: String) {
        datesList.add(FavoriteData(newTypeSource, newPriority, newLinkSource, newTitle,
            newDescription, newSearchRequest, newLinkImage))
    }
    fun addFavoriteData(newTypeSource: Int,
                        newLinkSource: String,
                        newDescription: String,
                        newSearchRequest: String) {
        datesList.add(FavoriteData(newTypeSource, 0, newLinkSource, "",
            newDescription, newSearchRequest, ""))
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