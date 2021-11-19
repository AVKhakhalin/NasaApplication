package com.example.nasaapplication.controller.observers.viewmodels.NASAArchive

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nasaapplication.controller.ConstantsController
import com.example.nasaapplication.repository.ConstantsRepository
import com.example.nasaapplication.repository.facadeuser.NASAArchive.NASAArchiveRetrofitImpl
import com.example.nasaapplication.repository.facadeuser.NASAArchive.NASAArchiveServerResponseWelcome
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NASAArchiveDataViewModel (
    private val liveDataForViewToObserve: MutableLiveData<NASAArchiveData> = MutableLiveData(),
    private val retrofitImpl: NASAArchiveRetrofitImpl = NASAArchiveRetrofitImpl()
): ViewModel() {
    // ЗАДАНИЕ ПЕРЕМЕННЫХ
    // Ссылка запроса для сохранения в списке "Избранное"
    private var baseUrl: String = "${ConstantsRepository.NASA_ARCHIVE_BASE_URL}search?q="
    private var requestUrl: String = ""
    //endregion

    fun getRequestUrl(): String {
        return requestUrl
    }

    fun getData(request: String): LiveData<NASAArchiveData> {
        sendServerRequest(request)
        return liveDataForViewToObserve
    }

    private fun sendServerRequest(request: String) {
        liveDataForViewToObserve.value = NASAArchiveData.Loading(null)
        retrofitImpl.getRetrofitImpl().getNASAArchive(request, "image").enqueue(object:
            Callback<NASAArchiveServerResponseWelcome> {
            override fun onResponse(
                call: Call<NASAArchiveServerResponseWelcome>,
                response: Response<NASAArchiveServerResponseWelcome>
            ) {
                // Сохранение ссылки запроса
                requestUrl = "$baseUrl$request&media_type=image"
                // Выполнение запроса
                if (response.isSuccessful && response.body() != null) {
                    liveDataForViewToObserve.value =
                        NASAArchiveData.Success(response.body()!!)
                } else {
                    val message = response.message()
                    if (message.isNullOrEmpty()) {
                        liveDataForViewToObserve.value =
                            NASAArchiveData.Error(Throwable(ConstantsController.ERROR_UNKNOWN))
                    } else {
                        liveDataForViewToObserve.value =
                            NASAArchiveData.Error(Throwable(message))
                    }
                }
            }

            override fun onFailure(call: Call<NASAArchiveServerResponseWelcome>,
                                   throwable: Throwable) {
                liveDataForViewToObserve.value = NASAArchiveData.Error(throwable)
            }
        })
    }
}