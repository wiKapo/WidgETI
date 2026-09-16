package com.wikapo.widgeti.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wikapo.widgeti.LocalSettings
import com.wikapo.widgeti.R
import com.wikapo.widgeti.components.NavigationBar
import com.wikapo.widgeti.data.AppDatabase
import com.wikapo.widgeti.data.Lesson
import com.wikapo.widgeti.data.Settings
import com.wikapo.widgeti.getExampleSchedule
import com.wikapo.widgeti.ui.theme.WidgETITheme
import com.wikapo.widgeti.util.nextDay
import com.wikapo.widgeti.util.previousDay
import com.wikapo.widgeti.util.today
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    startingDate: LocalDate?,
    db: AppDatabase?,
    onAddScheduleClicked: () -> Unit = {},
    onEditLessonClicked: (Lesson) -> Unit = {},
    preview: Boolean = false
) {
    val settings = if (preview) Settings(true) else LocalSettings.current
    val schedule = remember { mutableStateListOf<Lesson>() }
    var date by remember { mutableStateOf(startingDate ?: today(settings.showWeekends)) }
    var update by remember { mutableIntStateOf(0) }
    val lessonDao = db?.lessonDao()

    if (preview) schedule.addAll(getExampleSchedule(15))

    LaunchedEffect(date, update) {
        schedule.clear()
        lessonDao?.getByDay(date.dayOfWeek.value - 1, date)?.let { lessons ->
            schedule.addAll(lessons.filter {
                it.group == settings.selectedGroup?.get(0) || it.group == null || settings.selectedGroup == null
            })
        }
    }

    if (settings.scheduleName != null || preview) {
        Column {
            if (schedule.isNotEmpty()) LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var lastEndTime: LocalTime? = null
                itemsIndexed(schedule) { index, lesson ->
                    if (lastEndTime != null && settings.showBreaks && lastEndTime!! < lesson.startTime)
                        BreakItem(lastEndTime!!, lesson.startTime)
                    lastEndTime = lesson.endTime
                    if (lesson.isNotSetUp())
                        LessonEditTooltip(
                            lesson = lesson,
                            action = { onEditLessonClicked(lesson) }
                        ) {
                            LessonItem(index, lesson)
                        }
                    else LessonItem(index, lesson)
                }
                item { Spacer(modifier = Modifier.height(6.dp)) }
            }
            else Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.no_classes),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            ScheduleNavigationBar(date = date, onDateChange = { date = it })
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(R.string.schedule_will_be_here))
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = { onAddScheduleClicked() }) {
                Text(text = stringResource(R.string.add_schedule_redirect))
            }
        }
    }
}

@Composable
private fun LessonItem(index: Int, lesson: Lesson) {
    Row(
        modifier = Modifier
            .padding(top = 6.dp)
            .background(
                color = if (index % 2 == 1) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.tertiaryContainer,
                shape = MaterialTheme.shapes.large
            ), verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = lesson.type.toString(),
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .padding(vertical = 5.dp)
                .padding(start = 10.dp)
                .width(25.dp)
        )
        Column(modifier = Modifier.padding(10.dp, 5.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = lesson.name,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                when {
                    lesson.isBiweeklyAndNotSetUp() -> {
                        Icon(
                            painter = painterResource(R.drawable.error_colored),
                            tint = Color.Unspecified,
                            contentDescription = stringResource(R.string.biweekly_lessons_waring),
                            modifier = Modifier.requiredWidth(24.dp)
                        )
                    }

                    lesson.endsFirstHalfSemesterAndNotSetUp() -> {
                        Icon(
                            painter = painterResource(R.drawable.warning_colored),
                            tint = Color.Unspecified,
                            contentDescription = stringResource(R.string.biweekly_lessons_waring),
                            modifier = Modifier.requiredWidth(24.dp)
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = lesson.teacher, color = MaterialTheme.colorScheme.onSurface, maxLines = 1
                )
                if (lesson.group != null) Text(
                    text = stringResource(R.string.group_x, lesson.group),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${lesson.startTime} - ${lesson.endTime}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = lesson.place,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

/**
 *   @param startTime break start time
 *   @param endTime break end time
 */
@Composable
private fun BreakItem(startTime: LocalTime, endTime: LocalTime) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp)
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.large
            ), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = startTime.toString(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(10.dp, 5.dp)
        )
        Text(
            text = stringResource(R.string.break_time),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(10.dp, 5.dp)
        )
        Text(
            text = endTime.toString(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(10.dp, 5.dp)
        )
    }
}

@Composable
private fun ScheduleNavigationBar(
    date: LocalDate,
    onDateChange: (LocalDate) -> Unit = {}
) {
    val settings = LocalSettings.current

    NavigationBar(
        onBackClicked = { onDateChange(date.previousDay(settings.showWeekends)) },
        onMiddleClicked = { onDateChange(today(settings.showWeekends)) },
        onNextClicked = { onDateChange(date.nextDay(settings.showWeekends)) },
        middleText = date.format(DateTimeFormatter.ofPattern("EEEE\ndd.MM"))
    )
}

@Composable
fun LessonEditTooltip(lesson: Lesson, action: () -> Unit, content: @Composable () -> Unit) {
    when {
        lesson.isBiweeklyAndNotSetUp() -> {
            Tooltip(
                title = stringResource(R.string.set_start_date),
                text = stringResource(R.string.biweekly_lessons_waring),
                actionText = stringResource(R.string.edit_lesson),
                action = { action() },
            ) {
                content()
            }
        }

        lesson.endsFirstHalfSemesterAndNotSetUp() -> {
            Tooltip(
                title = stringResource(R.string.set_end_date),
                text = stringResource(R.string.lesson_end_first_half_semester),
                actionText = stringResource(R.string.edit_lesson),
                action = { action() },
            ) {
                content()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Tooltip(
    title: String,
    text: String,
    actionText: String = "",
    action: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val tooltipState = rememberTooltipState()
    val scope = rememberCoroutineScope()
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
            TooltipAnchorPosition.Above
        ),
        tooltip = {
            RichTooltip(
                title = { Text(text = title) },
                action = {
                    Row {
                        TextButton(onClick = { scope.launch { tooltipState.dismiss() } }) {
                            Text(text = stringResource(R.string.dismiss))
                        }
                        if (action != {})
                            TextButton(onClick = { action() }) {
                                Text(text = actionText)
                            }
                    }
                }
            ) { Text(text = text) }
        },
        state = tooltipState
    ) {
        Box(modifier = Modifier.clickable { scope.launch { tooltipState.show() } }) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScheduleScreenPreview() {
    WidgETITheme {
        ScheduleScreen(startingDate = null, db = null, preview = true)
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyScheduleScreenPreview() {
    WidgETITheme {
        ScheduleScreen(startingDate = null, db = null)
    }
}

@Preview(showBackground = true)
@Composable
fun SchduleNavigationPreview() {
    WidgETITheme {
        ScheduleNavigationBar(date = LocalDate.now())
    }
}