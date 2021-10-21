package com.example.nasaapplication.ui.fragments.dialogs

import android.content.Context
import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.nasaapplication.R
import com.example.nasaapplication.databinding.BottomNavigationLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.navigation.NavigationView

class BottomNavigationDrawerDialogFragment: BottomSheetDialogFragment() {

    private var _binding: BottomNavigationLayoutBinding? = null
    val binding: BottomNavigationLayoutBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = BottomNavigationLayoutBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        super.onViewCreated(view, savedInstanceState)
        binding.navigationView.setNavigationItemSelectedListener { it->
            when(it.itemId){
                R.id.navigation_to_archive ->{
                    Toast.makeText(context,"Перейти в архив", Toast.LENGTH_SHORT).show()
                }
                R.id.navigation_to_send ->{
                    Toast.makeText(context,"Перейти к отправке", Toast.LENGTH_SHORT).show()
                }
                R.id.navigation_to_observe ->{
                    Toast.makeText(context,"Перейти к просмотру", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}