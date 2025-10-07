package com.example.todox.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.todox.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    resetHour: Int,
    hasNotificationPermission: Boolean,
    onResetHourChange: (Int) -> Unit,
    onRequestNotificationPermission: () -> Unit,
    onBack: () -> Unit
) {
    var sliderValue by remember(resetHour) { mutableStateOf(resetHour.toFloat()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column {
                Text(text = stringResource(id = R.string.settings_reset_hour))
                Slider(
                    modifier = Modifier.fillMaxWidth(),
                    value = sliderValue,
                    onValueChange = { sliderValue = it },
                    onValueChangeFinished = {
                        onResetHourChange(sliderValue.toInt())
                    },
                    valueRange = 0f..23f,
                    steps = 22
                )
                Text(
                    text = stringResource(
                        id = R.string.settings_reset_hour_value,
                        sliderValue.toInt()
                    )
                )
                Text(
                    text = stringResource(id = R.string.settings_reset_hour_summary)
                )
            }

            if (!hasNotificationPermission) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = stringResource(id = R.string.settings_notification_permission))
                    Text(text = stringResource(id = R.string.settings_notification_permission_description))
                    Button(onClick = onRequestNotificationPermission) {
                        Text(text = stringResource(id = R.string.settings_request_permission))
                    }
                }
            }
        }
    }
}
