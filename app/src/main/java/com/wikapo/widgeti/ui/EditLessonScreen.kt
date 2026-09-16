package com.wikapo.widgeti.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
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
            .verticalScroll(scrollState)
    ) {
        TextField(
            state = rememberTextFieldState(initialText = lesson.name),
            label = { Text(text = stringResource(R.string.name)) },
        )
        Row {
            TextField(
                modifier = Modifier.width(75.dp),
                state = rememberTextFieldState(initialText = lesson.type.toString()),
                label = { Text(text = stringResource(R.string.type)) },
            )
            TextField(
                modifier = Modifier.width(75.dp),
                state = rememberTextFieldState(initialText = lesson.group.toString()),
                label = { Text(text = stringResource(R.string.group)) })
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
                    .width(150.dp)
            ) {
                Text(text = stringResource(R.string.start_time))
                Text(text = lesson.startTime.toString())
            }
            Column(
                modifier = Modifier
                    .clickable { openEndTimePickerDialog = true }
                    .width(150.dp)
            ) {
                Text(text = stringResource(R.string.end_time))
                Text(text = lesson.endTime.toString())
            }
        }
        Row {
            Column(
                modifier = Modifier
                    .clickable { openStartDatePickerDialog = true }
                    .width(150.dp)
            ) {
                Text(text = stringResource(R.string.start_date))
                Text(text = lesson.startDate.toString())
            }
            Column(
                modifier = Modifier
                    .clickable { openEndDatePickerDialog = true }
                    .width(150.dp)
            ) {
                Text(text = stringResource(R.string.end_date))
                Text(text = lesson.endDate.toString())
            }
        }
        TextField(
            state = rememberTextFieldState(initialText = lesson.frequency.toString()), //TODO Only numbers
            label = { Text(text = stringResource(R.string.frequency)) },
        )
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
                onDismiss = { openStartTimePickerDialog = false },
                onConfirm = { state ->
                    Log.d("TimePicker", "Selected time: ${state.hour}:${state.minute}")
                    openStartTimePickerDialog = false
                })

        openEndTimePickerDialog ->
            TimePickerDialog(
                time = lesson.endTime,
                onDismiss = { openEndTimePickerDialog = false },
                onConfirm = { state ->
                    Log.d("TimePicker", "Selected time: ${state.hour}:${state.minute}")
                    openEndTimePickerDialog = false
                })

        openStartDatePickerDialog ->
            DatePickerDialog(
                date = lesson.startDate,
                onDateSelected = {
                    Log.d("DatePicker", "Selected date: $it")
                    openStartDatePickerDialog = false
                },
                onDismiss = { openStartDatePickerDialog = false })

        openEndDatePickerDialog ->
            DatePickerDialog(
                date = lesson.endDate,
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
    onDismiss: () -> Unit,
    onConfirm: (TimePickerState) -> Unit
) {
    val timePickerState =
        rememberTimePickerState(time.hour, time.minute)

    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(stringResource(R.string.dismiss))
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
            TimePicker(state = timePickerState)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    date: LocalDate?,
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
        DatePicker(state = datePickerState)
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