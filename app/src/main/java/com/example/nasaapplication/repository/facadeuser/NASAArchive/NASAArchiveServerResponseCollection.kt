package com.example.nasaapplication.repository.facadeuser.NASAArchive

import com.google.gson.annotations.SerializedName

class NASAArchiveServerResponseCollection (
    @field:SerializedName("version") val version: String,
    @field:SerializedName("href") val href: String,
    @field:SerializedName("items") val items: List<NASAArchiveServerResponseItems>
)