package com.guidoroos.visproweather.viewmodel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import com.guidoroos.visproweather.api.ResultOf
import com.guidoroos.visproweather.model.Weather
import com.guidoroos.visproweather.repository.WeatherRepository
import kotlinx.coroutines.launch

/**
 * Location exists result
 *
 * @property location
 * @property exists
 * @constructor Create empty Location exists result
 */
data class LocationExistsResult(val location: String, val exists: Boolean?)

/**
 * Weather view model
 *
 * @property repository
 * @constructor Create empty Weather view model
 */
class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    var locationSet: String? by mutableStateOf(null)
    var weather: Weather? by mutableStateOf(null)
    var locationExistsResult: LocationExistsResult by mutableStateOf(LocationExistsResult("", null))
    var error: Boolean by mutableStateOf(false)

    /**
     * Function using weather api call to check if this location is know by open weather api
     * this prevents invalid input for setting current location
     *
     * @param location
     */
    fun checkIfLocationExists(location: String) = viewModelScope.launch {
        when (val getWeatherResult = repository.getWeather(location)) {
            is ResultOf.Success -> {
                locationExistsResult = LocationExistsResult(location, true)
                locationSet = location
            }
            is ResultOf.Failure -> {
                if (getWeatherResult.message == "city not found") {
                    locationExistsResult = LocationExistsResult(location, false)
                } else {
                    error = true
                }
            }
        }
    }

    /**
     * Get weather from open weather api for set location
     *
     * @param location
     */
    fun getWeather(location: String) = viewModelScope.launch {
        when (val getWeatherResult = repository.getWeather(location)) {
            is ResultOf.Success -> weather = getWeatherResult.value
            is ResultOf.Failure -> error = true
        }
    }
}

/**
 * viewmodel factory used to provide repository to viewmodel constructor
 *
 * @property repository
 * @constructor Create empty Weather view model factory
 */
class WeatherViewModelFactory(
    private val repository: WeatherRepository
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            return WeatherViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

