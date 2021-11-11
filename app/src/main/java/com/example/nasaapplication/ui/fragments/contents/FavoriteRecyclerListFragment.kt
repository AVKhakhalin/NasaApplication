package com.example.nasaapplication.ui.fragments.contents

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.nasaapplication.controller.recyclers.FavoriteRecyclerListFragmentAdapter
import com.example.nasaapplication.controller.recyclers.FavoriteRecyclerListFragmentOnItemClickListener
import com.example.nasaapplication.controller.recyclers.utils.ItemTouchHelperViewHolder
import com.example.nasaapplication.databinding.FavoriteListRecyclerBinding
import com.example.nasaapplication.domain.logic.Favorite
import com.example.nasaapplication.ui.activities.MainActivity
import com.example.nasaapplication.ui.utils.ViewBindingFragment

class FavoriteRecyclerListFragment(
    private val mainActivity: MainActivity
):
    ViewBindingFragment<FavoriteListRecyclerBinding>(FavoriteListRecyclerBinding::inflate) {

    companion object {
        fun newInstance(mainActivity: MainActivity) = FavoriteRecyclerListFragment(mainActivity)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Изменение вида Bottom Navigation Menu
        mainActivity.setIsMain(true)
        mainActivity.switchBottomAppBar(mainActivity)

        // Recycler
        val adapter = FavoriteRecyclerListFragmentAdapter(
        object: FavoriteRecyclerListFragmentOnItemClickListener {
            override fun onItemClick(favoriteData: Favorite) {
                Toast.makeText(requireContext(), favoriteData.getTitle(),
                    Toast.LENGTH_SHORT).show()
            }
        }, mainActivity.getFavoriteDataList(), mainActivity)
        binding.favoriteRecyclerListView.adapter = adapter
        ItemTouchHelper(ItemTouchHelperCallback(adapter))
            .attachToRecyclerView(binding.favoriteRecyclerListView)
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