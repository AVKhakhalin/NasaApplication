package com.example.nasaapplication.repository.facadeuser.NASAArchive

import com.google.gson.annotations.SerializedName

class NASAArchiveServerResponseItems (
    @field:SerializedName("href") val href: String,
    @field:SerializedName("data") val data: List<NASAArchiveServerResponseDatum>,
    @field:SerializedName("links") val links: List<NASAArchiveServerResponseItemsLinks>
)