package com.example.nasaapplication.repository.facadeuser.room

import com.example.nasaapplication.domain.logic.Favorite

class LocalRoomImpl (private val localDataSource: FavoriteDAO): LocalRoom {
    override fun getAllFavorite(): List<Favorite>{
        return convertFavoriteEntityToFavorite(localDataSource.all())
    }
    override fun saveFavorite(favorite: Favorite){
        localDataSource.insert(convertFavoriteToFavoriteEntity(favorite))
    }
    override fun deleteAllFavorite(){
        localDataSource.deleteAll()
    }
}