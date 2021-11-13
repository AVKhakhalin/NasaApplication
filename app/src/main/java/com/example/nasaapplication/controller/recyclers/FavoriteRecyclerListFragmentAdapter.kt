package com.example.nasaapplication.controller.recyclers

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.nasaapplication.R
import com.example.nasaapplication.controller.ConstantsController
import com.example.nasaapplication.controller.recyclers.utils.BaseViewHolder
import com.example.nasaapplication.controller.recyclers.utils.ItemTouchHelperAdapter
import com.example.nasaapplication.controller.recyclers.utils.ItemTouchHelperViewHolder
import com.example.nasaapplication.databinding.FavoriteListRecyclerItemPhotoOfDayBinding
import com.example.nasaapplication.databinding.FavoriteListRecyclerItemSearchInNasaBinding
import com.example.nasaapplication.databinding.FavoriteListRecyclerItemSearchInWikiBinding
import com.example.nasaapplication.domain.logic.Favorite
import com.example.nasaapplication.ui.ConstantsUi
import com.example.nasaapplication.ui.activities.MainActivity

class FavoriteRecyclerListFragmentAdapter (
    private var onListItemClickListener: FavoriteRecyclerListFragmentOnItemClickListener,
    private var favoriteData: MutableList<Favorite>,
    private var mainActivity: MainActivity,
): RecyclerView.Adapter<BaseViewHolder>(), ItemTouchHelperAdapter {
    //region БАЗОВЫЕ МЕТОДЫ ДЛЯ РАБОТЫ АДАПТЕРА
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when(viewType) {
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
        return when {
            favoriteData[position].getTypeSource() ==
                    ConstantsController.DAY_PHOTO_FRAGMENT_INDEX ->
                ConstantsController.DAY_PHOTO_FRAGMENT_INDEX
            favoriteData[position].getTypeSource() ==
                    ConstantsController.SEARCH_NASA_ARCHIVE_FRAGMENT_INDEX ->
                ConstantsController.SEARCH_NASA_ARCHIVE_FRAGMENT_INDEX
            favoriteData[position].getTypeSource() ==
                    ConstantsController.SEARCH_WIKI_FRAGMENT_INDEX ->
                ConstantsController.SEARCH_WIKI_FRAGMENT_INDEX
            else -> ConstantsController.DAY_PHOTO_FRAGMENT_INDEX
        }
    }
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        (holder).bind(favoriteData[position])
    }
    override fun getItemCount(): Int {
        return favoriteData.size
    }
    //endregion

    inner class PhotoOfDayViewHolder(view: View): BaseViewHolder(view), ItemTouchHelperViewHolder {
        override fun bind(itemFavoriteData: Favorite) {
            FavoriteListRecyclerItemPhotoOfDayBinding.bind(itemView).apply {
                recyclerItemPhotoOfDayItemTitle.text = itemFavoriteData.getTitle()
                recyclerItemPhotoOfDayItemDescription.text = itemFavoriteData.getDescription()
                // Загрузка информации по выбранному элементу на странице фрагмента "Картинка дня"
                recyclerItemPhotoOfDayTypeImage.setOnClickListener {
                    onListItemClickListener.onItemClick(itemFavoriteData)
                    // Переключение режима нижней навигационной кнопки BottomAppBar
                    // с крайнего правого положения в центральное
                    mainActivity.setIsMain(false)
                    mainActivity.switchBottomAppBar(mainActivity)
                }
                //region МЕТОДЫ ИЗМЕНЕНИЯ ПРИОРИТЕТОВ ЗАПИСИ
                recyclerItemPhotoOfDayPriorityHigh.setOnClickListener {
                    itemFavoriteData.setPriority(ConstantsUi.PRIORITY_HIGH)
                    changePhotoOfDayItemImageOnPriority(
                        itemFavoriteData, this.recyclerItemPhotoOfDayTypeImage)
                }
                recyclerItemPhotoOfDayPriorityNormal.setOnClickListener {
                    itemFavoriteData.setPriority(ConstantsUi.PRIORITY_NORMAL)
                    changePhotoOfDayItemImageOnPriority(
                        itemFavoriteData, this.recyclerItemPhotoOfDayTypeImage)
                }
                recyclerItemPhotoOfDayPriorityLow.setOnClickListener {
                    itemFavoriteData.setPriority(ConstantsUi.PRIORITY_LOW)
                    changePhotoOfDayItemImageOnPriority(
                        itemFavoriteData, this.recyclerItemPhotoOfDayTypeImage)
                }
                //endregion
                // Изменение картинки элемента в зависимости от его приоритета
                changePhotoOfDayItemImageOnPriority(
                    itemFavoriteData, this.recyclerItemPhotoOfDayTypeImage)
                //region МЕТОДЫ ИЗМЕНЕНИЯ ПОЛОЖЕНИЯ ЭЛЕМЕНТА В СПИСКЕ ПРИ НАЖАТИИ НА СТРЕЛОЧКИ
                recyclerItemPhotoOfDayArrowUp.setOnClickListener {
                    layoutPosition.takeIf {it > 0}?.also {
                        removeAtAndAddInFullDatesList(it, it - 1)
                        favoriteData.removeAt(it).apply {
                            favoriteData.add(it - 1, this)
                        }
                        notifyItemMoved(it, it - 1)
                    }
                }
                recyclerItemPhotoOfDayArrowDown.setOnClickListener {
                    if (layoutPosition < favoriteData.size - 1) {
                        layoutPosition.takeIf {it < itemCount - 1}?.also {
                            removeAtAndAddInFullDatesList(it, it + 1)
                            favoriteData.removeAt(it).apply {
                                favoriteData.add(it + 1, this)
                            }
                            notifyItemMoved(it, it + 1)
                        }
                    }
                }
                //endregion
                // Отображение описания элемента при нажатии на его заголовок (Title)
                recyclerItemPhotoOfDayItemTitle.setOnClickListener {
                    itemFavoriteData.setIsShowDescription(!itemFavoriteData.getIsShowDescription())
                    recyclerItemPhotoOfDayItemDescription.visibility =
                        if (itemFavoriteData.getIsShowDescription()) View.VISIBLE else View.GONE
                }
            }
        }
        //region МЕТОДЫ ItemTouchHelperViewHolder ДЛЯ РАБОТЫ СО СМАХИВАНИЕМ (ВЫДЕЛЕНИЕ И ОЧИСТКА)
        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
        }
        override fun onItemClear() {
            itemView.setBackgroundColor(mainActivity.getColorSecondary().data)
        }
        //endregion
    }

    inner class SearchInWikiViewHolder(view: View):BaseViewHolder(view), ItemTouchHelperViewHolder {
        override fun bind(itemFavoriteData: Favorite) {
            FavoriteListRecyclerItemSearchInWikiBinding.bind(itemView).apply {
                recyclerItemSearchInWikiItemTitle.text = itemFavoriteData.getTitle()
                recyclerItemSearchInWikiItemDescription.text = itemFavoriteData.getDescription()
                // Загрузка информации по выбранному элементу
                // на странице фрагмента с поиском в Википедии
                recyclerItemSearchInWikiTypeImage.setOnClickListener {
                    onListItemClickListener.onItemClick(itemFavoriteData)
                    // Переключение режима нижней навигационной кнопки BottomAppBar
                    // с крайнего правого положения в центральное
                    mainActivity.setIsMain(false)
                    mainActivity.switchBottomAppBar(mainActivity)
                }
                //region МЕТОДЫ ИЗМЕНЕНИЯ ПРИОРИТЕТОВ ЗАПИСИ
                recyclerItemSearchInWikiPriorityHigh.setOnClickListener {
                    itemFavoriteData.setPriority(ConstantsUi.PRIORITY_HIGH)
                    changeSearchInWikiItemImageOnPriority(
                        itemFavoriteData, this.recyclerItemSearchInWikiTypeImage)
                }
                recyclerItemSearchInWikiPriorityNormal.setOnClickListener {
                    itemFavoriteData.setPriority(ConstantsUi.PRIORITY_NORMAL)
                    changeSearchInWikiItemImageOnPriority(
                        itemFavoriteData, this.recyclerItemSearchInWikiTypeImage)
                }
                recyclerItemSearchInWikiPriorityLow.setOnClickListener {
                    itemFavoriteData.setPriority(ConstantsUi.PRIORITY_LOW)
                    changeSearchInWikiItemImageOnPriority(
                        itemFavoriteData, this.recyclerItemSearchInWikiTypeImage)
                }
                //endregion
                // Изменение картинки элемента в зависимости от его приоритета
                changeSearchInWikiItemImageOnPriority(
                    itemFavoriteData, this.recyclerItemSearchInWikiTypeImage)
                //region МЕТОДЫ ИЗМЕНЕНИЯ ПОЛОЖЕНИЯ ЭЛЕМЕНТА В СПИСКЕ ПРИ НАЖАТИИ НА СТРЕЛОЧКИ
                recyclerItemSearchInWikiArrowUp.setOnClickListener {
                    layoutPosition.takeIf {it > 0}?.also {
                        removeAtAndAddInFullDatesList(it, it - 1)
                        favoriteData.removeAt(it).apply {
                            favoriteData.add(it - 1, this)
                        }
                        notifyItemMoved(it, it - 1)
                    }
                }
                recyclerItemSearchInWikiArrowDown.setOnClickListener {
                    layoutPosition.takeIf {it < itemCount - 1}?.also {
                        removeAtAndAddInFullDatesList(it, it + 1)
                        favoriteData.removeAt(it).apply {
                            favoriteData.add(it + 1, this)
                        }
                        notifyItemMoved(it, it + 1)
                    }
                }
                //endregion
                // Отображение описания элемента при нажатии на его заголовок (Title)
                recyclerItemSearchInWikiItemTitle.setOnClickListener {
                    itemFavoriteData.setIsShowDescription(!itemFavoriteData.getIsShowDescription())
                    recyclerItemSearchInWikiItemDescription.visibility =
                        if (itemFavoriteData.getIsShowDescription()) View.VISIBLE else View.GONE
                }
            }
        }
        //region МЕТОДЫ ItemTouchHelperViewHolder ДЛЯ РАБОТЫ СО СМАХИВАНИЕМ (ВЫДЕЛЕНИЕ И ОЧИСТКА)
        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
        }
        override fun onItemClear() {
            itemView.setBackgroundColor(mainActivity.getColorSecondary().data)
        }
        //endregion
    }

    inner class SearchInNASAArchiveViewHolder(view: View):BaseViewHolder(view),
        ItemTouchHelperViewHolder  {
        override fun bind(itemFavoriteData: Favorite) {
            FavoriteListRecyclerItemSearchInNasaBinding.bind(itemView).apply {
                recyclerItemSearchInNasaItemTitle.text = itemFavoriteData.getTitle()
                recyclerItemSearchInNasaItemDescription.text = itemFavoriteData.getDescription()
                // Загрузка информации по выбранному элементу
                // на странице фрагмента поиска в архиве NASA
                recyclerItemSearchInNasaTypeImage.setOnClickListener {
                    onListItemClickListener.onItemClick(itemFavoriteData)
                    // Переключение режима нижней навигационной кнопки BottomAppBar
                    // с крайнего правого положения в центральное
                    mainActivity.setIsMain(false)
                    mainActivity.switchBottomAppBar(mainActivity)
                }
                //region МЕТОДЫ ИЗМЕНЕНИЯ ПРИОРИТЕТОВ ЗАПИСИ
                recyclerItemSearchInNasaPriorityHigh.setOnClickListener {
                    itemFavoriteData.setPriority(ConstantsUi.PRIORITY_HIGH)
                    changeSearchInNASAArchiveImageOnPriority(
                        itemFavoriteData, this.recyclerItemSearchInNasaTypeImage)
                }
                recyclerItemSearchInNasaPriorityNormal.setOnClickListener {
                    itemFavoriteData.setPriority(ConstantsUi.PRIORITY_NORMAL)
                    changeSearchInNASAArchiveImageOnPriority(
                        itemFavoriteData, this.recyclerItemSearchInNasaTypeImage)
                }
                recyclerItemSearchInNasaPriorityLow.setOnClickListener {
                    itemFavoriteData.setPriority(ConstantsUi.PRIORITY_LOW)
                    changeSearchInNASAArchiveImageOnPriority(
                        itemFavoriteData, this.recyclerItemSearchInNasaTypeImage)
                }
                //endregion
                // Изменение картинки элемента в зависимости от его приоритета
                changeSearchInNASAArchiveImageOnPriority(
                    itemFavoriteData, this.recyclerItemSearchInNasaTypeImage)
                //region МЕТОДЫ ИЗМЕНЕНИЯ ПОЛОЖЕНИЯ ЭЛЕМЕНТА В СПИСКЕ ПРИ НАЖАТИИ НА СТРЕЛОЧКИ
                recyclerItemSearchInNasaArrowUp.setOnClickListener {
                    layoutPosition.takeIf {it > 0}?.also {
                        removeAtAndAddInFullDatesList(it, it - 1)
                        favoriteData.removeAt(it).apply {
                            favoriteData.add(it - 1, this)
                        }
                        notifyItemMoved(it, it - 1)
                    }
                }
                recyclerItemSearchInNasaArrowDown.setOnClickListener {
                    layoutPosition.takeIf {it < itemCount - 1}?.also {
                        removeAtAndAddInFullDatesList(it, it + 1)
                        favoriteData.removeAt(it).apply {
                            favoriteData.add(it + 1, this)
                        }
                        notifyItemMoved(it, it + 1)
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
            itemView.setBackgroundColor(Color.LTGRAY)
        }
        override fun onItemClear() {
            itemView.setBackgroundColor(mainActivity.getColorSecondary().data)
        }
        //endregion
    }

    // Установка favoriteData
    fun setFavoriteData(newFavoriteData: MutableList<Favorite>) {
        this.favoriteData = newFavoriteData
    }

    //region МЕТОДЫ ИЗМЕНЕНИЯ КАРТИНКИ ЭЛЕМЕНТОВ В ЗАВИСИМОСТИ ОТ ИХ ПРИОРИТЕТОВ
    // Смена картинки информации с фрагмента "Картинка дня" в зависимости от приоритета
    private fun changePhotoOfDayItemImageOnPriority(
        itemFavoriteData: Favorite, currentImageView: ImageView) {
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
    // Смена картинки информации с фрагмента "Поиск в Википедии" в зависимости от приоритета
    private fun changeSearchInWikiItemImageOnPriority(
        itemFavoriteData: Favorite, currentImageView: ImageView) {
        if (itemFavoriteData.getPriority() == 0) {
            currentImageView.setImageDrawable(
                ContextCompat.getDrawable(mainActivity, R.drawable.ic_wikipedia))
        } else if (itemFavoriteData.getPriority() == 1) {
            currentImageView.setImageDrawable(
                ContextCompat.getDrawable(mainActivity,
                    R.drawable.ic_wikipedia_priority_normal))
        } else if (itemFavoriteData.getPriority() == 2) {
            currentImageView.setImageDrawable(
                ContextCompat.getDrawable(mainActivity, R.drawable.ic_wikipedia_tab_bottom)
            )
        } else
            currentImageView.setImageDrawable(
                ContextCompat.getDrawable(mainActivity, R.drawable.ic_wikipedia))
    }
    // Смена картинки информации с фрагмента "Поиск в архиве NASA" в зависимости от приоритета
    private fun changeSearchInNASAArchiveImageOnPriority(
        itemFavoriteData: Favorite, currentImageView: ImageView) {
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
    //endregion

    //region БАЗОВЫЕ МЕТОДЫ ДЛЯ РЕАЛИЗАЦИИ СМАХИВАНИЯ (УДАЛЕНИЯ) ЭЛЕМЕНТОВ
    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        removeAtAndAddInFullDatesList(fromPosition,
            if (toPosition > fromPosition) toPosition - 1 else toPosition)
        favoriteData.removeAt(fromPosition).apply {
            favoriteData.add(
                if (toPosition > fromPosition) toPosition - 1 else toPosition, this)
        }
        if (toPosition > fromPosition) {
            notifyItemMoved(fromPosition, toPosition) // Удивительно, но в этом случае все работает
        }
        else {
            notifyItemMoved(fromPosition, toPosition) // Удивительно, но в этом случае все работает
        }
    }
    override fun onItemDismiss(position: Int) {
        removeAtInFullDatesList(position)
        favoriteData.removeAt(position)
        notifyItemRemoved(position)
    }
    //endregion

    //region МЕТОДЫ ДЛЯ УДАЛЕНИЯ И ДОБАВЛЕНИЯ ЭЛЕМЕНТОВ В ОСНОВНОМ СПИСКЕ
    private fun removeAtInFullDatesList(removedElementIndex: Int) {
        mainActivity.removeFavoriteDataByCorrectedData(removedElementIndex)
    }
    private fun removeAtAndAddInFullDatesList(removedElementIndex: Int, addedElementIndex: Int) {
        mainActivity.removeAndAddFavoriteDataByCorrectedData(removedElementIndex, addedElementIndex)
    }
    //endregion
}