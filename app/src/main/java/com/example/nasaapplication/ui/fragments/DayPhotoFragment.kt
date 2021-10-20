package com.example.nasaapplication.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import coil.load
import com.example.nasaapplication.R
import com.example.nasaapplication.controller.observers.viewmodels.PODData
import com.example.nasaapplication.controller.observers.viewmodels.PODViewModel
import com.example.nasaapplication.databinding.FragmentDayPhotoBinding
import com.example.nasaapplication.ui.ConstantsUi
import com.google.android.material.bottomsheet.BottomSheetBehavior


class DayPhotoFragment: Fragment() {
    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
    private val viewModel: PODViewModel by lazy {
        ViewModelProviders.of(this).get(PODViewModel::class.java)
    }
    // BottomSheet
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var bottomSheetDescriptionTitle: TextView
    private lateinit var bottomSheetDescriptionText: TextView
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getData()
//            .observe(this@DayPhotoFragment, Observer<PODData> { renderData(it) })
            .observe(viewLifecycleOwner, Observer<PODData> { renderData(it) })
    }

    private fun renderData(data: PODData) {
        when (data) {
            is PODData.Success -> {
                val serverResponseData = data.serverResponseData
                val url = serverResponseData.url
                if (url.isNullOrEmpty()) {
                    //showError("Сообщение, что ссылка пустая")
                    toast("Link is empty")
                } else {
                    //showSuccess()
                    binding.pODImageView.load(url) {
                        lifecycle(this@DayPhotoFragment)
                        error(R.drawable.ic_load_error_vector)
//                        placeholder(R.drawable.ic_downloading)
                    }
                    // Показать описание фотографии дня
                    bottomSheetDescriptionTitle.setText(serverResponseData.title)
                    bottomSheetDescriptionText.setText(serverResponseData.explanation)

                    binding.pODImageView.visibility = View.VISIBLE
                    binding.pODLoadingLayout.visibility = View.INVISIBLE
                }
            }
            is PODData.Loading -> {
                //showLoading()
                binding.pODLoadingLayout.visibility = View.VISIBLE
                binding.pODImageView.visibility = View.INVISIBLE
            }
            is PODData.Error -> {
                //showError(data.error.message)
                toast(data.error.message)
            }
        }
    }

    private fun Fragment.toast(string: String?) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).apply {
            setGravity(Gravity.BOTTOM, 0, 250)
            show()
        }
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

    //region МЕТОДЫ РАБОТЫ С BottomSheet
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBottomSheetBehavior(view.findViewById(R.id.bottom_sheet_container))
        bottomSheetDescriptionTitle = view.findViewById(R.id.bottom_sheet_description_title)
        bottomSheetDescriptionText = view.findViewById(R.id.bottom_sheet_description_text)

        binding.inputWikiField.setEndIconOnClickListener {
            if ((binding.inputWikiFieldText.text != null) &&
                (binding.inputWikiFieldText.text!!.length <=
                        binding.inputWikiField.counterMaxLength)) {
                startActivity(Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(
                        "${ConstantsUi.WIKI_URL}${
                            binding.inputWikiFieldText.text.toString()
                        }"
                    )
                })
            }
        }
    }

    private fun setBottomSheetBehavior(bottomSheet: ConstraintLayout) {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }
    //endregion
}