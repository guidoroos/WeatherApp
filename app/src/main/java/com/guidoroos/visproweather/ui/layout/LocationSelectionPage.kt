package com.guidoroos.visproweather.ui.layout

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.guidoroos.visproweather.R
import com.guidoroos.visproweather.util.LAST_LOCATIONS
import com.guidoroos.visproweather.util.getStringSetFromPreferences
import com.guidoroos.visproweather.util.saveToPreferences
import com.guidoroos.visproweather.viewmodel.WeatherViewModel


/**
 * Setup UI for setting location from textfield or from saved latest locations
 *
 * @param onNavigate
 * @param viewModel
 * @receiver
 */
@Composable
fun SetupLocationSelectionLayout(onNavigate: (Page) -> Unit, viewModel: WeatherViewModel) {
    val context = LocalContext.current
    var lastLocations by remember { mutableStateOf(listOf<String>()) }
    var currentLocation by remember { mutableStateOf("") }

    //at launch, get state from shared preferences, after response update state values
    if (viewModel.locationExistsResult.exists == true) {
        val location = viewModel.locationExistsResult.location
        currentLocation = location
        lastLocations = updateLastLocations(
            location,
            getStringSetFromPreferences(LAST_LOCATIONS, context)?.toList() ?: listOf()
        )
        saveToPreferences(LAST_LOCATIONS, lastLocations.toSet(), context)
    } else if (viewModel.locationExistsResult.exists == false) {
        Toast.makeText(context, stringResource(id = R.string.location_not_found), Toast.LENGTH_LONG)
            .show()
    } else {
        lastLocations =
            getStringSetFromPreferences(LAST_LOCATIONS, context)?.toList() ?: listOf("Haarlem")
        currentLocation = lastLocations.first()
    }

    //use state to setup UI
    Column(modifier = Modifier.padding(start = 16.dp)) {
        BackButtonLocationSelect(onNavigate, currentLocation)
        Spacer(modifier = Modifier.height(24.dp))
        LocationInputFieldConfirmButton(onClickLocation = { location ->
            viewModel.checkIfLocationExists(
                location
            )
        })
        Spacer(modifier = Modifier.height(48.dp))
        LastFiveLocations(lastLocations,
            onClickLocation = { location -> viewModel.checkIfLocationExists(location) })
    }
}

/**
 * Setup UI to present max of five last locations if these are stored
 *
 * @param locations
 * @param onClickLocation
 * @receiver
 */
@Composable
fun LastFiveLocations(locations: List<String>, onClickLocation: (String) -> Unit) {
    Text(
        text = stringResource(id = R.string.last_locations),
        color = colors.onPrimary,
        style = typography.h5
    )

    LazyColumn(modifier = Modifier.padding(all = 8.dp)) {
        items(items = locations) { location ->
            Location(location = location, onClickLocation = onClickLocation)
        }
    }
}


/**
 * Setup UI for one last location data point
 *
 * @param location
 * @param onClickLocation
 * @receiver
 */
@Composable
fun Location(location: String, onClickLocation: (String) -> Unit) {
    TextButton(onClick = { onClickLocation(location) }) {
        Text(
            text = location,
            color = colors.onPrimary,
            style = typography.h6
        )
    }
}

/**
 * Update last location data after updating location
 *
 * @param location
 * @param lastLocations
 * @return
 */
fun updateLastLocations(location: String, lastLocations: List<String>): List<String> {
    val mutableLocations = lastLocations.toMutableList()

    if (location in lastLocations) {
        mutableLocations.remove(location)
        mutableLocations.add(0, location)
    } else if (lastLocations.size == 5) {
        mutableLocations.removeLast()
        mutableLocations.add(0, location)
    } else {
        mutableLocations.add(0, location)
    }
    return mutableLocations
}

/**
 * Setup UI for input text field for setting new location and a confirmation button
 *
 * @param onClickLocation
 * @receiver
 */
@Composable
fun LocationInputFieldConfirmButton(onClickLocation: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    Text(
        stringResource(id = R.string.enter_location),
        modifier = Modifier.padding(start = 16.dp),
        style = typography.h5.copy(color = colors.onPrimary)
    )
    BasicTextField(value = text,
        onValueChange = { text = it },
        singleLine = true,
        textStyle = typography.h5.copy(color = colors.onPrimary),
        modifier = Modifier.padding(end = 16.dp),
        decorationBox = { innerTextField ->
            Row(
                Modifier
                    .background(colors.primary, RoundedCornerShape(percent = 30))
                    .padding(16.dp)
                    .border(BorderStroke(1.dp, colors.onPrimary))
                    .fillMaxWidth()
            ) {
                Box(Modifier.padding(all = 4.dp)) {
                    innerTextField()
                }
            }
        }
    )
    //setup ui for confirmation button
    Button(
        onClick = { onClickLocation(text) },
        modifier = Modifier.padding(start = 16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.confirm),
            style = typography.h5.copy(color = colors.onPrimary)
        )
    }
}

