package com.guidoroos.visproweather.ui.layout

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.guidoroos.visproweather.R
import com.guidoroos.visproweather.api.ApiClient
import com.guidoroos.visproweather.repository.WeatherRepository
import com.guidoroos.visproweather.ui.theme.VisProWeatherTheme
import com.guidoroos.visproweather.viewmodel.WeatherViewModel
import com.guidoroos.visproweather.viewmodel.WeatherViewModelFactory

enum class Page { DAILY, LOCATION_SELECT, SETTINGS }

class MainActivity : ComponentActivity() {

    private val viewModelFactory =
        WeatherViewModelFactory(WeatherRepository(ApiClient.weatherService))
    private val viewModel: WeatherViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //following this design: https://dribbble.com/shots/18911229-weather-app
        setContent {
            val context = LocalContext.current
            var page by remember { mutableStateOf(Page.DAILY) }

            if (viewModel.error) {
                Toast.makeText(
                    context,
                    stringResource(id = R.string.error_message),
                    Toast.LENGTH_LONG
                ).show()
            }

            //handling navigation manually when backbutton pressed on device, alternative to this is using navigation component with compose
            onBackPressedDispatcher.addCallback(this) {
                if (page != Page.DAILY) {
                    page = Page.DAILY
                } else {
                    this@MainActivity.finish()
                }
            }

            //setup ui for specific page together with navigation bottom bar
            VisProWeatherTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colors.primary
                ) {

                    SetupLayout(page) { selectedPage -> page = selectedPage }
                    Column(
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally
                    )
                    {
                        SetupNavigationBarLayout(currentPage = page) { selectedPage ->
                            page = selectedPage
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                }
            }
        }
    }


    /**
     * Function used for navigation between pages, compose will setup page after "navigation"
     *
     * @param forPage
     * @param onNavigate
     * @receiver
     */
    @Composable
    fun SetupLayout(forPage: Page, onNavigate: (Page) -> Unit) {
        when (forPage) {
            Page.DAILY -> SetupDailyWeatherLayout(onNavigate, viewModel)
            Page.LOCATION_SELECT -> SetupLocationSelectionLayout(onNavigate, viewModel)
            Page.SETTINGS -> SetupSettingsPageLayout(onNavigate)
        }
    }

    @Composable
    fun SetupNavigationBarLayout(currentPage: Page, onIconClicked: (Page) -> Unit) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, colors.onPrimary)
        ) {
            Row {
                IconButton(
                    onClick = { onIconClicked(Page.DAILY) },
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_home),
                        contentDescription = "home",
                        tint = if (currentPage == Page.DAILY) colors.secondary else colors.primaryVariant
                    )
                }
                IconButton(
                    onClick = { onIconClicked(Page.LOCATION_SELECT) },
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "set location",
                        tint = if (currentPage == Page.LOCATION_SELECT) colors.secondary else colors.primaryVariant
                    )
                }
                IconButton(
                    onClick = { onIconClicked(Page.SETTINGS) },
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_settings),
                        contentDescription = "settings",
                        tint = if (currentPage == Page.SETTINGS) colors.secondary else colors.primaryVariant
                    )
                }
            }
        }
    }
}

