package com.example.nasaapplication.controller.recyclers

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.nasaapplication.R
import com.example.nasaapplication.Constants
import com.example.nasaapplication.ui.fragments.contents.SearchNASAArchiveFragment

class SearchNASAArchiveFragmentAdapter(
    private val newNASAArchiveEntityList: MutableList<String> = mutableListOf(),
    private var entitiesLinks: MutableList<String> = mutableListOf(),
    private var entitiesTexts: MutableList<String> = mutableListOf(),
    private var isRecyclerViewWindowHide: Boolean,
    private val searchNASAArchiveFragment: SearchNASAArchiveFragment
):  RecyclerView.Adapter<SearchNASAArchiveFragmentAdapter.SearchNASAArchiveFragmentViewHolder>() {

    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
    // Анимация появления результирующих данных
    private val durationAnimation: Long = 800
    private val transparientValue: Float = 0f
    private val notTransparientValue: Float = 1f
    //endregion

    //region МЕТОДЫ ДЛЯ РАБОТЫ АДАПТЕРА
    class SearchNASAArchiveFragmentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var newNASAArchiveEntityTextView: TextView? = null
        var newNASAArchiveEntityTextViewContainer: ConstraintLayout? = null

        init {
            newNASAArchiveEntityTextView =
                itemView.findViewById(R.id.fragment_search_in_nasa_archive_recycler_item_text_view)
            newNASAArchiveEntityTextViewContainer =
                itemView.findViewById(R.id.fragment_search_in_nasa_archive_recycler_item_container)
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchNASAArchiveFragmentViewHolder {
        val newNASAArchiveItemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_search_in_nasa_archive_recycler_item,
                    parent, false)
        return SearchNASAArchiveFragmentViewHolder(newNASAArchiveItemView)
    }
    override fun onBindViewHolder(
        holder: SearchNASAArchiveFragmentViewHolder,
        position: Int
    ) {
        holder.newNASAArchiveEntityTextView?.let {
            it.text = newNASAArchiveEntityList[position]
        }
        holder.newNASAArchiveEntityTextViewContainer?.let {
            it.setOnClickListener {
                searchNASAArchiveFragment.getMainActivity()?.let {
                    it.getUIObserversManager().clickOnFoundedInNASAInformationItem(
                        searchNASAArchiveFragment, entitiesLinks[position],
                        newNASAArchiveEntityList[position], entitiesTexts[position],
                        durationAnimation, transparientValue, notTransparientValue)
                }
            }
        }
    }
    override fun getItemCount(): Int {
        return newNASAArchiveEntityList.size
    }
    //endregion
}