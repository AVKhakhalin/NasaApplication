package com.example.nasaapplication.domain.logic

class FavoriteData(
) {
    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
    // Индекс фрагментов во ViewPager2
    // Расшифровка содержится в файле ConstantsController.kt
    private var typeSource: Int = -1
    // Приоритет информации
    // (0 - самый маленький приоритет, 1 - средний приоритеты, 3 - высокий приоритет)
    private var priority: Int = -1
    // Ссылка на источник размещения информации
    private var linkSource: String = ""
    // Заголовок информации
    private var title: String = ""
    // Описание информации
    private var description: String = ""
    // Поисковый запрос в WebView или в архиве NASA
    private var searchRequest: String = ""
    // Ссылка на картнику
    private var linkImage: String = ""
    // Второй конструктор класса
    constructor(
        typeSource: Int,
        priority: Int,
        linkSource: String,
        title: String,
        description: String,
        searchRequest: String,
        linkImage: String
    ): this() {
        this.typeSource = typeSource
        this.priority = priority
        this.linkSource = linkSource
        this.title = title
        this.description = description
        this.searchRequest = searchRequest
        this.linkImage = linkImage
    }
    //endregion

    //region Установка сеттеров и геттеров на основные поля
    fun setTypeSource(typeSource: Int) {
        this.typeSource = typeSource
    }
    fun getTypeSource(): Int {
        return typeSource
    }
    fun setPriority(priority: Int) {
        this.priority = priority
    }
    fun getPriority(): Int {
        return priority
    }
    fun setLinkSource(linkSource: String) {
        this.linkSource = linkSource
    }
    fun getLinkSource(): String {
        return linkSource
    }
    fun setTitle(title: String) {
        this.title = title
    }
    fun getTitle(): String {
        return title
    }
    fun setDescription(description: String) {
        this.description = description
    }
    fun getDescription(): String {
        return description
    }
    fun setSearchRequest(searchRequest: String) {
        this.searchRequest = searchRequest
    }
    fun getSearchRequest(): String {
        return searchRequest
    }
    fun setLinkImage(linkImage: String) {
        this.linkImage = linkImage
    }
    fun getLinkImage(): String {
        return linkImage
    }
    //endregion
}