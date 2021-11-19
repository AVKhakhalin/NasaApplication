package com.example.nasaapplication.controller.recyclers.utils

import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.example.nasaapplication.Constants
import com.example.nasaapplication.R
import com.example.nasaapplication.controller.recyclers.FavoriteRecyclerListFragmentAdapter
import com.example.nasaapplication.controller.recyclers.FavoriteRecyclerListFragmentOnItemClickListener
import com.example.nasaapplication.databinding.FavoriteListRecyclerItemSearchInNasaBinding
import com.example.nasaapplication.domain.logic.Favorite
import com.example.nasaapplication.ui.activities.MainActivity

class SearchInNASAArchiveViewHolder(
    val view: View,
    val mainActivity: MainActivity,
    val onListItemClickListener: FavoriteRecyclerListFragmentOnItemClickListener,
    var favoriteData: MutableList<Favorite>,
    val favoriteRecyclerListFragmentAdapter: FavoriteRecyclerListFragmentAdapter
): BaseViewHolder(view), ItemTouchHelperViewHolder {
    override fun bind(itemFavoriteData: Favorite) {
        FavoriteListRecyclerItemSearchInNasaBinding.bind(itemView).apply {
            recyclerItemSearchInNasaItemTitle.text = itemFavoriteData.getTitle()
            recyclerItemSearchInNasaItemDescription.text = itemFavoriteData.getDescription()
            // Загрузка информации по выбранному элементу
            // на странице фрагмента поиска в архиве NASA
            recyclerItemSearchInNasaTypeImage.setOnClickListener {
                onListItemClickListener.onItemClick(itemFavoriteData)
                // Переключение режима нижней навигационной кнопки BottomAppBar
                // с крайнего правого положения в центральное положение
                mainActivity.setIsMain(false)
                mainActivity.getSetBottomNavigationMenu().switchBottomAppBar()
            }
            //region МЕТОДЫ ИЗМЕНЕНИЯ ПРИОРИТЕТОВ ЗАПИСИ
            recyclerItemSearchInNasaPriorityHigh.setOnClickListener {
                itemFavoriteData.setPriority(Constants.PRIORITY_HIGH)
                changeSearchInNASAArchiveImageOnPriority(
                    itemFavoriteData, this.recyclerItemSearchInNasaTypeImage)
                mainActivity.getFacadeFavoriteLogic().priorityRangeFullDatesList()
                favoriteData = mainActivity.getFacadeFavoriteLogic().getFavoriteDataList()
                favoriteRecyclerListFragmentAdapter.notifyDataSetChanged()
            }
            recyclerItemSearchInNasaPriorityNormal.setOnClickListener {
                itemFavoriteData.setPriority(Constants.PRIORITY_NORMAL)
                changeSearchInNASAArchiveImageOnPriority(
                    itemFavoriteData, this.recyclerItemSearchInNasaTypeImage)
                mainActivity.getFacadeFavoriteLogic().priorityRangeFullDatesList()
                favoriteData = mainActivity.getFacadeFavoriteLogic().getFavoriteDataList()
                favoriteRecyclerListFragmentAdapter.notifyDataSetChanged()
            }
            recyclerItemSearchInNasaPriorityLow.setOnClickListener {
                itemFavoriteData.setPriority(Constants.PRIORITY_LOW)
                changeSearchInNASAArchiveImageOnPriority(
                    itemFavoriteData, this.recyclerItemSearchInNasaTypeImage)
                mainActivity.getFacadeFavoriteLogic().priorityRangeFullDatesList()
                favoriteData = mainActivity.getFacadeFavoriteLogic().getFavoriteDataList()
                favoriteRecyclerListFragmentAdapter.notifyDataSetChanged()
            }
            //endregion
            // Изменение картинки элемента в зависимости от его приоритета
            changeSearchInNASAArchiveImageOnPriority(
                itemFavoriteData, this.recyclerItemSearchInNasaTypeImage)
            //region МЕТОДЫ ИЗМЕНЕНИЯ ПОЛОЖЕНИЯ ЭЛЕМЕНТА В СПИСКЕ ПРИ НАЖАТИИ НА СТРЕЛОЧКИ
            recyclerItemSearchInNasaArrowUp.setOnClickListener {
                layoutPosition.takeIf {it > 0}?.also {
                    favoriteRecyclerListFragmentAdapter
                        .removeAtAndAddInFullDatesList(it, it - 1)
                    favoriteData.removeAt(it).apply {
                        favoriteData.add(it - 1, this)
                    }
                    favoriteRecyclerListFragmentAdapter.notifyItemMoved(it, it - 1)
                }
            }
            recyclerItemSearchInNasaArrowDown.setOnClickListener {
                layoutPosition.takeIf {
                    it < favoriteRecyclerListFragmentAdapter.itemCount - 1}?.also {
                    favoriteRecyclerListFragmentAdapter
                        .removeAtAndAddInFullDatesList(it, it + 1)
                    favoriteData.removeAt(it).apply {
                        favoriteData.add(it + 1, this)
                    }
                    favoriteRecyclerListFragmentAdapter.notifyItemMoved(it, it + 1)
                }
            }
            //endregion
            // Отображение описания элемента при нажатии на его заголовок (Title)
            recyclerItemSearchInNasaItemTitle.setOnClickListener {
                itemFavoriteData.setIsShowDescription(!itemFavoriteData.getIsShowDescription())
                recyclerItemSearchInNasaItemDescription.visibility =
                    if (itemFavoriteData.getIsShowDescription()) View.VISIBLE else View.GONE
            }
        }
    }
    //region МЕТОДЫ ItemTouchHelperViewHolder ДЛЯ РАБОТЫ СО СМАХИВАНИЕМ (ВЫДЕЛЕНИЕ И ОЧИСТКА)
    override fun onItemSelected() {
        mainActivity.getThemeColor()?.let {
            itemView.setBackgroundColor(it.getColorPrimaryTypedValue())
        }
    }
    override fun onItemClear() {
        mainActivity.getThemeColor()?.let {
            itemView.setBackgroundColor(it.getColorSecondaryTypedValue())
        }
    }
    //endregion

    // Смена картинки информации с фрагмента "Поиск в архиве NASA" в зависимости от приоритета
    private fun changeSearchInNASAArchiveImageOnPriority(
        itemFavoriteData: Favorite, currentImageView: ImageView
    ) {
        if (itemFavoriteData.getPriority() == 0) {
            currentImageView.setImageDrawable(
                ContextCompat.getDrawable(mainActivity, R.drawable.ic_archive))
        } else if (itemFavoriteData.getPriority() == 1) {
            currentImageView.setImageDrawable(
                ContextCompat.getDrawable(mainActivity, R.drawable.ic_archive_tab))
        } else if (itemFavoriteData.getPriority() == 2) {
            currentImageView.setImageDrawable(
                ContextCompat.getDrawable(mainActivity, R.drawable.ic_archive_tab_bottom))
        } else
            currentImageView.setImageDrawable(
                ContextCompat.getDrawable(mainActivity, R.drawable.ic_archive))
    }
}