package com.wikapo.widgeti.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wikapo.widgeti.R
import com.wikapo.widgeti.components.NavigationBar
import com.wikapo.widgeti.data.AppDatabase
import com.wikapo.widgeti.data.Lesson
import com.wikapo.widgeti.getExampleSchedule

private const val SHOW_EXTRA = true //TODO Change to false after testing

@Composable
fun EditScheduleScreen(
    db: AppDatabase?,
    onLessonClicked: (Lesson) -> Unit = {},
    preview: Boolean = false
) {
    val lessonDao = db?.lessonDao()
    val schedule = remember { mutableStateListOf<Lesson>() }
    var weekDay by remember { mutableIntStateOf(0) }

    if (preview) schedule.addAll(getExampleSchedule(15))

    LaunchedEffect(weekDay) {
        schedule.clear()
        lessonDao?.getByWeekDay(weekDay)?.let { lessons -> schedule.addAll(lessons) }
    }

    Column {
        if (schedule.isNotEmpty()) LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(schedule) { index, lesson ->
                EditableLessonItem(index, lesson) { onLessonClicked(lesson) }
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
        ManageScheduleNavigationBar(
            weekDay,
            onBackClicked = { if (weekDay > 1) weekDay-- else weekDay = 6 },
            onMiddleClicked = { weekDay = 0 },
            onNextClicked = { if (weekDay < 6) weekDay++ else weekDay = 0 })
    }
}

@Composable
fun ManageScheduleNavigationBar(
    weekDay: Int,
    onBackClicked: () -> Unit = {},
    onMiddleClicked: () -> Unit = {},
    onNextClicked: () -> Unit = {}
) {
    val weekDeyText = when (weekDay) {
        0 -> stringResource(R.string.monday)
        1 -> stringResource(R.string.tuesday)
        2 -> stringResource(R.string.wednesday)
        3 -> stringResource(R.string.thursday)
        4 -> stringResource(R.string.friday)
        5 -> stringResource(R.string.saturday)
        6 -> stringResource(R.string.sunday)
        else -> stringResource(R.string.error)
    }
    NavigationBar(
        onBackClicked = { onBackClicked() },
        onMiddleClicked = { onMiddleClicked() },
        onNextClicked = { onNextClicked() },
        middleText = weekDeyText
    )
}

@Composable
private fun EditableLessonItem(index: Int, lesson: Lesson, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(top = 6.dp)
            .background(
                color = if (index % 2 == 1) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.tertiaryContainer,
                shape = MaterialTheme.shapes.large
            )
            .clickable { onClick() }, verticalAlignment = Alignment.CenterVertically
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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = lesson.teacher,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                if (lesson.group != null) Text(
                    text = stringResource(R.string.group_x, lesson.group),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (lesson.endDate != lesson.startDate || lesson.startDate == null)
                    Text(
                        text = pluralStringResource(
                            R.plurals.every_x_weeks,
                            lesson.frequency,
                            lesson.frequency
                        )
                    )
                CreateDateText(lesson)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${lesson.startTime} - ${lesson.endTime}",
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = lesson.place,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            if (lesson.extra != null && SHOW_EXTRA) Text(
                text = lesson.extra,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CreateDateText(lesson: Lesson) {
    when {
        lesson.startDate != null && lesson.startDate == lesson.endDate -> {
            Text(text = stringResource(R.string.on_the_day, lesson.startDate))
        }

        lesson.startDate != null && lesson.endDate != null -> {
            Text(
                text = stringResource(
                    R.string.from_x_to_y,
                    lesson.startDate,
                    lesson.endDate
                )
            )
        }

        lesson.startDate != null -> {
            Text(text = stringResource(R.string.starts_on, lesson.startDate))
        }

        lesson.isBiweeklyAndNotSetUp() -> {
            Text(
                text = stringResource(R.string.set_start_date),
                color = MaterialTheme.colorScheme.error
            )
        }

        lesson.endsFirstHalfSemesterAndNotSetUp() -> {
            Text(
                text = stringResource(R.string.set_end_date),
                color = colorResource(R.color.warning_color)
            )
        }

        lesson.endDate != null -> {
            Text(text = stringResource(R.string.ends_on, lesson.endDate))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditScheduleScreenPreview() {
    EditScheduleScreen(db = null, preview = true)
}