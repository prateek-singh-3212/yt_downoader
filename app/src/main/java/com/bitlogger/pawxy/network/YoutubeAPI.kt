package com.bitlogger.pawxy.network

import com.bitlogger.pawxy.data.YoutubeData
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface YoutubeAPI {
    @POST(".")
    suspend fun getVideoData(@Body videoUrl: YoutubeURL): YoutubeData

    companion object {
        const val BASE_URL = "https://downloadvideo-k2fqvtyckq-uc.a.run.app/"
    }
}