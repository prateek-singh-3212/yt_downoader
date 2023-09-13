package com.bitlogger.pawxy.network

import com.google.gson.annotations.SerializedName

data class YoutubeURL(
    @SerializedName("data")
    val data: Data
)
data class Data(
    @SerializedName("ytURL")
    val ytURL: String
)