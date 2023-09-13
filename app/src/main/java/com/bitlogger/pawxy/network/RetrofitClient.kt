package com.bitlogger.pawxy.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient private constructor() {
    private val myYoutubeAPI: YoutubeAPI

    init {
//        val logging = HttpLoggingInterceptor()
//        logging.level = HttpLoggingInterceptor.Level.BODY
//        val httpClient = OkHttpClient.Builder()
//        httpClient.addInterceptor(logging)

        val retrofit: Retrofit = Retrofit.Builder().baseUrl(YoutubeAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
//            .client(httpClient.build())
            .build()
        myYoutubeAPI = retrofit.create(YoutubeAPI::class.java)
    }

    fun getYoutubeAPI(): YoutubeAPI {
        return myYoutubeAPI
    }

    companion object {
        @get:Synchronized
        var instance: RetrofitClient? = null
            get() {
                if (field == null) {
                    field = RetrofitClient()
                }
                return field
            }
            private set
    }
}