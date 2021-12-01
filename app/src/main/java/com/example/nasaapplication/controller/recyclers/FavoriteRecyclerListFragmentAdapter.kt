package com.example.nasaapplication.controller.recyclers

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.nasaapplication.Constants
import com.example.nasaapplication.controller.recyclers.utils.*
import com.example.nasaapplication.databinding.FavoriteListRecyclerItemPhotoOfDayBinding
import com.example.nasaapplication.databinding.FavoriteListRecyclerItemSearchInNasaBinding
import com.example.nasaapplication.databinding.FavoriteListRecyclerItemSearchInWikiBinding
import com.example.nasaapplication.domain.logic.Favorite
import com.example.nasaapplication.ui.activities.MainActivity

class FavoriteRecyclerListFragmentAdapter (
    private val onListItemClickListener: FavoriteRecyclerListFragmentOnItemClickListener,
    private var favoriteData: MutableList<Favorite>,
    private val mainActivity: MainActivity,
): RecyclerView.Adapter<BaseViewHolder>(), ItemTouchHelperAdapter {

    //region БАЗОВЫЕ МЕТОДЫ ДЛЯ РАБОТЫ АДАПТЕРА
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when(viewType) {
            Constants.DAY_PHOTO_FRAGMENT_INDEX -> {
                val binding: FavoriteListRecyclerItemPhotoOfDayBinding =
                    FavoriteListRecyclerItemPhotoOfDayBinding
                        .inflate(LayoutInflater.from(parent.context), parent,false)
                PhotoOfDayViewHolder(binding.root, mainActivity, onListItemClickListener,
                    favoriteData, this)
            }
            Constants.SEARCH_WIKI_FRAGMENT_INDEX -> {
                val binding: FavoriteListRecyclerItemSearchInWikiBinding =
                    FavoriteListRecyclerItemSearchInWikiBinding
                        .inflate(LayoutInflater.from(parent.context), parent,false)
                SearchInWikiViewHolder(binding.root, mainActivity, onListItemClickListener,
                    favoriteData, this)
            }
            Constants.SEARCH_NASA_ARCHIVE_FRAGMENT_INDEX -> {
                val binding: FavoriteListRecyclerItemSearchInNasaBinding =
                    FavoriteListRecyclerItemSearchInNasaBinding
                        .inflate(LayoutInflater.from(parent.context), parent,false)
                SearchInNASAArchiveViewHolder(binding.root, mainActivity, onListItemClickListener,
                    favoriteData, this)
            }
            else -> {
                val binding: FavoriteListRecyclerItemPhotoOfDayBinding =
                    FavoriteListRecyclerItemPhotoOfDayBinding
                        .inflate(LayoutInflater.from(parent.context), parent,false)
                PhotoOfDayViewHolder(binding.root, mainActivity, onListItemClickListener,
                    favoriteData, this)
            }
        }
    }
    override fun getItemViewType(position: Int): Int {
        return when {
            favoriteData[position].getTypeSource() == Constants.DAY_PHOTO_FRAGMENT_INDEX ->
                Constants.DAY_PHOTO_FRAGMENT_INDEX
            favoriteData[position].getTypeSource() == Constants.SEARCH_WIKI_FRAGMENT_INDEX ->
                Constants.SEARCH_WIKI_FRAGMENT_INDEX
            favoriteData[position].getTypeSource() ==
                    Constants.SEARCH_NASA_ARCHIVE_FRAGMENT_INDEX ->
                Constants.SEARCH_NASA_ARCHIVE_FRAGMENT_INDEX
            else -> Constants.DAY_PHOTO_FRAGMENT_INDEX
        }
    }
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        (holder).bind(favoriteData[position])
    }
    override fun getItemCount(): Int {
        return favoriteData.size
    }
    //endregion

    // Установка favoriteData
    fun setFavoriteData(newFavoriteData: MutableList<Favorite>) {
        this.favoriteData = newFavoriteData
    }

    //region БАЗОВЫЕ МЕТОДЫ ДЛЯ РЕАЛИЗАЦИИ СМАХИВАНИЯ (УДАЛЕНИЯ) ЭЛЕМЕНТОВ
    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (!mainActivity.getUIObserversManager().getIsBlockingOtherFABButtons()) {
            removeAtAndAddInFullDatesList(fromPosition, toPosition)
            favoriteData.removeAt(fromPosition).apply {
                favoriteData.add(toPosition, this)
            }
            notifyItemMoved(fromPosition, toPosition)
        }
    }
    override fun onItemDismiss(position: Int) {
        if (!mainActivity.getUIObserversManager().getIsBlockingOtherFABButtons()) {
            removeAtInFullDatesList(position)
            favoriteData.removeAt(position)
            notifyItemRemoved(position)
        }
    }
    //endregion

    //region МЕТОДЫ ДЛЯ УДАЛЕНИЯ И ДОБАВЛЕНИЯ ЭЛЕМЕНТОВ В ОСНОВНОМ СПИСКЕ
    private fun removeAtInFullDatesList(removedElementIndex: Int) {
        mainActivity.getUIObserversManager()
            .getFacadeFavoriteLogic().removeFavoriteDataByCorrectedData(removedElementIndex)
    }
    fun removeAtAndAddInFullDatesList(removedElementIndex: Int, addedElementIndex: Int) {
        mainActivity.getUIObserversManager()
            .getFacadeFavoriteLogic().removeAndAddFavoriteDataByCorrectedData(
            removedElementIndex, addedElementIndex)
    }
    //endregion

    //region МЕТОД И КЛАСС ДЛЯ ДИНАМИЧЕСКОГО ОБНОВЛЕНИЯ СПИСКА
    fun submitList(newFavoriteData: List<Favorite>) {
        val oldFavoriteData: List<Favorite> = favoriteData
        val diffResult: DiffUtil.DiffResult =
            DiffUtil.calculateDiff(DiffCallback(oldFavoriteData, newFavoriteData))
        favoriteData.clear()
        newFavoriteData.forEach {
            favoriteData.add(it)
        }
        diffResult.dispatchUpdatesTo(this)
    }
    class DiffCallback(
        var oldList: List<Favorite>,
        var newList: List<Favorite>
    ): DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
    //endregion
}