package com.wikapo.widgeti.ui

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.getSelectedDate
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.setSelectedDate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wikapo.widgeti.R
import com.wikapo.widgeti.data.AppDatabase
import com.wikapo.widgeti.data.Lesson
import com.wikapo.widgeti.data.MutableLesson
import com.wikapo.widgeti.ui.theme.WidgETITheme
import com.wikapo.widgeti.util.flatEnd
import com.wikapo.widgeti.util.flatStart
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

private val verticalSpacing = 6.dp
private val horizontalButtonSpacing = 6.dp
private val externalPadding = 12.dp

enum class ConnectShape {
    None,
    Right,
    Both,
    Left,
    Dropdown
}

/**
 * Get string resource for week day
 */
enum class WeekDay(val value: Int, @StringRes val resource: Int) {
    Monday(0, R.string.monday),
    Tuesday(1, R.string.tuesday),
    Wednesday(2, R.string.wednesday),
    Thursday(3, R.string.thursday),
    Friday(4, R.string.friday),
    Saturday(5, R.string.saturday),
    Sunday(6, R.string.sunday)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditLessonScreen(
    db: AppDatabase?,
    lesson: Lesson,
    onCancelClicked: () -> Unit = {}
) {
    val lessonDao = db?.lessonDao()
    var openStartTimePickerDialog by remember { mutableStateOf(false) }
    var openEndTimePickerDialog by remember { mutableStateOf(false) }
    var openStartDatePickerDialog by remember { mutableStateOf(false) }
    var openEndDatePickerDialog by remember { mutableStateOf(false) }
    var openSelectWeekDayDropdown by remember { mutableStateOf(false) }
    var localLesson by remember { mutableStateOf(MutableLesson(lesson)) }

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardHeight = WindowInsets.ime.getBottom(LocalDensity.current)
    LaunchedEffect(key1 = keyboardHeight) {
        coroutineScope.launch {
            scrollState.scrollTo(0)
//            scrollState.scrollBy(keyboardHeight.toFloat())
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Spacer(modifier = Modifier.height(externalPadding))
            TextField(
                value = localLesson.name,
                labelResource = R.string.name,
                required = true
            ) {
                Log.d("TAG", "EditLessonScreen: $it")
                localLesson = localLesson.copy(name = it)
                Log.d("TAG", "EditLessonScreen: ${localLesson.name}")
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = externalPadding)
                    .padding(bottom = verticalSpacing)
                    .height(IntrinsicSize.Min)
            ) {
                TextField(
                    connectShape = ConnectShape.Right,
                    modifier = Modifier.weight(.5f),
                    value = localLesson.type?.toString() ?: "",
                    labelResource = R.string.type,
                    required = true
                ) {
                    if (it.length < 2)
                        localLesson = localLesson.copy(type = it.firstOrNull()?.uppercaseChar())
                }
                TextFieldDivider()
                TextField(
                    connectShape = ConnectShape.Both,
                    modifier = Modifier.weight(.5f),
                    value = localLesson.group?.toString() ?: "",
                    labelResource = R.string.group
                ) {
                    if (it.length < 2) localLesson =
                        localLesson.copy(group = it.firstOrNull()?.uppercaseChar())
                }
                TextFieldDivider()
                TextField(
                    connectShape = ConnectShape.Left,
                    modifier = Modifier.weight(1f),
                    value = localLesson.frequency?.toString() ?: "",
                    labelResource = R.string.frequency,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    required = true
                ) { localLesson = localLesson.copy(frequency = it.toIntOrNull()) }
            }
            TextField(
                value = localLesson.teacher,
                labelResource = R.string.teacher,
                required = true
            ) { localLesson = localLesson.copy(teacher = it) }
            TextField(
                value = localLesson.place,
                labelResource = R.string.place,
                required = true
            ) { localLesson = localLesson.copy(place = it) }
            ExposedDropdownMenuBox(
                modifier = Modifier
                    .padding(horizontal = externalPadding)
                    .padding(bottom = verticalSpacing),
                expanded = openSelectWeekDayDropdown,
                onExpandedChange = { openSelectWeekDayDropdown = !openSelectWeekDayDropdown }
            ) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                    value = stringResource(
                        localLesson.weekDay?.let { WeekDay.entries[it] }?.resource
                            ?: R.string.select_week_day
                    ),
                    connectShape = ConnectShape.Dropdown,
                    labelResource = R.string.week_day,
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = openSelectWeekDayDropdown) },
                    required = true
                )

                DropdownMenu(
                    modifier = Modifier.exposedDropdownSize(matchAnchorWidth = true),
                    shape = MaterialTheme.shapes.large,
                    shadowElevation = 10.dp,
                    expanded = openSelectWeekDayDropdown,
                    onDismissRequest = { openSelectWeekDayDropdown = false }) {
                    WeekDay.entries.forEachIndexed { index, day ->
                        DropdownMenuItem(
                            text = { Text(text = stringResource(day.resource)) },
                            onClick = {
                                localLesson = localLesson.copy(weekDay = index)
                                openSelectWeekDayDropdown = false
                            }
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .padding(horizontal = externalPadding)
                    .fillMaxWidth()
                    .padding(bottom = verticalSpacing)
                    .height(IntrinsicSize.Min)
            ) {
                TextField(
                    value = localLesson.startTime?.toString() ?: "",
                    labelResource = R.string.start_time,
                    readOnly = true,
                    connectShape = ConnectShape.Right,
                    required = true,
                    modifier = Modifier
                        .weight(1f)
                        .pointerInput(localLesson.startTime) {
                            awaitEachGesture {
                                awaitFirstDown(pass = PointerEventPass.Initial)
                                val upEvent =
                                    waitForUpOrCancellation(pass = PointerEventPass.Initial)
                                if (upEvent != null) {
                                    openStartTimePickerDialog = true
                                }
                            }
                        }
                )
                TextFieldDivider()
                TextField(
                    value = localLesson.endTime?.toString() ?: "",
                    labelResource = R.string.end_time,
                    readOnly = true,
                    connectShape = ConnectShape.Left,
                    required = true,
                    modifier = Modifier
                        .weight(1f)
                        .pointerInput(localLesson.endTime) {
                            awaitEachGesture {
                                awaitFirstDown(pass = PointerEventPass.Initial)
                                val upEvent =
                                    waitForUpOrCancellation(pass = PointerEventPass.Initial)
                                if (upEvent != null) {
                                    openEndTimePickerDialog = true
                                }
                            }
                        }
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = externalPadding)
                    .padding(bottom = verticalSpacing)
                    .height(IntrinsicSize.Min)
            ) {
                TextField(
                    value = localLesson.startDate?.toString() ?: "",
                    labelResource = R.string.start_date,
                    readOnly = true,
                    connectShape = ConnectShape.Right,
                    required = localLesson.frequency?.let { it > 1 } ?: false,
                    modifier = Modifier
                        .weight(1f)
                        .pointerInput(localLesson.startDate) {
                            awaitEachGesture {
                                awaitFirstDown(pass = PointerEventPass.Initial)
                                val upEvent =
                                    waitForUpOrCancellation(pass = PointerEventPass.Initial)
                                if (upEvent != null) {
                                    openStartDatePickerDialog = true
                                }
                            }
                        }
                )
                TextFieldDivider()
                TextField(
                    value = localLesson.endDate?.toString() ?: "",
                    labelResource = R.string.end_date,
                    readOnly = true,
                    connectShape = ConnectShape.Left,
                    required = localLesson.extra?.contains("first half semester") ?: false,
                    supportResource = R.string.recommended,
                    errorColorResource = R.color.warning_color,
                    modifier = Modifier
                        .weight(1f)
                        .pointerInput(localLesson.endDate) {
                            awaitEachGesture {
                                awaitFirstDown(pass = PointerEventPass.Initial)
                                val upEvent =
                                    waitForUpOrCancellation(pass = PointerEventPass.Initial)
                                if (upEvent != null) {
                                    openEndDatePickerDialog = true
                                }
                            }
                        }
                )
            }
        }
        Row(
            modifier = Modifier
                .padding(bottom = externalPadding)
                .padding(horizontal = externalPadding)
                .fillMaxWidth()
                .height(40.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = { onCancelClicked() },
                modifier = Modifier.padding(end = horizontalButtonSpacing)
            ) {
                Text(text = stringResource(R.string.cancel))
            }
            if (localLesson != lesson) {
                Button(
                    onClick = { localLesson = MutableLesson(lesson) },
                    modifier = Modifier.padding(end = horizontalButtonSpacing)
                ) {
                    Text(text = stringResource(R.string.reset))
                }
            }
//            Button(onClick = { }) {
//                Text(text = stringResource(R.string.delete))
//            } // TODO move to the top bar. Show three dots menu with delete option
//            Spacer(modifier = Modifier.width(horizontalSpacing))
            Button(
                onClick = {
                    localLesson.toLesson()?.let { lesson ->
                        coroutineScope.launch { lessonDao?.updateLesson(lesson) }
                        onCancelClicked() //TODO confirmation of changes to apply in dialog
                    }
                }
            ) {
                Text(text = stringResource(R.string.save))
            }
        }
    }
    when {
        openStartTimePickerDialog ->
            TimePickerDialog(
                time = localLesson.startTime ?: LocalTime.MIN,
                title = stringResource(R.string.select_start_time),
                onDismiss = { openStartTimePickerDialog = false },
                onConfirm = { time ->
                    localLesson = localLesson.copy(startTime = time)
                    openStartTimePickerDialog = false
                })

        openEndTimePickerDialog ->
            TimePickerDialog(
                time = localLesson.endTime ?: LocalTime.MIN,
                title = stringResource(R.string.select_end_time),
                onDismiss = { openEndTimePickerDialog = false },
                onConfirm = { time ->
                    localLesson = localLesson.copy(endTime = time)
                    openEndTimePickerDialog = false
                })

        openStartDatePickerDialog ->
            DatePickerDialog(
                date = localLesson.startDate,
                title = stringResource(R.string.select_start_date),
                onDateSelected = {
                    localLesson = localLesson.copy(startDate = it)
                    openStartDatePickerDialog = false
                },
                onDismiss = { openStartDatePickerDialog = false })

        openEndDatePickerDialog ->
            DatePickerDialog(
                date = localLesson.endDate,
                title = stringResource(R.string.select_end_date),
                onDateSelected = {
                    localLesson = localLesson.copy(endDate = it)
                    openEndDatePickerDialog = false
                },
                onDismiss = { openEndDatePickerDialog = false })
    }
}

@Composable
private fun TextFieldDivider() {
    VerticalDivider(modifier = Modifier.height(TextFieldDefaults.MinHeight))
}

@Composable
fun TextField(
    value: String?,
    labelResource: Int,
    modifier: Modifier = Modifier,
    connectShape: ConnectShape = ConnectShape.None,
    singleLine: Boolean = true,
    readOnly: Boolean = false,
    required: Boolean = false,
    supportResource: Int = R.string.required,
    errorColorResource: Int? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: @Composable (() -> Unit)? = null,
    onValueChange: (String) -> Unit = {}
) {
    val isEmpty by remember(value) { mutableStateOf(value == null || value == "") }
    val errorDescription: @Composable (() -> Unit)? = if (isEmpty && required) {
        { Text(text = stringResource(supportResource)) }
    } else {
        null
    }
    val errorColor = if (errorColorResource != null) colorResource(errorColorResource) else null
    val shapeLarge = MaterialTheme.shapes.large
    TextField(
        modifier = when (connectShape) {
            ConnectShape.None -> modifier
                .padding(horizontal = externalPadding)
                .padding(bottom = verticalSpacing)
                .fillMaxWidth()

            else -> modifier
        },
        shape = when (connectShape) {
            ConnectShape.None, ConnectShape.Dropdown -> shapeLarge
            ConnectShape.Right -> shapeLarge.flatEnd()
            ConnectShape.Both -> RectangleShape
            ConnectShape.Left -> shapeLarge.flatStart()
        },
        singleLine = singleLine,
        readOnly = readOnly,
        isError = isEmpty && required,
        supportingText = errorDescription,
        colors = if (errorColor != null) {
            TextFieldDefaults.colors().copy(
                errorTextColor = errorColor,
                errorLabelColor = errorColor,
                errorIndicatorColor = errorColor,
                errorSupportingTextColor = errorColor,
                errorCursorColor = errorColor,
                errorTrailingIconColor = errorColor
            )
        } else TextFieldDefaults.colors(),
        value = value ?: "",
        onValueChange = onValueChange,
        keyboardOptions = keyboardOptions,
        trailingIcon = trailingIcon,
        label = { Text(text = stringResource(labelResource)) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    time: LocalTime,
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (LocalTime?) -> Unit
) {
    val timePickerState =
        rememberTimePickerState(time.hour, time.minute)

    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            Row {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.cancel))
                }
                TextButton(onClick = {
                    timePickerState.hour = 0
                    timePickerState.minute = 0
                    onConfirm(null)
                }) {
                    Text(stringResource(R.string.clear))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(LocalTime.of(timePickerState.hour, timePickerState.minute))
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
    onDateSelected: (LocalDate?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(date)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.getSelectedDate())
                onDismiss()
            }) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.cancel))
                }
                TextButton(onClick = {
                    datePickerState.setSelectedDate(null)
                    onDateSelected(null)
                }) {
                    Text(stringResource(R.string.clear))
                }
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
@Preview(showBackground = true, locale = "pl")
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