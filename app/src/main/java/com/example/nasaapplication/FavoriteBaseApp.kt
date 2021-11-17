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
                if (appInstance != null) {
                    db = Room.databaseBuilder(appInstance!!.applicationContext,
                        FavoriteDataBase::class.java, DB_NAME)
                        .allowMainThreadQueries()
                        .build()
                } else {
                    throw IllegalStateException("appInstance==null")
                }
            }
            return db!!.favoriteDAO()
        }
    }
}