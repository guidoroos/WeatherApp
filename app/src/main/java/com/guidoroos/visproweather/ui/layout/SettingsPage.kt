package com.guidoroos.visproweather.ui.layout

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.guidoroos.visproweather.R
import com.guidoroos.visproweather.util.USE_FAHRENHEIT
import com.guidoroos.visproweather.util.getBooleanFromPreferences
import com.guidoroos.visproweather.util.saveToPreferences
import java.time.LocalDate

@Composable
fun SetupSettingsPageLayout(onNavigate: (Page) -> Unit ) {
    Column(modifier = Modifier.padding(16.dp)) {
        BackButton(onClick = {onNavigate(Page.DAILY)})
        Spacer(modifier = Modifier.height (16.dp))
        SwitchFahrenheit()
    }
}

@Composable
fun SwitchFahrenheit() {
    val context = LocalContext.current
    val checkedState = remember { mutableStateOf(getStoredStatus(context)) }
    Text(text = stringResource(id = R.string.use_fahrenheit),
        color = MaterialTheme.colors.onPrimary,
        style = MaterialTheme.typography.h6
        )
    Switch(
        checked = checkedState.value,
        onCheckedChange = { checkedState.value = it
        saveToPreferences(USE_FAHRENHEIT, it, context )
        }
    )
}

fun getStoredStatus(context: Context): Boolean {
    return getBooleanFromPreferences(USE_FAHRENHEIT, context)
}
