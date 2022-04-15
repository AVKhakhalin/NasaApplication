package com.example.nasaapplication.controller.observers.viewmodels.POD

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nasaapplication.R
import com.example.nasaapplication.Constants
import com.example.nasaapplication.repository.facadeuser.POD.PODRetrofitImpl
import com.example.nasaapplication.repository.facadeuser.POD.PODServerResponseData
import com.example.nasaapplication.ui.activities.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class PODViewModel (
    private val liveDataForViewToObserve: MutableLiveData<PODData> = MutableLiveData(),
    private val retrofitImpl: PODRetrofitImpl = PODRetrofitImpl(),
): ViewModel() {

    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
    private var curDate: String = ""
    // Ссылка запроса для сохранения в списке "Избранное"
    private var baseUrl: String = "${Constants.POD_BASE_URL}planetary/apod?date="
    private var requestUrl: String = ""
    // MainActivity
    private var mainActivity: MainActivity? = null
    //endregion

    fun setMainActivity(mainActivity: MainActivity?) {
        this.mainActivity = mainActivity
    }

    fun getRequestUrl(): String {
        return requestUrl
    }

    fun getData(curDate: String): LiveData<PODData> {
        this.curDate = curDate
        sendServerRequest()
        return liveDataForViewToObserve
    }

    private fun sendServerRequest() {
        liveDataForViewToObserve.value = PODData.Loading(null)
        if (Constants.API_KEY.isBlank()) {
            mainActivity?.let {
                PODData.Error(Throwable("${it.resources.getString(R.string.error)}: ${
                    it.resources.getString(R.string.error_no_api_key)}"))
            }
        } else {
            // Сохранение ссылки запроса
            requestUrl = "$baseUrl${getCurDate()}&api_key=${Constants.API_KEY}"
            // Выполнение запроса
            retrofitImpl.getRetrofitImpl().getPictureOfTheDay(getCurDate(),
                Constants.API_KEY).enqueue(object:
                Callback<PODServerResponseData> {
                override fun onResponse(
                    call: Call<PODServerResponseData>,
                    response: Response<PODServerResponseData>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        liveDataForViewToObserve.value =
                            PODData.Success(response.body()!!)
                    } else {
                        val message = response.message()
                        if (message.isNullOrEmpty()) {
                            mainActivity?.let {
                                liveDataForViewToObserve.value =
                                    PODData.Error(Throwable("${
                                        it.resources.getString(R.string.error)}: ${
                                        it.resources.getString(R.string.error_unknown)}"))
                            }
                        } else {
                            liveDataForViewToObserve.value =
                                PODData.Error(Throwable(message))
                        }
                    }
                }

                override fun onFailure(call: Call<PODServerResponseData>, throwable: Throwable) {
                    liveDataForViewToObserve.value = PODData.Error(throwable)
                }
            })
        }
    }

    // Получить текущую дату и выдать её в формате YYYY-MM-DD типа String
    fun getCurDate(): String {
        if (curDate == "") {
            val calendar: Calendar = Calendar.getInstance(TimeZone.getDefault())
            val dateYear: Int = calendar.get(Calendar.YEAR)
            val dateMonth: Int = calendar.get(Calendar.MONTH) + 1
            val dateDay: Int = calendar.get(Calendar.DAY_OF_MONTH)
            return "$dateYear-${if (dateMonth < 10) "0$dateMonth" else "$dateMonth"}-${
                if (dateDay < 10) "0$dateDay" else "$dateDay"}"
        } else {
            return curDate
        }
    }
}