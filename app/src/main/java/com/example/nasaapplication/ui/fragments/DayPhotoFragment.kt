package com.example.nasaapplication.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.nasaapplication.databinding.FragmentDayPhotoBinding

class DayPhotoFragment: Fragment() {
    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
    // Binding
    private var _binding: FragmentDayPhotoBinding? = null
    private val binding: FragmentDayPhotoBinding
        get() {
            return _binding!!
        }
    //endregion

    companion object {
        fun newInstance() = DayPhotoFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDayPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}