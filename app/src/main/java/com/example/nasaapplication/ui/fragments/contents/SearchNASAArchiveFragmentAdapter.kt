package com.example.nasaapplication.ui.fragments.contents

import android.renderscript.ScriptGroup
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import coil.load
import com.example.nasaapplication.R

class SearchNASAArchiveFragmentAdapter(
    private val newNASAArchiveEntityList: MutableList<String> = mutableListOf(),
    private var entitiesLinks: MutableList<String> = mutableListOf(),
    private var entitiesTexts: MutableList<String> = mutableListOf(),
    private var isRecyclerViewWindowHide: Boolean,
    private val searchNASAArchiveFragment: SearchNASAArchiveFragment
):  RecyclerView.Adapter<SearchNASAArchiveFragmentAdapter.SearchNASAArchiveFragmentViewHolder>() {

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
    ): SearchNASAArchiveFragmentAdapter.SearchNASAArchiveFragmentViewHolder {
        val newNASAArchiveItemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_search_in_nasa_archive_recycler_item, parent, false)
        return SearchNASAArchiveFragmentViewHolder(newNASAArchiveItemView)
    }
    override fun onBindViewHolder(
        holder: SearchNASAArchiveFragmentAdapter.SearchNASAArchiveFragmentViewHolder,
        position: Int
    ) {
        holder.newNASAArchiveEntityTextView?.let {
            it.text = newNASAArchiveEntityList[position]
        }
        holder.newNASAArchiveEntityTextViewContainer?.let {
            it.setOnClickListener {
                // Загрузить найденную картинку
                searchNASAArchiveFragment.binding.searchInNasaArchiveImageView
                    .load(entitiesLinks[position]) {
                    lifecycle(searchNASAArchiveFragment)
                    error(R.drawable.ic_load_error_vector)
                }
                // Показать описание фотографии по запрошенному событию
                searchNASAArchiveFragment.binding.searchInNasaArchiveTitleTextView.text =
                    newNASAArchiveEntityList[position]
                searchNASAArchiveFragment.binding.searchInNasaArchiveDescriptionTextView.text =
                    entitiesTexts[position]

                // Отобразить элементы View для вывода полученной информации
                searchNASAArchiveFragment.binding.fragmentSearchInNasaArchiveGroupElements
                    .visibility = View.VISIBLE
                searchNASAArchiveFragment.binding.searchInNasaArchiveLoadingLayout
                    .visibility = View.INVISIBLE

                // Скрытие списка Recycler View с результатами поиска в архиве NASA
                val constraintLayout =
                    searchNASAArchiveFragment.binding.nasaArchiveEntityListContainer
                val timeLayoutParams: (ConstraintLayout.LayoutParams) =
                    constraintLayout.layoutParams as ConstraintLayout.LayoutParams
                timeLayoutParams.constrainedWidth = true
                isRecyclerViewWindowHide = true
                constraintLayout.layoutParams = timeLayoutParams
                searchNASAArchiveFragment.setIsRecyclerViewWindowHide(isRecyclerViewWindowHide)
                searchNASAArchiveFragment.binding.fragmentSearchInNasaArchiveRecyclerView
                    .visibility = View.INVISIBLE
            }
        }
    }
    override fun getItemCount(): Int {
        return newNASAArchiveEntityList.size
    }
    //endregion
}