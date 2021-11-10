package com.example.nasaapplication.controller.recyclers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nasaapplication.controller.ConstantsController
import com.example.nasaapplication.databinding.FavoriteListRecyclerItemPhotoOfDayBinding
import com.example.nasaapplication.databinding.FavoriteListRecyclerItemSearchInNasaBinding
import com.example.nasaapplication.databinding.FavoriteListRecyclerItemSearchInWikiBinding
import com.example.nasaapplication.domain.logic.Favorite
import com.example.nasaapplication.ui.activities.MainActivity

class FavoriteRecyclerListFragmentAdapter (
    private var onListItemClickListener: FavoriteRecyclerListFragmentOnItemClickListener,
    private var favoriteData: MutableList<Favorite>,
    private var mainActivity: MainActivity
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            ConstantsController.DAY_PHOTO_FRAGMENT_INDEX -> {
                val binding: FavoriteListRecyclerItemPhotoOfDayBinding =
                    FavoriteListRecyclerItemPhotoOfDayBinding
                        .inflate(LayoutInflater.from(parent.context), parent,false)
                PhotoOfDayViewHolder(binding.root)
            }
            ConstantsController.SEARCH_NASA_ARCHIVE_FRAGMENT_INDEX -> {
                val binding: FavoriteListRecyclerItemSearchInNasaBinding =
                    FavoriteListRecyclerItemSearchInNasaBinding
                        .inflate(LayoutInflater.from(parent.context), parent,false)
                SearchInNASAArchiveViewHolder(binding.root)
            }
            ConstantsController.SEARCH_WIKI_FRAGMENT_INDEX -> {
                val binding: FavoriteListRecyclerItemSearchInWikiBinding =
                    FavoriteListRecyclerItemSearchInWikiBinding
                        .inflate(LayoutInflater.from(parent.context), parent,false)
                SearchInWikiViewHolder(binding.root)
            }
            else -> {
                val binding: FavoriteListRecyclerItemPhotoOfDayBinding =
                    FavoriteListRecyclerItemPhotoOfDayBinding
                        .inflate(LayoutInflater.from(parent.context), parent,false)
                PhotoOfDayViewHolder(binding.root)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (favoriteData[position].getTypeSource() == 0)
            ConstantsController.DAY_PHOTO_FRAGMENT_INDEX
        else if (favoriteData[position].getTypeSource() == 1)
            ConstantsController.SEARCH_NASA_ARCHIVE_FRAGMENT_INDEX
        else if (favoriteData[position].getTypeSource() == 2)
            ConstantsController.SEARCH_WIKI_FRAGMENT_INDEX
        else
            ConstantsController.DAY_PHOTO_FRAGMENT_INDEX
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)) {
            ConstantsController.DAY_PHOTO_FRAGMENT_INDEX  -> {
                (holder as PhotoOfDayViewHolder).bind(favoriteData[position])
            }
            ConstantsController.SEARCH_NASA_ARCHIVE_FRAGMENT_INDEX -> {
                (holder as SearchInNASAArchiveViewHolder).bind(favoriteData[position])
            }
            ConstantsController.SEARCH_WIKI_FRAGMENT_INDEX -> {
                (holder as SearchInWikiViewHolder).bind(favoriteData[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return favoriteData.size
    }

    inner class PhotoOfDayViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bind(favoriteData: Favorite) {
            FavoriteListRecyclerItemPhotoOfDayBinding.bind(itemView).apply {
                recyclerItemPhotoOfDayItemTitle.text = favoriteData.getTitle()
                // Смена картинки в зависимости от приоритета
//                recyclerItemPhotoOfDayTypeImage.setImageDrawable(
//                    ContextCompat.getDrawable(mainActivity, R.drawable.ic_favourite_on))
                recyclerItemPhotoOfDayTypeImage.setOnClickListener {
                    onListItemClickListener.onItemClick(favoriteData)
                }
            }
        }
    }

    inner class SearchInNASAArchiveViewHolder(view: View):RecyclerView.ViewHolder(view) {
        fun bind(favoriteData: Favorite) {
            FavoriteListRecyclerItemSearchInNasaBinding.bind(itemView).apply {
                recyclerItemSearchInNasaItemTitle.text = favoriteData.getTitle()
                recyclerItemSearchInNasaTypeImage.setOnClickListener {
                    onListItemClickListener.onItemClick(favoriteData)
                }
            }
        }
    }

    inner class SearchInWikiViewHolder(view: View):RecyclerView.ViewHolder(view) {
        fun bind(favoriteData: Favorite) {
            FavoriteListRecyclerItemSearchInWikiBinding.bind(itemView).apply {
                recyclerItemSearchInWikiItemTitle.text = favoriteData.getTitle()
                recyclerItemSearchInWikiTypeImage.setOnClickListener {
                    onListItemClickListener.onItemClick(favoriteData)
                }
            }
        }
    }
}