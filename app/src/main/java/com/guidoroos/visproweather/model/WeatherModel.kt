package com.guidoroos.visproweather.model

data class Weather(
    val name: String,
    val weather: List<WeatherDescription>,
    val main: WeatherMain,
    val dt: Long
)

data class WeatherMain (val temp: Float)

data class WeatherDescription(val description: String, var icon : String)

//this is used to cope with design which mentions data that is not provided by api
val mockWeather = Weather (
    name = "name",
    weather = listOf(WeatherDescription("Cloudy",""), WeatherDescription("Chilly","")),
    main = WeatherMain(288f),
    dt = 0
    )
