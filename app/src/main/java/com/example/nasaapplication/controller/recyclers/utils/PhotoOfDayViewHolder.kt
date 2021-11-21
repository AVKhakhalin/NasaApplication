package com.example.nasaapplication.controller.recyclers.utils

import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.example.nasaapplication.Constants
import com.example.nasaapplication.R
import com.example.nasaapplication.controller.recyclers.FavoriteRecyclerListFragmentAdapter
import com.example.nasaapplication.controller.recyclers.FavoriteRecyclerListFragmentOnItemClickListener
import com.example.nasaapplication.databinding.FavoriteListRecyclerItemPhotoOfDayBinding
import com.example.nasaapplication.domain.logic.Favorite
import com.example.nasaapplication.ui.activities.MainActivity

class PhotoOfDayViewHolder(
    val view: View,
    val mainActivity: MainActivity,
    val onListItemClickListener: FavoriteRecyclerListFragmentOnItemClickListener,
    var favoriteData: MutableList<Favorite>,
    val favoriteRecyclerListFragmentAdapter: FavoriteRecyclerListFragmentAdapter
): BaseViewHolder(view), ItemTouchHelperViewHolder {
    override fun bind(itemFavoriteData: Favorite) {
        FavoriteListRecyclerItemPhotoOfDayBinding.bind(itemView).apply {
            recyclerItemPhotoOfDayItemTitle.text = itemFavoriteData.getTitle()
            recyclerItemPhotoOfDayItemDescription.text = itemFavoriteData.getDescription()
            // Загрузка информации по выбранному элементу на странице фрагмента "Картинка дня"
            recyclerItemPhotoOfDayTypeImage.setOnClickListener {
                if (!mainActivity.getUIObserversManager().getIsBlockingOtherFABButtons()) {
                    onListItemClickListener.onItemClick(itemFavoriteData)
                    // Переключение режима нижней навигационной кнопки BottomAppBar
                    // с крайнего правого положения в центральное положение
                    mainActivity.setIsMain(false)
                    mainActivity.getSetBottomNavigationMenu().switchBottomAppBar()
                }
            }
            //region МЕТОДЫ ИЗМЕНЕНИЯ ПРИОРИТЕТОВ ЗАПИСИ
            recyclerItemPhotoOfDayPriorityHigh.setOnClickListener {
                if (!mainActivity.getUIObserversManager().getIsBlockingOtherFABButtons()) {
                    itemFavoriteData.setPriority(Constants.PRIORITY_HIGH)
                    changePhotoOfDayItemImageOnPriority(
                        itemFavoriteData, this.recyclerItemPhotoOfDayTypeImage)
                    mainActivity.getUIObserversManager()
                        .getFacadeFavoriteLogic().priorityRangeFullDatesList()
                    favoriteData = mainActivity.getUIObserversManager()
                        .getFacadeFavoriteLogic().getFavoriteDataList()
                    favoriteRecyclerListFragmentAdapter.notifyDataSetChanged()
                }
            }
            recyclerItemPhotoOfDayPriorityNormal.setOnClickListener {
                if (!mainActivity.getUIObserversManager().getIsBlockingOtherFABButtons()) {
                    itemFavoriteData.setPriority(Constants.PRIORITY_NORMAL)
                    changePhotoOfDayItemImageOnPriority(
                        itemFavoriteData, this.recyclerItemPhotoOfDayTypeImage
                    )
                    mainActivity.getUIObserversManager()
                        .getFacadeFavoriteLogic().priorityRangeFullDatesList()
                    favoriteData = mainActivity.getUIObserversManager()
                        .getFacadeFavoriteLogic().getFavoriteDataList()
                    favoriteRecyclerListFragmentAdapter.notifyDataSetChanged()
                }
            }
            recyclerItemPhotoOfDayPriorityLow.setOnClickListener {
                if (!mainActivity.getUIObserversManager().getIsBlockingOtherFABButtons()) {
                    itemFavoriteData.setPriority(Constants.PRIORITY_LOW)
                    changePhotoOfDayItemImageOnPriority(
                        itemFavoriteData, this.recyclerItemPhotoOfDayTypeImage)
                    mainActivity.getUIObserversManager()
                        .getFacadeFavoriteLogic().priorityRangeFullDatesList()
                    favoriteData = mainActivity.getUIObserversManager()
                        .getFacadeFavoriteLogic().getFavoriteDataList()
                    favoriteRecyclerListFragmentAdapter.notifyDataSetChanged()
                }
            }
            //endregion
            // Изменение картинки элемента в зависимости от его приоритета
            changePhotoOfDayItemImageOnPriority(
                itemFavoriteData, this.recyclerItemPhotoOfDayTypeImage)
            //region МЕТОДЫ ИЗМЕНЕНИЯ ПОЛОЖЕНИЯ ЭЛЕМЕНТА В СПИСКЕ ПРИ НАЖАТИИ НА СТРЕЛОЧКИ
            recyclerItemPhotoOfDayArrowUp.setOnClickListener {
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
            recyclerItemPhotoOfDayArrowDown.setOnClickListener {
                if (!mainActivity.getUIObserversManager().getIsBlockingOtherFABButtons()) {
                    if (layoutPosition < favoriteData.size - 1) {
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
            }
            //endregion
            // Отображение описания элемента при нажатии на его заголовок (Title)
            recyclerItemPhotoOfDayItemTitle.setOnClickListener {
                if (!mainActivity.getUIObserversManager().getIsBlockingOtherFABButtons()) {
                    itemFavoriteData.setIsShowDescription(!itemFavoriteData.getIsShowDescription())
                    recyclerItemPhotoOfDayItemDescription.visibility =
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
            itemView.setBackgroundColor(it.getColorSecondaryTypedValue()
            )
        }
    }
    //endregion

    // Смена картинки информации с фрагмента "Картинка дня" в зависимости от приоритета
    private fun changePhotoOfDayItemImageOnPriority(
        itemFavoriteData: Favorite, currentImageView: ImageView
    ) {
        if (itemFavoriteData.getPriority() == 0) {
            currentImageView.setImageDrawable(
                ContextCompat.getDrawable(mainActivity, R.drawable.ic_telescope))
        } else if (itemFavoriteData.getPriority() == 1) {
            currentImageView.setImageDrawable(
                ContextCompat.getDrawable(mainActivity, R.drawable.ic_telescope_tab))
        } else if (itemFavoriteData.getPriority() == 2) {
            currentImageView.setImageDrawable(
                ContextCompat.getDrawable(mainActivity, R.drawable.ic_telescope_tab_bottom))
        } else
            currentImageView.setImageDrawable(
                ContextCompat.getDrawable(mainActivity, R.drawable.ic_telescope))
    }
}