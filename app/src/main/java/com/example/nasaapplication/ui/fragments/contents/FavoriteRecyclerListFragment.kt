package com.example.nasaapplication.ui.fragments.contents

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.nasaapplication.controller.recyclers.FavoriteRecyclerListFragmentAdapter
import com.example.nasaapplication.controller.recyclers.FavoriteRecyclerListFragmentOnItemClickListener
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
        binding.favoriteRecyclerListView.adapter = FavoriteRecyclerListFragmentAdapter(
            object: FavoriteRecyclerListFragmentOnItemClickListener {
                override fun onItemClick(favoriteData: Favorite) {
                    Toast.makeText(requireContext(), favoriteData.getTitle(),
                        Toast.LENGTH_SHORT).show()
                }
            }, mainActivity.getFavoriteDataList(), mainActivity)
    }
}