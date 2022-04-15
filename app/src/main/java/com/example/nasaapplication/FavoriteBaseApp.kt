package com.example.nasaapplication

import android.app.Application
import androidx.room.Room
import com.example.nasaapplication.repository.facadeuser.room.FavoriteDAO
import com.example.nasaapplication.repository.facadeuser.room.FavoriteDataBase

class FavoriteBaseApp: Application() {
    override fun onCreate() {
        super.onCreate()
        appInstance = this
    }

    companion object{
        private var appInstance: FavoriteBaseApp? = null
        private var db: FavoriteDataBase? = null
        private const val DB_NAME = "FavoriteDataBase.db"

        fun getFavoriteDAO(): FavoriteDAO {
            if (db == null) {
                appInstance?.let {
                    db = Room.databaseBuilder(it.applicationContext,
                        FavoriteDataBase::class.java, DB_NAME)
                        .allowMainThreadQueries()
                        .build()
                }
                if (appInstance == null) {
                    throw IllegalStateException("appInstance==null")
                }
            }
            db?.let { return it.favoriteDAO() }
            throw IllegalStateException("db==null")
        }
    }
}