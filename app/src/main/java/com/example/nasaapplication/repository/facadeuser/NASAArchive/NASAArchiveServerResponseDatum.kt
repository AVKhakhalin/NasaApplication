package com.example.nasaapplication.repository.facadeuser.NASAArchive

import com.google.gson.annotations.SerializedName

class NASAArchiveServerResponseDatum (
    @field:SerializedName("center") val center: String?,
    @field:SerializedName("title") val title: String?,
    @field:SerializedName("nasa_id") val nasaID: String?,
    @field:SerializedName("media_type") val mediaType: String?,
    @field:SerializedName("keywords") val keywords: List<String>?,
    @field:SerializedName("date_created") val dateCreated: String?,
    @field:SerializedName("description_508") val description508: String?,
    @field:SerializedName("secondary_creator") val secondaryCreator: String?,
    @field:SerializedName("description") val description: String?,
    @field:SerializedName("photographer") val photographer: String?,
    @field:SerializedName("location") val location: String?
)