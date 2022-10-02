package com.guidoroos.visproweather.api

import com.guidoroos.visproweather.model.Weather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("weather")
    //todo implement better way for providing appid: via a backend service for example
    suspend fun getWeather(
        @Query("q") location: String,
        @Query("appid") appid: String = "7587eaff3affbf8e56a81da4d6c51d06"
    ): Response<Weather>

}