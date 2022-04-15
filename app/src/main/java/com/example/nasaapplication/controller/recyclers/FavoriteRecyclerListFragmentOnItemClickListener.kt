package com.example.nasaapplication.controller.recyclers

import com.example.nasaapplication.domain.logic.Favorite

interface FavoriteRecyclerListFragmentOnItemClickListener {
    fun onItemClick(date: Favorite)
}