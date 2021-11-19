package com.example.nasaapplication.repository.facadeuser.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(FavoriteEntity::class), version = 1, exportSchema = false)
abstract class FavoriteDataBase: RoomDatabase() {
    abstract fun favoriteDAO(): FavoriteDAO
}