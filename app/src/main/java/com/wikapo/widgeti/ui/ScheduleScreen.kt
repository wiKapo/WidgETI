package com.wikapo.widgeti.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import com.wikapo.widgeti.data.AppDatabase
import com.wikapo.widgeti.data.Lesson
import com.wikapo.widgeti.getExampleSchedule
import com.wikapo.widgeti.ui.theme.WidgETITheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ScheduleScreen(
    startingDate: LocalDate?,
    db: AppDatabase?,
    previewMode: Boolean = false
) {
    val schedule = remember { mutableStateListOf<Lesson>() }
    val date = if (startingDate != null)
        remember { mutableStateOf(startingDate) }
    else
        remember { mutableStateOf(LocalDate.now()) }
    val update = remember { mutableIntStateOf(0) }
    val lessonDao = db?.lessonDao()

    if (previewMode) schedule.addAll(getExampleSchedule(15))

    LaunchedEffect(date.value, update.intValue) {
        schedule.clear()
        lessonDao?.getAll()?.let { schedule.addAll(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (schedule.isNotEmpty())
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(schedule.size) { index ->
                    val lesson = schedule[index]
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (index % 2 == 1) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.tertiaryContainer,
                                    shape = MaterialTheme.shapes.large
                                )
                        ) {
                            Column(modifier = Modifier.padding(10.dp, 5.dp)) {
                                Text(
                                    text = "[${lesson.kind}]\t\t${lesson.name}",
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
                                        text = "${"START"}:00 - ${"END"}:00",
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
                }
                item {
                    Spacer(modifier = Modifier.height(5.dp))
                }
            }
        else
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
                Text(
                    text = stringResource(R.string.no_classes_desc),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        ScheduleNavigationBar(date)
    }
}

@Composable
private fun ScheduleNavigationBar(date: MutableState<LocalDate>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(60.dp)
            .background(MaterialTheme.colorScheme.primaryContainer),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = { date.value = date.value.minusDays(1) },
            colors = IconButtonDefaults.filledIconButtonColors(),
            modifier = Modifier
                .width(100.dp)
                .requiredHeight(50.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.arrow_back),
                contentDescription = stringResource(R.string.previous_button)
            )
        }
        TextButton(
            onClick = { date.value = LocalDate.now() },
            modifier = Modifier.width(110.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = date.value.format(DateTimeFormatter.ofPattern("EEEE\ndd.MM")),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 15.sp
                )
            }
        }
        IconButton(
            onClick = { date.value = date.value.plusDays(1) },
            colors = IconButtonDefaults.filledIconButtonColors(),
            modifier = Modifier
                .width(100.dp)
                .requiredHeight(50.dp)
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
        ScheduleScreen(LocalDate.now(), db = null, previewMode = true)
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyScheduleScreenPreview() {
    WidgETITheme {
        ScheduleScreen(LocalDate.now(), db = null)
    }
}

@Preview(showBackground = true)
@Composable
fun SchduleNavigationPreview() {
    WidgETITheme {
        ScheduleNavigationBar(date = remember { mutableStateOf(LocalDate.now()) })
    }
}