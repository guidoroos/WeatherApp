package com.guidoroos.visproweather.ui.layout



import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.guidoroos.visproweather.R
import com.guidoroos.visproweather.model.Weather
import com.guidoroos.visproweather.model.mockWeather
import com.guidoroos.visproweather.util.LAST_LOCATIONS
import com.guidoroos.visproweather.util.USE_FAHRENHEIT
import com.guidoroos.visproweather.util.getBooleanFromPreferences
import com.guidoroos.visproweather.util.getStringSetFromPreferences
import com.guidoroos.visproweather.viewmodel.WeatherViewModel
import com.skydoves.landscapist.glide.GlideImage
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


private val formatter = DateTimeFormatter.ofPattern("EEEE, d MMM")

/**
 * Method called from main activity to setup layout based on viewmodel state
 *
 * @param onNavigate
 * @param viewModel
 */
@Composable
fun SetupDailyWeatherLayout(onNavigate: (Page) -> Unit, viewModel: WeatherViewModel ) {
    val context = LocalContext.current
    val latestLocation = viewModel.locationSet  ?: getStringSetFromPreferences(LAST_LOCATIONS, context)?.first() ?: "Haarlem"

    viewModel.getWeather(latestLocation)

    Column(modifier = Modifier.padding(16.dp)) {
        Row(modifier = Modifier.align(Alignment.End)) {
            LocationName(name = latestLocation, onNavigate)
        }
        Spacer(modifier = Modifier.height(16.dp))
        DaySelection(date = LocalDate.now(), onNavigate)
        Spacer(modifier = Modifier.height(24.dp))
        CurrentWeather(viewModel.weather)
        Spacer(modifier = Modifier.height(64.dp))
        WeatherPerHour( mockWeather)
        Spacer(modifier = Modifier.height(32.dp))
    }
}

/**
 * Ui element that can in the future be used to fetch weather 7 days in advance
 *
 * @param date
 * @param onNavigate
 * @receiver
 */
@Composable
fun DaySelection(date: LocalDate = LocalDate.now(), onNavigate: (Page) -> Unit) {
    var selectedIndex by remember { mutableStateOf(0) }

    //generate data for current date .. current date + 6 days
    val comingDatesList = mutableListOf<LocalDate>()
    (0L..6L).forEach { i ->
        comingDatesList.add(date.plusDays(i))
    }

    Column {
        Spacer(modifier = Modifier.height(8.dp))
        Row  {
            Text(
                text = stringResource(id = R.string.today),
                color = colors.onPrimary,
                style = typography.h5,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        LazyRow {
            itemsIndexed(items = comingDatesList) { index,date ->
                DateChipButton(date = date,
                    isSelected = index == selectedIndex,
                    onChipClicked = { selectedIndex = index } )
            }
        }
    }
}


/**
 * Date chip button that could be used to show for which day weather is shown
 *
 * @param date
 * @param isSelected
 * @param onChipClicked
 */
@Composable
fun DateChipButton(date: LocalDate, isSelected: Boolean, onChipClicked: () -> Unit) {
    Button(
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (isSelected) colors.secondary else colors.primary
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.padding(horizontal = 4.dp),
        onClick = onChipClicked
    ) {
        Text(
            text = date.format(formatter),
            color = if (isSelected) colors.onSecondary else colors.onPrimary
        )
    }
}

/**
 * Show current weather as fetched for a location from open weather api
 *
 * @param weather
 */
@Composable
fun CurrentWeather(weather: Weather?) {
    if (weather == null) return

    val context =  LocalContext.current
    val temperatureString = getFormattedTemperatureFromWeather(weather, context)
    val iconName = weather.weather[0].icon
    val iconUrl = ("https://openweathermap.org/img/wn/${iconName}@2x.png")

    Column(modifier = Modifier.padding(start = 16.dp)) {
        Row {
            //use Glide to handle fetching weather icon over the network
            GlideImage(imageModel = iconUrl,
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = temperatureString,
            color = colors.onPrimary,
                style = typography.h2.copy(
                    fontWeight = FontWeight.Bold
                )
                ,
            modifier = Modifier.padding(top = 16.dp))
        }
        Text(text = weather.weather[0].description,
            color = colors.onPrimary,
            style = typography.h6,
            modifier = Modifier.padding(top = 16.dp, start = 8.dp))
    }
}

/**
 * Get formatted temperature from weather response
 *
 * @param weather
 * @param context
 * @return
 */
fun getFormattedTemperatureFromWeather(weather: Weather, context: Context):String {
    val shouldUseFahrenheit = getBooleanFromPreferences(USE_FAHRENHEIT, context)
    //response uses Kelvin unit somehow
    val celsius = weather.main.temp - 273

    return if (shouldUseFahrenheit) {
        val fahrenheit = celsius.celsiusToFahrenheit().toInt()
        "${fahrenheit}°"
    } else {
        "${celsius.toInt()}°"
    }
}


/**
 * Celsius to fahrenheit
 *
 * @return
 */
fun Float.celsiusToFahrenheit ():Float {
    return this * (5f/9f) + 35
}

/**
 * Can be used in the future to show weather predictions for coming hours
 *
 * @param weather
 */
@Composable
fun WeatherPerHour(weather:Weather)  {
    //generate data for current time .. current time + 24 hours
    val time = LocalTime.now()

    val comingHoursList = mutableListOf<LocalTime>()
    (0L..12L).forEach { i ->
        comingHoursList.add(time.plusHours(2*i))
    }

    LazyRow {
        items(items = comingHoursList) { hour ->
            WeatherForHour(weather = weather,
            time = hour)
        }
    }
}


/**
 * uses dummy data only now
 *
 * @param weather
 * @param time
 */
@Composable
fun WeatherForHour(weather:Weather, time:LocalTime) {
    val context = LocalContext.current
    val timeString = "${time.hour}:00"
    val temperatureString = getFormattedTemperatureFromWeather(weather, context)

    Column(modifier = Modifier.padding(end = 32.dp)) {
        Text(text = timeString)
        Spacer(modifier = Modifier.height(4.dp))
        Icon(painter = painterResource(id = R.drawable.ic_cloud),
            contentDescription = "weather icon",
            tint = colors.onPrimary,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = temperatureString,
            color = colors.onPrimary,
            style = typography.h4)
    }
}




