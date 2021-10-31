package com.example.nasaapplication.controller.observers.viewmodels.NASAArchive

import com.example.nasaapplication.repository.facadeuser.NASAArchive.NASAArchiveServerResponseWelcome

sealed class NASAArchiveData {
    data class Success(val serverResponseData: NASAArchiveServerResponseWelcome): NASAArchiveData()
    data class Error(val error: Throwable): NASAArchiveData()
    data class Loading(val progress: Int?): NASAArchiveData()
}