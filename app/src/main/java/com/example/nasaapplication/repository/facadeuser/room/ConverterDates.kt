package com.example.nasaapplication.repository.facadeuser.room

import com.example.nasaapplication.domain.logic.Favorite

fun convertFavoriteEntityToFavorite(entityList: List<FavoriteEntity>): List<Favorite>{
    return entityList.map {
        Favorite().apply {
            this.setTypeSource(it.typeSource)
            this.setPriority(it.priority)
            this.setLinkSource(it.linkSource)
            this.setTitle(it.title)
            this.setDescription(it.description)
            this.setIsShowDescription(false)
            this.setSearchRequest(it.searchRequest)
            this.setLinkImage(it.linkImage)
        }
    }
}

fun convertFavoriteToFavoriteEntity(favorite: Favorite): FavoriteEntity{
    return FavoriteEntity(
        0,
        favorite.getTypeSource(),
        favorite.getPriority(),
        favorite.getLinkSource(),
        favorite.getTitle(),
        favorite.getDescription(),
        favorite.getSearchRequest(),
        favorite.getLinkImage()
    )
}