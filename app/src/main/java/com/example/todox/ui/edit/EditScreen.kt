package com.example.todox.ui.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.todox.R
import com.example.todox.data.model.Priority
import com.example.todox.data.model.Todo
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import androidx.compose.material3.DatePicker
import androidx.compose.material3.rememberTimePickerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    todo: Todo?,
    onSave: (Todo) -> Unit,
    onDelete: (() -> Unit)?,
    onBack: () -> Unit
) {
    val zone = remember { ZoneId.systemDefault() }
    val existingDateTime = remember(todo?.dueAt) {
        todo?.dueAt?.let { Instant.ofEpochMilli(it).atZone(zone) }
    }
    var title by rememberSaveable(todo?.id) { mutableStateOf(todo?.title.orEmpty()) }
    var note by rememberSaveable(todo?.id) { mutableStateOf(todo?.note.orEmpty()) }
    var daily by rememberSaveable(todo?.id) { mutableStateOf(todo?.daily ?: false) }
    var dueAt by rememberSaveable(todo?.id) { mutableStateOf(todo?.dueAt) }
    var priority by rememberSaveable(todo?.id) { mutableStateOf(todo?.priority ?: Priority.MID) }
    var tags by rememberSaveable(todo?.id) {
        mutableStateOf(todo?.tags?.joinToString(", ") ?: "")
    }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showTimePicker by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    val dateTimeFormatter = remember { DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT) }

    val dueText = dueAt?.let {
        Instant.ofEpochMilli(it).atZone(zone).let(dateTimeFormatter::format)
    }

    val isSaveEnabled = title.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = if (todo == null) stringResource(id = R.string.fab_add) else todo.title)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    if (todo != null && onDelete != null) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(id = R.string.edit_delete)
                            )
                        }
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = title,
                onValueChange = { title = it },
                label = { Text(text = stringResource(id = R.string.edit_title_hint)) },
                singleLine = true
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = note,
                onValueChange = { note = it },
                label = { Text(text = stringResource(id = R.string.edit_note_hint)) },
                singleLine = false
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.edit_daily))
                Switch(
                    checked = daily,
                    onCheckedChange = { daily = it }
                )
            }

            Column {
                Text(text = stringResource(id = R.string.edit_priority))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Priority.entries.forEach { item ->
                        val labelRes = when (item) {
                            Priority.LOW -> R.string.priority_low
                            Priority.MID -> R.string.priority_mid
                            Priority.HIGH -> R.string.priority_high
                        }
                        FilterChip(
                            selected = priority == item,
                            onClick = { priority = item },
                            label = { Text(text = stringResource(id = labelRes)) },
                            colors = FilterChipDefaults.filterChipColors()
                        )
                    }
                }
            }

            Column {
                Text(text = stringResource(id = R.string.edit_tags))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = tags,
                    onValueChange = { tags = it },
                    singleLine = false
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = stringResource(id = R.string.edit_due_date))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { showDatePicker = true }) {
                        Text(text = dueText ?: stringResource(id = R.string.edit_due_date))
                    }
                    Button(onClick = { showTimePicker = true }) {
                        Text(text = stringResource(id = R.string.edit_due_time))
                    }
                    if (dueAt != null) {
                        TextButton(onClick = { dueAt = null }) {
                            Text(text = stringResource(id = R.string.edit_clear_due))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f, fill = true))

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = isSaveEnabled,
                onClick = {
                    val base = todo ?: Todo(title = title)
                    val sanitizedTags = tags.split(",")
                        .mapNotNull { it.trim().takeIf(String::isNotEmpty) }
                    val updated = base.copy(
                        title = title.trim(),
                        note = note.trim().takeIf { it.isNotEmpty() },
                        daily = daily,
                        dueAt = dueAt,
                        priority = priority,
                        tags = sanitizedTags
                    )
                    onSave(updated)
                }
            ) {
                Text(text = stringResource(id = R.string.edit_save), textAlign = TextAlign.Center)
            }
        }
    }

    if (showDatePicker) {
        val selectedDateMillis = dueAt ?: existingDateTime?.toInstant()?.toEpochMilli()
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDateMillis
        )
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        val existingTime = dueAt?.let {
                            Instant.ofEpochMilli(it).atZone(zone).toLocalTime()
                        } ?: LocalTime.of(9, 0)
                        val newDate = Instant.ofEpochMilli(millis).atZone(zone).toLocalDate()
                        val target = newDate.atTime(existingTime)
                        dueAt = target.atZone(zone).toInstant().toEpochMilli()
                    }
                    showDatePicker = false
                }) {
                    Text(text = stringResource(android.R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(text = stringResource(android.R.string.cancel))
                }
            },
            text = {
                DatePicker(state = datePickerState)
            }
        )
    }

    if (showTimePicker) {
        val existingTime = dueAt?.let {
            Instant.ofEpochMilli(it).atZone(zone).toLocalTime()
        } ?: LocalTime.of(9, 0)
        val existingDate = dueAt?.let {
            Instant.ofEpochMilli(it).atZone(zone).toLocalDate()
        } ?: LocalDate.now()
        val timePickerState = rememberTimePickerState(
            initialHour = existingTime.hour,
            initialMinute = existingTime.minute,
            is24Hour = true
        )

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val target = existingDate.atTime(timePickerState.hour, timePickerState.minute)
                    dueAt = target.atZone(zone).toInstant().toEpochMilli()
                    showTimePicker = false
                }) {
                    Text(text = stringResource(android.R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(text = stringResource(android.R.string.cancel))
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }

    if (showDeleteDialog && onDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) {
                    Text(text = stringResource(android.R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(text = stringResource(android.R.string.cancel))
                }
            },
            text = { Text(text = stringResource(id = R.string.edit_delete)) }
        )
    }
}
