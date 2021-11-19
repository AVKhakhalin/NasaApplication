package com.example.nasaapplication.repository.facadeuser.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class FavoriteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    // Индекс фрагментов во ViewPager2
    // Расшифровка содержится в файле ConstantsController.kt
    // (0 - самый маленький приоритет, 1 - средний приоритеты, 3 - высокий приоритет)
    val typeSource: Int,
    // Приоритет информации
    // Расшифровка содержится в файле ConstantsUi.kt
    // (0 - самый маленький приоритет, 1 - средний приоритет, 3 - высокий приоритет)
    val priority: Int,
    // Ссылка на источник размещения информации
    val linkSource: String,
    // Заголовок информации
    val title: String,
    // Описание информации
    val description: String,
    // Поисковый запрос в WebView или в архиве NASA
    val searchRequest: String,
    // Ссылка на картнику
    val linkImage: String
)