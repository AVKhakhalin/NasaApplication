package com.example.nasaapplication.controller.recyclers.utils

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.nasaapplication.domain.logic.Favorite

abstract class BaseViewHolder(view: View): RecyclerView.ViewHolder(view) {
    abstract fun bind(favoriteData: Favorite)
}