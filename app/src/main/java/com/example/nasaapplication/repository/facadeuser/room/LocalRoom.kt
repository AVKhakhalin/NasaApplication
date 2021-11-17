package com.example.nasaapplication.repository.facadeuser.room

import com.example.nasaapplication.domain.logic.Favorite

interface LocalRoom {
    fun getAllFavorite():List<Favorite>
    fun saveFavorite(favorite: Favorite)
    fun deleteAllFavorite()
}