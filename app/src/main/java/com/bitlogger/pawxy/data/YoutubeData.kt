package com.bitlogger.pawxy.data

import com.google.gson.annotations.SerializedName

data class YoutubeData(
    @SerializedName("title")
    val title: String,
    @SerializedName("thumbnail")
    val thumbnail: String,
    @SerializedName("likes")
    val likes: Long,
    @SerializedName("views")
    val views: Int,
    @SerializedName("downloadURL")
    val downloadURL: String,
    @SerializedName("downloadSize")
    val downloadSize: Int,
    @SerializedName("duration")
    val duration: Int
)