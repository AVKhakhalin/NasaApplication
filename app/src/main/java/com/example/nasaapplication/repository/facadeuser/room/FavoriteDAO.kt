package com.example.nasaapplication.repository.facadeuser.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteDAO {
    // Получение всех записей в базе данных
    @Query("SELECT * FROM FavoriteEntity")
    fun all(): List<FavoriteEntity>

    // Удаление записи по номеру id
    @Query("DELETE FROM FavoriteEntity WHERE id=:idForDelete")
    fun deleteQ(idForDelete: Long)

    // Добавление записи в базу данных
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(entity: FavoriteEntity)

    // Получение всех полей таблицы из записи, у которых заданное поле title
    @Query("SELECT * FROM FavoriteEntity WHERE title LIKE :title")
    fun getDataByWord(title: String): List<FavoriteEntity>

    // Удаление всех записей в базе данных
    //    @Delete
//    fun delete(entity: FavoriteEntity)
    @Query("DELETE FROM FavoriteEntity")
    fun deleteAll()

    // Обновление поля title в зависпи по id
    //    @Update
//    fun update(entity: HistoryEntity)
    @Query("UPDATE FavoriteEntity SET title=:newTitle WHERE id=:idForUpdate")
    fun updateDataById(idForUpdate: Long, newTitle: String)

    // Получение списка уникальных записей, у которых поле title равно заданному значению
    @Query("SELECT DISTINCT title FROM FavoriteEntity")
    fun getUniqueListTitles(): List<String>
}