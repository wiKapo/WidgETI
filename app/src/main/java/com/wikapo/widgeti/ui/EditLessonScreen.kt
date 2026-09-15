package com.wikapo.widgeti.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.wikapo.widgeti.data.AppDatabase
import com.wikapo.widgeti.data.Lesson
import com.wikapo.widgeti.ui.theme.WidgETITheme
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun EditLessonScreen(
    db: AppDatabase?,
    lesson: Lesson,
    preview: Boolean = false
) {
    val lessonDao = db?.lessonDao()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = lesson.toString())
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