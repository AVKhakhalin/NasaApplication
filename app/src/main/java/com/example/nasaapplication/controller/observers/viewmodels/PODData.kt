package com.example.nasaapplication.controller.observers.viewmodels

import com.example.nasaapplication.repository.facadeuser.POD.PODServerResponseData

sealed class PODData {
    data class Success(val serverResponseData: PODServerResponseData): PODData()
    data class Error(val error: Throwable): PODData()
    data class Loading(val progress: Int?): PODData()
}