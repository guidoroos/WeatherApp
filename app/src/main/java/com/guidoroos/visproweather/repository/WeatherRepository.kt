package com.guidoroos.visproweather.repository

import com.guidoroos.visproweather.api.ResultOf
import com.guidoroos.visproweather.api.WeatherService
import com.guidoroos.visproweather.model.Weather

class WeatherRepository(private val service: WeatherService) {
    suspend fun getWeather(location: String): ResultOf<Weather?> {
        val response = service.getWeather(location)
        return if (response.isSuccessful) {
            ResultOf.Success(response.body())
        } else {
            ResultOf.Failure(response.message())
        }
    }
}

