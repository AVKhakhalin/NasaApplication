package com.example.nasaapplication.controller.recyclers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.nasaapplication.R
import com.example.nasaapplication.controller.ConstantsController
import com.example.nasaapplication.databinding.FavoriteListRecyclerItemPhotoOfDayBinding
import com.example.nasaapplication.databinding.FavoriteListRecyclerItemSearchInNasaBinding
import com.example.nasaapplication.databinding.FavoriteListRecyclerItemSearchInWikiBinding
import com.example.nasaapplication.domain.logic.Favorite
import com.example.nasaapplication.ui.ConstantsUi
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
                // Загрузка информации по выбранному элементу на странице фрагмента "Картинка дня"
                recyclerItemPhotoOfDayTypeImage.setOnClickListener {
                    onListItemClickListener.onItemClick(favoriteData)
                }
                //region МЕТОДЫ ИЗМЕНЕНИЯ ПРИОРИТЕТОВ ЗАПИСИ
                recyclerItemPhotoOfDayPriorityHigh.setOnClickListener {
                    favoriteData.setPriority(ConstantsUi.PRIORITY_HIGH)
                    changePhotoOfDayItemImageOnPriority(
                        favoriteData, this.recyclerItemPhotoOfDayTypeImage)
                }
                recyclerItemPhotoOfDayPriorityNormal.setOnClickListener {
                    favoriteData.setPriority(ConstantsUi.PRIORITY_NORMAL)
                    changePhotoOfDayItemImageOnPriority(
                        favoriteData, this.recyclerItemPhotoOfDayTypeImage)
                }
                recyclerItemPhotoOfDayPriorityLow.setOnClickListener {
                    favoriteData.setPriority(ConstantsUi.PRIORITY_LOW)
                    changePhotoOfDayItemImageOnPriority(
                        favoriteData, this.recyclerItemPhotoOfDayTypeImage)
                }
                //endregion
                // Изменение картинки элемента в зависимости от его приоритета
                changePhotoOfDayItemImageOnPriority(
                    favoriteData, this.recyclerItemPhotoOfDayTypeImage)
            }
        }
    }

    inner class SearchInNASAArchiveViewHolder(view: View):RecyclerView.ViewHolder(view) {
        fun bind(favoriteData: Favorite) {
            FavoriteListRecyclerItemSearchInNasaBinding.bind(itemView).apply {
                recyclerItemSearchInNasaItemTitle.text = favoriteData.getTitle()
                // Загрузка информации по выбранному элементу
                // на странице фрагмента поиска в архиве NASA
                recyclerItemSearchInNasaTypeImage.setOnClickListener {
                    onListItemClickListener.onItemClick(favoriteData)
                }
                //region МЕТОДЫ ИЗМЕНЕНИЯ ПРИОРИТЕТОВ ЗАПИСИ
                recyclerItemSearchInNasaPriorityHigh.setOnClickListener {
                    favoriteData.setPriority(ConstantsUi.PRIORITY_HIGH)
                    changePhotoOfDayItemImageOnPriority(
                        favoriteData, this.recyclerItemSearchInNasaTypeImage)
                }
                recyclerItemSearchInNasaPriorityNormal.setOnClickListener {
                    favoriteData.setPriority(ConstantsUi.PRIORITY_NORMAL)
                    changePhotoOfDayItemImageOnPriority(
                        favoriteData, this.recyclerItemSearchInNasaTypeImage)
                }
                recyclerItemSearchInNasaPriorityLow.setOnClickListener {
                    favoriteData.setPriority(ConstantsUi.PRIORITY_LOW)
                    changePhotoOfDayItemImageOnPriority(
                        favoriteData, this.recyclerItemSearchInNasaTypeImage)
                }
                //endregion
                // Изменение картинки элемента в зависимости от его приоритета
                changePhotoOfDayItemImageOnPriority(
                    favoriteData, this.recyclerItemSearchInNasaTypeImage)
            }
        }
    }

    inner class SearchInWikiViewHolder(view: View):RecyclerView.ViewHolder(view) {
        fun bind(favoriteData: Favorite) {
            FavoriteListRecyclerItemSearchInWikiBinding.bind(itemView).apply {
                recyclerItemSearchInWikiItemTitle.text = favoriteData.getTitle()
                // Загрузка информации по выбранному элементу
                // на странице фрагмента с поиском в Википедии
                recyclerItemSearchInWikiTypeImage.setOnClickListener {
                    onListItemClickListener.onItemClick(favoriteData)
                }
                //region МЕТОДЫ ИЗМЕНЕНИЯ ПРИОРИТЕТОВ ЗАПИСИ
                recyclerItemSearchInWikiPriorityHigh.setOnClickListener {
                    favoriteData.setPriority(ConstantsUi.PRIORITY_HIGH)
                    changePhotoOfDayItemImageOnPriority(
                        favoriteData, this.recyclerItemSearchInWikiTypeImage)
                }
                recyclerItemSearchInWikiPriorityNormal.setOnClickListener {
                    favoriteData.setPriority(ConstantsUi.PRIORITY_NORMAL)
                    changePhotoOfDayItemImageOnPriority(
                        favoriteData, this.recyclerItemSearchInWikiTypeImage)
                }
                recyclerItemSearchInWikiPriorityLow.setOnClickListener {
                    favoriteData.setPriority(ConstantsUi.PRIORITY_LOW)
                    changePhotoOfDayItemImageOnPriority(
                        favoriteData, this.recyclerItemSearchInWikiTypeImage)
                }
                //endregion
                // Изменение картинки элемента в зависимости от его приоритета
                changePhotoOfDayItemImageOnPriority(
                    favoriteData, this.recyclerItemSearchInWikiTypeImage)

            }
        }
    }

    //region МЕТОДЫ ИЗМЕНЕНИЯ КАРТИНКИ ЭЛЕМЕНТОВ В ЗАВИСИМОСТИ ОТ ИХ ПРИОРИТЕТОВ
    // Смена картинки информации с фрагмента "Картинка дня" в зависимости от приоритета
    private fun changePhotoOfDayItemImageOnPriority(
        favoriteData: Favorite, currentImageView: ImageView) {
        if (favoriteData.getPriority() == 0) {
            currentImageView.setImageDrawable(
                ContextCompat.getDrawable(mainActivity, R.drawable.ic_telescope))
        } else if (favoriteData.getPriority() == 1) {
            currentImageView.setImageDrawable(
                ContextCompat.getDrawable(mainActivity, R.drawable.ic_telescope_tab))
        } else if (favoriteData.getPriority() == 2) {
            currentImageView.setImageDrawable(
                ContextCompat.getDrawable(mainActivity, R.drawable.ic_telescope_tab_bottom))
        } else
            currentImageView.setImageDrawable(
                ContextCompat.getDrawable(mainActivity, R.drawable.ic_telescope))
    }
    // Смена картинки информации с фрагмента "Поиск в Википедии" в зависимости от приоритета
    private fun changeSearchInWikiItemImageOnPriority(
        favoriteData: Favorite, currentImageView: ImageView) {
        if (favoriteData.getPriority() == 0) {
            currentImageView.setImageDrawable(
                ContextCompat.getDrawable(mainActivity, R.drawable.ic_wikipedia))
        } else if (favoriteData.getPriority() == 1) {
            currentImageView.setImageDrawable(
                ContextCompat.getDrawable(mainActivity,
                    R.drawable.ic_wikipedia_priority_normal))
        } else if (favoriteData.getPriority() == 2) {
            currentImageView.setImageDrawable(
                ContextCompat.getDrawable(mainActivity, R.drawable.ic_wikipedia_tab_bottom)
            )
        } else
            currentImageView.setImageDrawable(
                ContextCompat.getDrawable(mainActivity, R.drawable.ic_wikipedia))
    }
    // Смена картинки информации с фрагмента "Поиск в архиве NASA" в зависимости от приоритета
    private fun changeSearchInNASAArchiveImageOnPriority(
        favoriteData: Favorite, currentImageView: ImageView) {
        if (favoriteData.getPriority() == 0) {
            currentImageView.setImageDrawable(
                ContextCompat.getDrawable(mainActivity, R.drawable.ic_archive))
        } else if (favoriteData.getPriority() == 1) {
            currentImageView.setImageDrawable(
                ContextCompat.getDrawable(mainActivity, R.drawable.ic_archive_tab))
        } else if (favoriteData.getPriority() == 2) {
            currentImageView.setImageDrawable(
                ContextCompat.getDrawable(mainActivity, R.drawable.ic_archive_tab_bottom))
        } else
            currentImageView.setImageDrawable(
                ContextCompat.getDrawable(mainActivity, R.drawable.ic_archive))
    }
    //endregion
}