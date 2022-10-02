package com.guidoroos.visproweather.ui.layout


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.guidoroos.visproweather.R

/**
 * Top bar element used in multiple pages
 *
 * @param onNavigate
 * @param location
 * @receiver
 */
@Composable
fun BackButtonLocationSelect(onNavigate: (Page) -> Unit, location: String) {
    Row(modifier = Modifier.padding(all = 8.dp)) {
        Spacer(modifier = Modifier.height(8.dp))
        BackButton(onClick = { onNavigate(Page.DAILY) })
        Spacer(Modifier.weight(1f))
        LocationName(name = location, onNavigate = { onNavigate(Page.LOCATION_SELECT) })
    }
}

@Composable
fun BackButton(onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
    ) {
        Icon(
            painterResource(id = R.drawable.ic_arrow_back),
            contentDescription = "forward icon",
            tint = MaterialTheme.colors.onPrimary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(id = R.string.back),
            color = MaterialTheme.colors.onPrimary,
            style = MaterialTheme.typography.h6,
        )
    }
}

@Composable
fun LocationName(name: String, onNavigate: (Page) -> Unit) {
    Button(
        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colors.primaryVariant),
        onClick = { onNavigate(Page.LOCATION_SELECT) }

    ) {
        Row(modifier = Modifier.padding(end = 4.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_location),
                contentDescription = "location",
                tint = MaterialTheme.colors.onPrimary
            )
            Text(
                text = name,
                color = MaterialTheme.colors.onPrimary,
            )
        }
    }
}