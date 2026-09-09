package com.wikapo.widgeti.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wikapo.widgeti.R
import com.wikapo.widgeti.LocalSettings
import com.wikapo.widgeti.data.AppDatabase
import com.wikapo.widgeti.data.Lesson
import com.wikapo.widgeti.data.Settings
import com.wikapo.widgeti.getExampleSchedule
import com.wikapo.widgeti.ui.theme.WidgETITheme
import com.wikapo.widgeti.util.nextDay
import com.wikapo.widgeti.util.previousDay
import com.wikapo.widgeti.util.today
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun ScheduleScreen(
    startingDate: LocalDate?,
    db: AppDatabase?,
    previewMode: Boolean = false
) {
    val settings = if (previewMode) Settings(true) else LocalSettings.current
    val schedule = remember { mutableStateListOf<Lesson>() }
    val date = if (startingDate != null) remember { mutableStateOf(startingDate) }
    else remember { mutableStateOf(today(settings.showWeekends)) }
    val update = remember { mutableIntStateOf(0) }
    val lessonDao = db?.lessonDao()

    if (previewMode) schedule.addAll(getExampleSchedule(15))

    LaunchedEffect(date.value, update.intValue) {
        schedule.clear()
        lessonDao?.getByWeekDay(date.value.dayOfWeek.value - 1)?.let { schedule.addAll(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (schedule.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var lastEndTime: LocalTime? = null
                itemsIndexed(schedule) { index, lesson ->
                    if (lastEndTime != null && settings.showBreaks && lastEndTime!! < lesson.startTime) BreakItem(
                        lastEndTime!!, lesson.startTime
                    )
                    lastEndTime = lesson.endTime

                    LessonItem(index, lesson)
                }
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.no_classes),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (settings.scheduleName == null)
                    Text(
                        text = stringResource(R.string.no_classes_desc),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
            }
        }
        ScheduleNavigationBar(date)
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
            text = lesson.kind.toString(),
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
            Text(
                text = lesson.name,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = lesson.teacher,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
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
private fun ScheduleNavigationBar(date: MutableState<LocalDate>) {
    val settings = LocalSettings.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(60.dp)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = { date.value = date.value.previousDay(settings.showWeekends) },
            colors = IconButtonDefaults.filledIconButtonColors(),
            modifier = Modifier
                .width(100.dp)
                .height(50.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.arrow_back),
                contentDescription = stringResource(R.string.previous_button)
            )
        }
        TextButton(
            onClick = { date.value = today(settings.showWeekends) },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.5.dp)
        ) {
            Text(
                text = date.value.format(DateTimeFormatter.ofPattern("EEEE\ndd.MM")),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp
            )
        }
        IconButton(
            onClick = { date.value = date.value.nextDay(settings.showWeekends) },
            colors = IconButtonDefaults.filledIconButtonColors(),
            modifier = Modifier
                .width(100.dp)
                .height(50.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.arrow_forward),
                contentDescription = stringResource(R.string.next_button)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScheduleScreenPreview() {
    WidgETITheme {
        ScheduleScreen(startingDate = null, db = null, previewMode = true)
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
        ScheduleNavigationBar(date = remember { mutableStateOf(LocalDate.now()) })
    }
}