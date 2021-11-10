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
        val newNASAArchiveItemView =
            LayoutInflater.from(parent.context)
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
                if (!searchNASAArchiveFragment.getIsBlockingOtherFABButtons()) {
                    // Сохранение ссылки на картинку, заголовка и описания в "Избранное"
                    searchNASAArchiveFragment.getMainActivity()
                        .setListFavoriteDataLinkImage(entitiesLinks[position])
                    searchNASAArchiveFragment.getMainActivity()
                        .setListFavoriteDataTitle(newNASAArchiveEntityList[position])
                    searchNASAArchiveFragment.getMainActivity()
                        .setListFavoriteDataDescription(entitiesTexts[position])
                    // Анимированное появление найденной картинки по запросу в архиве NASA
                    searchNASAArchiveFragment.binding.searchInNasaArchiveImageView.alpha =
                        transparientValue
                    searchNASAArchiveFragment.binding.searchInNasaArchiveImageView
                        .load(entitiesLinks[position]) {
                            lifecycle(searchNASAArchiveFragment)
                            error(R.drawable.ic_load_error_vector)
                            // Анимация появления картинки
                            searchNASAArchiveFragment.binding.searchInNasaArchiveImageView.animate()
                                .alpha(notTransparientValue)
                                .setDuration(durationAnimation)
                                .setListener(object: AnimatorListenerAdapter() {
                                    override fun onAnimationEnd(animation: Animator) {
                                        searchNASAArchiveFragment.binding
                                            .searchInNasaArchiveImageView.isClickable = true
                                    }
                                })
                        }

                    // Анимационный показ заголовка и описания фотографии по запрошенному событию
                    searchNASAArchiveFragment.binding.searchInNasaArchiveTitleTextView.alpha =
                        transparientValue
                    searchNASAArchiveFragment.binding.searchInNasaArchiveTitleTextView.text =
                        newNASAArchiveEntityList[position]
                    searchNASAArchiveFragment.binding.searchInNasaArchiveDescriptionTextView.alpha =
                        transparientValue
                    searchNASAArchiveFragment.binding.searchInNasaArchiveDescriptionTextView.text =
                        entitiesTexts[position]
                    // Анимация появления заголовка картинки
                    searchNASAArchiveFragment.binding.searchInNasaArchiveTitleTextView.animate()
                        .alpha(notTransparientValue)
                        .setDuration(durationAnimation)
                        .setListener(object: AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                searchNASAArchiveFragment.binding.searchInNasaArchiveTitleTextView
                                    .isClickable = true
                            }
                        })
                    // Анимация появления описания картинки
                    searchNASAArchiveFragment.binding.searchInNasaArchiveDescriptionTextView
                        .animate()
                        .alpha(notTransparientValue)
                        .setDuration(durationAnimation)
                        .setListener(object: AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                searchNASAArchiveFragment.binding
                                    .searchInNasaArchiveDescriptionTextView.isClickable = true
                            }
                        })

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
    }
    override fun getItemCount(): Int {
        return newNASAArchiveEntityList.size
    }
    //endregion
}