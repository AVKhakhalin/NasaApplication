package com.example.nasaapplication.controller.observers.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nasaapplication.controller.ConstantsController
import com.example.nasaapplication.repository.facadeuser.POD.PODRetrofitImpl
import com.example.nasaapplication.repository.facadeuser.POD.PODServerResponseData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class PODViewModel (
    private val liveDataForViewToObserve: MutableLiveData<PODData> = MutableLiveData(),
    private val retrofitImpl: PODRetrofitImpl = PODRetrofitImpl()
): ViewModel() {

    fun getData(): LiveData<PODData> {
        sendServerRequest()
        return liveDataForViewToObserve
    }

    private fun sendServerRequest() {
        liveDataForViewToObserve.value = PODData.Loading(null)
        if (ConstantsController.API_KEY.isBlank()) {
            PODData.Error(Throwable(ConstantsController.ERROR_NO_API_KEY))
        } else {
            val proba: String = getCurDate()
            retrofitImpl.getRetrofitImpl().getPictureOfTheDay(getCurDate(), ConstantsController.API_KEY).enqueue(object:
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
                            liveDataForViewToObserve.value =
                                PODData.Error(Throwable(ConstantsController.ERROR_UNKNOWN))
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
        val calendar: Calendar = Calendar.getInstance(TimeZone.getDefault())
        val dateYear: Int = calendar.get(Calendar.YEAR)
        val dateMonth: Int = calendar.get(Calendar.MONTH) + 1
        val dateDay: Int = calendar.get(Calendar.DAY_OF_MONTH)
        return "$dateYear-${if (dateMonth < 10) "0$dateMonth" else "$dateMonth"}-${if (dateDay < 10) "0$dateDay" else "$dateDay"}"
    }
}