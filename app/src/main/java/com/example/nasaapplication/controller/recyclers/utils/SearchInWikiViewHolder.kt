package com.example.nasaapplication.controller.recyclers.utils

import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.example.nasaapplication.Constants
import com.example.nasaapplication.R
import com.example.nasaapplication.controller.recyclers.FavoriteRecyclerListFragmentAdapter
import com.example.nasaapplication.controller.recyclers.FavoriteRecyclerListFragmentOnItemClickListener
import com.example.nasaapplication.databinding.FavoriteListRecyclerItemSearchInWikiBinding
import com.example.nasaapplication.domain.logic.Favorite
import com.example.nasaapplication.ui.activities.MainActivity

class SearchInWikiViewHolder(
    val view: View,
    val mainActivity: MainActivity,
    val onListItemClickListener: FavoriteRecyclerListFragmentOnItemClickListener,
    var favoriteData: MutableList<Favorite>,
    val favoriteRecyclerListFragmentAdapter: FavoriteRecyclerListFragmentAdapter
): BaseViewHolder(view), ItemTouchHelperViewHolder {
    override fun bind(itemFavoriteData: Favorite) {
        FavoriteListRecyclerItemSearchInWikiBinding.bind(itemView).apply {
            recyclerItemSearchInWikiItemTitle.text = itemFavoriteData.getTitle()
            recyclerItemSearchInWikiItemDescription.text = itemFavoriteData.getDescription()
            // Загрузка информации по выбранному элементу
            // на странице фрагмента с поиском в Википедии
            recyclerItemSearchInWikiTypeImage.setOnClickListener {
                if (!mainActivity.getUIObserversManager().getIsBlockingOtherFABButtons()) {
                    onListItemClickListener.onItemClick(itemFavoriteData)
                    // Переключение режима нижней навигационной кнопки BottomAppBar
                    // с крайнего правого положения в центральное положение
                    mainActivity.setIsMain(false)
                    mainActivity.getSetBottomNavigationMenu().switchBottomAppBar()
                }
            }
            //region МЕТОДЫ ИЗМЕНЕНИЯ ПРИОРИТЕТОВ ЗАПИСИ
            recyclerItemSearchInWikiPriorityHigh.setOnClickListener {
                if (!mainActivity.getUIObserversManager().getIsBlockingOtherFABButtons()) {
                    itemFavoriteData.setPriority(Constants.PRIORITY_HIGH)
                    changeSearchInWikiItemImageOnPriority(
                        itemFavoriteData, this.recyclerItemSearchInWikiTypeImage)
                    mainActivity.getUIObserversManager()
                        .getFacadeFavoriteLogic().priorityRangeFullDatesList()
                    favoriteData = mainActivity.getUIObserversManager()
                        .getFacadeFavoriteLogic().getFavoriteDataList()
                    favoriteRecyclerListFragmentAdapter.notifyDataSetChanged()
                }
            }
            recyclerItemSearchInWikiPriorityNormal.setOnClickListener {
                if (!mainActivity.getUIObserversManager().getIsBlockingOtherFABButtons()) {
                    itemFavoriteData.setPriority(Constants.PRIORITY_NORMAL)
                    changeSearchInWikiItemImageOnPriority(
                        itemFavoriteData, this.recyclerItemSearchInWikiTypeImage)
                    mainActivity.getUIObserversManager()
                        .getFacadeFavoriteLogic().priorityRangeFullDatesList()
                    favoriteData = mainActivity.getUIObserversManager()
                        .getFacadeFavoriteLogic().getFavoriteDataList()
                    favoriteRecyclerListFragmentAdapter.notifyDataSetChanged()
                }
            }
            recyclerItemSearchInWikiPriorityLow.setOnClickListener {
                if (!mainActivity.getUIObserversManager().getIsBlockingOtherFABButtons()) {
                    itemFavoriteData.setPriority(Constants.PRIORITY_LOW)
                    changeSearchInWikiItemImageOnPriority(
                        itemFavoriteData, this.recyclerItemSearchInWikiTypeImage)
                    mainActivity.getUIObserversManager()
                        .getFacadeFavoriteLogic().priorityRangeFullDatesList()
                    favoriteData = mainActivity.getUIObserversManager()
                        .getFacadeFavoriteLogic().getFavoriteDataList()
                    favoriteRecyclerListFragmentAdapter.notifyDataSetChanged()
                }
            }
            //endregion
            // Изменение картинки элемента в зависимости от его приоритета
            changeSearchInWikiItemImageOnPriority(
                itemFavoriteData, this.recyclerItemSearchInWikiTypeImage)
            //region МЕТОДЫ ИЗМЕНЕНИЯ ПОЛОЖЕНИЯ ЭЛЕМЕНТА В СПИСКЕ ПРИ НАЖАТИИ НА СТРЕЛОЧКИ
            recyclerItemSearchInWikiArrowUp.setOnClickListener {
                if (!mainActivity.getUIObserversManager().getIsBlockingOtherFABButtons()) {
                    layoutPosition.takeIf { it > 0 }?.also {
                        favoriteRecyclerListFragmentAdapter
                            .removeAtAndAddInFullDatesList(it, it - 1)
                        favoriteData.removeAt(it).apply {
                            favoriteData.add(it - 1, this)
                        }
                        favoriteRecyclerListFragmentAdapter.notifyItemMoved(it, it - 1)
                    }
                }
            }
            recyclerItemSearchInWikiArrowDown.setOnClickListener {
                if (!mainActivity.getUIObserversManager().getIsBlockingOtherFABButtons()) {
                    layoutPosition.takeIf {
                        it < favoriteRecyclerListFragmentAdapter.itemCount - 1
                    }?.also {
                        favoriteRecyclerListFragmentAdapter
                            .removeAtAndAddInFullDatesList(it, it + 1)
                        favoriteData.removeAt(it).apply {
                            favoriteData.add(it + 1, this)
                        }
                        favoriteRecyclerListFragmentAdapter.notifyItemMoved(it, it + 1)
                    }
                }
            }
            //endregion
            // Отображение описания элемента при нажатии на его заголовок (Title)
            recyclerItemSearchInWikiItemTitle.setOnClickListener {
                if (!mainActivity.getUIObserversManager().getIsBlockingOtherFABButtons()) {
                    itemFavoriteData.setIsShowDescription(!itemFavoriteData.getIsShowDescription())
                    recyclerItemSearchInWikiItemDescription.visibility =
                        if (itemFavoriteData.getIsShowDescription()) View.VISIBLE else View.GONE
                }
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

    // Смена картинки информации с фрагмента "Поиск в Википедии" в зависимости от приоритета
    private fun changeSearchInWikiItemImageOnPriority(
        itemFavoriteData: Favorite, currentImageView: ImageView
    ) {
        if (itemFavoriteData.getPriority() == 0) {
            currentImageView.setImageDrawable(
                ContextCompat.getDrawable(mainActivity, R.drawable.ic_wikipedia))
        } else if (itemFavoriteData.getPriority() == 1) {
            currentImageView.setImageDrawable(
                ContextCompat.getDrawable(mainActivity,
                    R.drawable.ic_wikipedia_priority_normal))
        } else if (itemFavoriteData.getPriority() == 2) {
            currentImageView.setImageDrawable(
                ContextCompat.getDrawable(mainActivity, R.drawable.ic_wikipedia_tab_bottom))
        } else
            currentImageView.setImageDrawable(
                ContextCompat.getDrawable(mainActivity, R.drawable.ic_wikipedia))
    }
}