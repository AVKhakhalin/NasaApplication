package com.example.nasaapplication.ui.fragments.contents

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.example.nasaapplication.R
import com.example.nasaapplication.controller.navigation.contents.NavigationContent
import com.example.nasaapplication.controller.navigation.dialogs.NavigationDialogs
import com.example.nasaapplication.controller.observers.viewmodels.PODViewModel
import com.example.nasaapplication.databinding.FragmentSearchWikiBinding
import com.example.nasaapplication.ui.ConstantsUi
import com.example.nasaapplication.ui.activities.MainActivity
import com.example.nasaapplication.ui.utils.ViewBindingFragment
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.stream.Collectors
import javax.net.ssl.HttpsURLConnection

class SearchWikiFragment: ViewBindingFragment<FragmentSearchWikiBinding>(FragmentSearchWikiBinding::inflate) {
    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
    // Navigations
    private var navigationDialogs: NavigationDialogs? = null
    private var navigationContent: NavigationContent? = null
    // ViewModel
    private val viewModel: PODViewModel by lazy {
        ViewModelProviders.of(this).get(PODViewModel::class.java)
    }
    // BottomSheet
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    //endregion

    companion object {
        fun newInstance() = SearchWikiFragment()
        private var isMain = true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //region ПОЛУЧЕНИЕ КЛАССОВ НАВИГАТОРОВ
        navigationDialogs = (context as MainActivity).getNavigationDialogs()
        navigationContent = (context as MainActivity).getNavigationContent()
        //endregion
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    //region МЕТОДЫ РАБОТЫ С BottomSheet
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Установка прозрачного фона для элемента Webview
        binding.webViewContainer.setBackgroundColor(Color.TRANSPARENT)
        // Установка слушателя при нажатии на кнопку поиска в "Википедии"
        binding.inputWikiField.setEndIconOnClickListener {
            if ((binding.inputWikiFieldText.text != null) &&
                (binding.inputWikiFieldText.text!!.length <=
                        binding.inputWikiField.counterMaxLength)) {
                    showUrlInWiki("${ConstantsUi.WIKI_URL}${
                                binding.inputWikiFieldText.text.toString()}")
            }
        }

        // Установка BOTTOM NAVIGATION MENU
        setBottomAppBar(view)
    }

    fun showUrlInWiki(urlString:String){
        val url = URL(urlString)
        Thread{
            val urlConnection = url.openConnection() as HttpsURLConnection
            urlConnection.requestMethod = ConstantsUi.SHOWURLINWIKI_METHOD_NAME
            urlConnection.readTimeout = ConstantsUi.SHOWURLINWIKI_READ_TIME_OUT
            var reader: BufferedReader? = null
            try {
                reader = BufferedReader(InputStreamReader(urlConnection.inputStream))
            } catch (exception: FileNotFoundException) {
                urlConnection.disconnect()
            }
            if (reader != null) {
                val result = getLines(reader)
                val handler = Handler(Looper.getMainLooper())
                handler.post {
                    binding.webViewContainer.loadDataWithBaseURL(
                        null,
                        result,
                        ConstantsUi.SHOWURLINWIKI_TEXT_CHARSER,
                        ConstantsUi.SHOWURLINWIKI_ENCODING,
                        null)
                }
            } else {
                val handler = Handler(Looper.getMainLooper())
                handler.post {
                    binding.webViewContainer.loadDataWithBaseURL(
                        null,
                        resources.getString(R.string.error_wiki_empty_request),
                        ConstantsUi.SHOWURLINWIKI_TEXT_CHARSER,
                        ConstantsUi.SHOWURLINWIKI_ENCODING,
                        null)
                }
            }
            urlConnection.disconnect()
        }.start()
    }

    @RequiresApi(Build.VERSION_CODES.N) // TODO: Доработать, заменить на метод, независящий от версии
    private fun getLines(reader: BufferedReader): String {
        return reader.lines().collect(Collectors.joining("\n"))
    }

//    private fun setBottomSheetBehavior(bottomSheet: ConstraintLayout) {
//        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
//        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
//    }
    //endregion

    //region МЕТОДЫ ДЛЯ РАБОТЫ С BOTTOM NAVIGATION MENU
    private fun setBottomAppBar(view: View) {
        val context = activity as MainActivity
        context.setSupportActionBar(binding.bottomNavigationMenu.bottomAppBar)
        setHasOptionsMenu(true)

        binding.bottomNavigationMenu.bottomAppBarFab.setOnClickListener {
            switchBottomAppBar(context)
        }
    }

    // Переключение режима нижней навигационной кнопки BottomAppBar
    // с центрального на крайнее левое положение и обратно
    private fun switchBottomAppBar(context: MainActivity) {
        if (isMain) {
            isMain = false
            binding.bottomNavigationMenu.bottomAppBar.navigationIcon = null
            binding.bottomNavigationMenu.bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
            binding.bottomNavigationMenu.bottomAppBarFab.setImageDrawable(
                ContextCompat.getDrawable(
                    context, R.drawable.ic_back_fab
                )
            )
            binding.bottomNavigationMenu.bottomAppBar.replaceMenu(R.menu.bottom_menu_bottom_bar_other_screen)
        } else {
            isMain = true
            binding.bottomNavigationMenu.bottomAppBar.navigationIcon =
                ContextCompat.getDrawable(context, R.drawable.ic_hamburger_menu_bottom_bar)
            binding.bottomNavigationMenu.bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
            binding.bottomNavigationMenu.bottomAppBarFab.setImageDrawable(
                ContextCompat.getDrawable(
                    context, R.drawable.ic_plus_fab
                )
            )
            binding.bottomNavigationMenu.bottomAppBar.replaceMenu(R.menu.bottom_menu_bottom_bar)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
//        inflater.inflate(R.menu.bottom_menu_bottom_bar, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.app_bar_save -> toast("Сохранение")
            R.id.app_bar_settings -> navigationContent?.let{
                requireActivity().findViewById<ViewPager>(R.id.view_pager).visibility =
                    View.INVISIBLE
                requireActivity().findViewById<TabLayout>(R.id.tab_layout).visibility =
                    View.INVISIBLE
                requireActivity().findViewById<FrameLayout>(R.id.activity_fragments_container)
                    .visibility = View.VISIBLE
                it.showSettingsFragment(false)
            }
            R.id.app_bar_search -> toast("Поиск")
            android.R.id.home -> {
                toast("SearchWikiFragment Бургер кнопка")
                navigationDialogs?.let {
                    it.showBottomNavigationDrawerDialogFragment(requireActivity())
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
    //endregion

    // Метод для отображения сообщения в виде Toast
    private fun Fragment.toast(string: String?) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).apply {
            setGravity(Gravity.BOTTOM, 0, 250)
            show()
        }
    }
}