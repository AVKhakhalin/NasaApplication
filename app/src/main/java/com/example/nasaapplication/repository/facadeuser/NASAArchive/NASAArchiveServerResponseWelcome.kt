package com.example.nasaapplication.repository.facadeuser.NASAArchive

import com.google.gson.annotations.SerializedName

class NASAArchiveServerResponseWelcome (
    @field:SerializedName("collection") val collection: NASAArchiveServerResponseCollection
)