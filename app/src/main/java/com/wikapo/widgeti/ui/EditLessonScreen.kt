package com.wikapo.widgeti.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wikapo.widgeti.R
import com.wikapo.widgeti.data.AppDatabase
import com.wikapo.widgeti.data.Lesson
import com.wikapo.widgeti.ui.theme.WidgETITheme
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditLessonScreen(
    db: AppDatabase?,
    lesson: Lesson,
    preview: Boolean = false
) {
    val lessonDao = db?.lessonDao()
    var openStartTimePickerDialog by remember { mutableStateOf(false) }
    var openEndTimePickerDialog by remember { mutableStateOf(false) }
    var openStartDatePickerDialog by remember { mutableStateOf(false) }
    var openEndDatePickerDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardHeight = WindowInsets.ime.getBottom(LocalDensity.current)
    LaunchedEffect(key1 = keyboardHeight) {
        coroutineScope.launch {
            scrollState.scrollBy(keyboardHeight.toFloat())
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(top = 6.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        TextField(
            state = rememberTextFieldState(initialText = lesson.name),
            label = { Text(text = stringResource(R.string.name)) },
        )
        Row {
            TextField(
                modifier = Modifier.width(100.dp),
                state = rememberTextFieldState(initialText = lesson.type.toString()),
                label = { Text(text = stringResource(R.string.type)) },
            )
            TextField(
                modifier = Modifier.width(100.dp),
                state = rememberTextFieldState(initialText = lesson.group.toString()),
                label = { Text(text = stringResource(R.string.group)) })
            TextField(
                modifier = Modifier.width(100.dp),
                state = rememberTextFieldState(initialText = lesson.frequency.toString()), //TODO Only numbers
                label = { Text(text = stringResource(R.string.frequency)) },
            )
        }
        TextField(
            state = rememberTextFieldState(initialText = lesson.teacher),
            label = { Text(text = stringResource(R.string.teacher)) },
        )
        TextField(
            state = rememberTextFieldState(initialText = lesson.place),
            label = { Text(text = stringResource(R.string.place)) },
        )
        TextField(
            state = rememberTextFieldState(initialText = lesson.weekDay.toString()),
            label = { Text(text = stringResource(R.string.week_day)) },
        )
        Row {
            Column(
                modifier = Modifier
                    .clickable { openStartTimePickerDialog = true }
                    .size(150.dp, 60.dp)
                    .padding(horizontal = 16.5.dp, vertical = 5.dp)
            ) {
                Text(text = stringResource(R.string.start_time))
                Text(text = lesson.startTime.toString())
            }
            Column(
                modifier = Modifier
                    .clickable { openEndTimePickerDialog = true }
                    .size(150.dp, 60.dp)
                    .padding(horizontal = 16.5.dp, vertical = 5.dp)
            ) {
                Text(text = stringResource(R.string.end_time))
                Text(text = lesson.endTime.toString())
            }
        }
        Row(
            modifier = Modifier.background(
                LocalTextSelectionColors.current.backgroundColor.copy(
                    alpha = 0.15f
                )
            )
        ) {
            Column(
                modifier = Modifier
                    .clickable { openStartDatePickerDialog = true }
                    .size(150.dp, 60.dp)
                    .padding(horizontal = 16.5.dp, vertical = 5.dp)
            ) {
                Text(text = stringResource(R.string.start_date)) //TODO tekst wyszarzony
                Text(text = lesson.startDate.toString())
            }
            Column(
                modifier = Modifier
                    .clickable { openEndDatePickerDialog = true }
                    .size(150.dp, 60.dp)
                    .padding(horizontal = 16.5.dp, vertical = 5.dp)
            ) {
                Text(text = stringResource(R.string.end_date))
                Text(text = lesson.endDate.toString())
            }
        }
        Row {
            Button(onClick = { /*TODO*/ }) {
                Text(text = stringResource(R.string.cancel))
            }
            Button(onClick = { /*TODO*/ }) {
                Text(text = stringResource(R.string.delete))
            }
            Button(onClick = { /*TODO*/ }) {
                Text(text = stringResource(R.string.save))
            }
        }
    }
    when {
        openStartTimePickerDialog ->
            TimePickerDialog(
                time = lesson.startTime,
                title = stringResource(R.string.select_start_time),
                onDismiss = { openStartTimePickerDialog = false },
                onConfirm = { state ->
                    Log.d("TimePicker", "Selected time: ${state.hour}:${state.minute}")
                    openStartTimePickerDialog = false
                })

        openEndTimePickerDialog ->
            TimePickerDialog(
                time = lesson.endTime,
                title = stringResource(R.string.select_end_time),
                onDismiss = { openEndTimePickerDialog = false },
                onConfirm = { state ->
                    Log.d("TimePicker", "Selected time: ${state.hour}:${state.minute}")
                    openEndTimePickerDialog = false
                })

        openStartDatePickerDialog ->
            DatePickerDialog(
                date = lesson.startDate,
                title = stringResource(R.string.select_start_date),
                onDateSelected = {
                    Log.d("DatePicker", "Selected date: $it")
                    openStartDatePickerDialog = false
                },
                onDismiss = { openStartDatePickerDialog = false })

        openEndDatePickerDialog ->
            DatePickerDialog(
                date = lesson.endDate,
                title = stringResource(R.string.select_end_date),
                onDateSelected = {
                    Log.d("DatePicker", "Selected date: $it")
                    openEndDatePickerDialog = false
                },
                onDismiss = { openEndDatePickerDialog = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    time: LocalTime,
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (TimePickerState) -> Unit
) {
    val timePickerState =
        rememberTimePickerState(time.hour, time.minute)

    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(stringResource(R.string.cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(timePickerState)
            }) {
                Text(stringResource(R.string.ok))
            }
        },
        text = {
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                TimePicker(state = timePickerState)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    date: LocalDate?,
    title: String,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(date)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            title = {
                Text(
                    text = title,
                    color = DatePickerDefaults.colors().titleContentColor,
                    modifier = Modifier.padding(
                        PaddingValues(start = 24.dp, end = 12.dp, top = 16.dp)
                    )
                )
            })
    }
}

@Preview(showBackground = true)
@Composable
fun EditLessonScreenPreview() {
    WidgETITheme {
        EditLessonScreen(
            db = null, lesson = Lesson(
                name = "Preview",
                type = 'P',
                teacher = "Mr. Preview",
                place = "Android Studio",
                weekDay = 2,
                startTime = LocalTime.of(12, 15),
                endTime = LocalTime.of(14, 45),
                group = 'G',
                startDate = LocalDate.of(2026, 9, 15),
                endDate = LocalDate.of(2026, 9, 15),
                frequency = 1,
                extra = null
            )
        )
    }
}