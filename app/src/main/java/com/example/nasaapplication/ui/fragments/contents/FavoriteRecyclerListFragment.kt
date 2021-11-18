package com.example.nasaapplication.ui.fragments.contents

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.nasaapplication.R
import com.example.nasaapplication.Constants
import com.example.nasaapplication.controller.recyclers.FavoriteRecyclerListFragmentAdapter
import com.example.nasaapplication.controller.recyclers.FavoriteRecyclerListFragmentOnItemClickListener
import com.example.nasaapplication.controller.recyclers.utils.ItemTouchHelperViewHolder
import com.example.nasaapplication.databinding.FavoriteListRecyclerBinding
import com.example.nasaapplication.domain.logic.Favorite
import com.example.nasaapplication.ui.activities.MainActivity
import com.example.nasaapplication.ui.utils.ViewBindingFragment

class FavoriteRecyclerListFragment(
): ViewBindingFragment<FavoriteListRecyclerBinding>(FavoriteListRecyclerBinding::inflate) {

    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
    var adapter: FavoriteRecyclerListFragmentAdapter? = null
    // MainActivity
    private var mainActivity: MainActivity? = null
    //endregion

    // Передача адаптера
    @JvmName("getAdapter1")
    fun getAdapter(): FavoriteRecyclerListFragmentAdapter? {
        return adapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    companion object {
        fun newInstance() = FavoriteRecyclerListFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity?.let { mainActivity ->
            // Изменение вида Bottom Navigation Menu
            mainActivity.setIsMain(true)
            mainActivity.getSetBottomNavigationMenu().switchBottomAppBar()

            // Recycler
            adapter = FavoriteRecyclerListFragmentAdapter(
            object: FavoriteRecyclerListFragmentOnItemClickListener {
                override fun onItemClick(favoriteData: Favorite) {
                    // Открытие данных во фрагментах
                    when(favoriteData.getTypeSource()) {
                        Constants.DAY_PHOTO_FRAGMENT_INDEX -> {
                            // Очистка текущей информации для списка "Избранное"
                            // при переключении на фрагмент "Картинка дня"
                            mainActivity.setListFavoriteEmptyData()
                            // Открытие выбранной информации во фрагменте "Картинка дня"
                            mainActivity.getViewPager().currentItem =
                                Constants.DAY_PHOTO_FRAGMENT_INDEX
                            (mainActivity.getViewPagerAdapter()
                                .getFragments()[Constants.DAY_PHOTO_FRAGMENT_INDEX]
                                    as DayPhotoFragment).setAndShowFavoriteData(favoriteData)
                            mainActivity.binding.activityFragmentsContainer.visibility =
                                View.INVISIBLE
                            mainActivity.binding.transparentBackground.visibility = View.VISIBLE
                        }
                        Constants.SEARCH_WIKI_FRAGMENT_INDEX -> {
                            // Очистка текущей информации для списка "Избранное"
                            // при переключении на фрагмент с поиском в Википедии
                            mainActivity.setListFavoriteEmptyData()
                            // Открытие выбранной информации во фрагменте с поиском в Википедии
                            mainActivity.getViewPager().currentItem =
                                Constants.SEARCH_WIKI_FRAGMENT_INDEX
                            (mainActivity.getViewPagerAdapter()
                                .getFragments()[Constants.SEARCH_WIKI_FRAGMENT_INDEX]
                                    as SearchWikiFragment).setAndShowFavoriteData(favoriteData)
                            mainActivity.binding.activityFragmentsContainer.visibility = View.INVISIBLE
                            mainActivity.binding.transparentBackground.visibility = View.VISIBLE
                        }
                        Constants.SEARCH_NASA_ARCHIVE_FRAGMENT_INDEX -> {
                            // Очистка текущей информации для списка "Избранное"
                            // при переключении на фрагмент с поиском в архиве NASA
                            mainActivity.setListFavoriteEmptyData()
                            // Открытие выбранной информации во фрагменте с поиском в архиве NASA
                            mainActivity.binding.viewPager.visibility = View.VISIBLE
                            mainActivity.binding.tabLayout.visibility = View.VISIBLE
                            mainActivity.binding.activityFragmentsContainer.visibility = View.INVISIBLE

                            mainActivity.getViewPager().currentItem =
                                Constants.SEARCH_NASA_ARCHIVE_FRAGMENT_INDEX
                            (mainActivity.getViewPagerAdapter()
                                .getFragments()[Constants.SEARCH_NASA_ARCHIVE_FRAGMENT_INDEX]
                                    as SearchNASAArchiveFragment).setAndShowFavoriteData(favoriteData)
                            mainActivity.binding.activityFragmentsContainer.visibility = View.INVISIBLE
                            mainActivity.binding.transparentBackground.visibility = View.VISIBLE
                        }
                        else -> {
                            mainActivity.toast("${getString(R.string.error)}: ${
                                getString(R.string.unknown_type_source_favorite_data)}")
                        }
                    }
                }
            }, mainActivity.getFacadeFavoriteLogic().getFavoriteDataList(), mainActivity)
            binding.favoriteRecyclerListView.adapter = adapter
            adapter?.let { ItemTouchHelper(ItemTouchHelperCallback(it))
                .attachToRecyclerView(binding.favoriteRecyclerListView) }
        }
    }
}
//region КЛАСС С МЕТОДАМИ ДЛЯ ДОБАВЛЕНИЯ ВОЗМОЖНОСТИ СМАХИВАНИЯ ЭЛЕМЕНТОВ СПИСКА "ИЗБРАННОЕ"
class ItemTouchHelperCallback(private val adapter: FavoriteRecyclerListFragmentAdapter):
    ItemTouchHelper.Callback() {
    override fun isLongPressDragEnabled(): Boolean {
        return true
    }
    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(
            dragFlags,
            swipeFlags
        )
    }
    override fun onMove(
        recyclerView: RecyclerView,
        source: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        adapter.onItemMove(source.adapterPosition, target.adapterPosition)
        return true
    }
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
        adapter.onItemDismiss(viewHolder.adapterPosition)
    }
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            val itemViewHolder = viewHolder as ItemTouchHelperViewHolder
            itemViewHolder.onItemSelected()
        }
        super.onSelectedChanged(viewHolder, actionState)
    }
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        val itemViewHolder = viewHolder as ItemTouchHelperViewHolder
        itemViewHolder.onItemClear()
    }
}
//endregion