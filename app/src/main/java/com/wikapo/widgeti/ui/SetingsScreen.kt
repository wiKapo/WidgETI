package com.wikapo.widgeti.ui

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wikapo.widgeti.R
import com.wikapo.widgeti.data.Lesson
import com.wikapo.widgeti.ui.theme.WidgETITheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.time.LocalTime


@Composable
fun SettingsScreen(
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var parsedResult by remember { mutableStateOf("") }
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    val htmlContent = readTextFromUri(context, uri)

                    if (htmlContent != null) {
                        val document = Jsoup.parse(htmlContent)

                        val rows = document.body().getElementsByTag("tr")

                        val schedule: MutableList<Lesson> = mutableListOf()
                        rows.forEach { row ->
                            Log.i("TEST", "Read a row")
                            val cells = row.getElementsByTag("td")
                            cells.next()

                            var time: LocalTime = LocalTime.MIN
                            cells.forEachIndexed { index, cell ->
                                if (index == 0) {
                                    time = LocalTime.parse(cell.text())
                                    return@forEachIndexed
                                }
                                if (cell.text() != "") { // TODO Obsługa kilku wydarzeń w danym polu
                                    val place = cell.getElementsByClass("room_name").text()
                                    val name = cell.getElementsByClass("subject_name").text()
                                    val teacher = cell.ownText()
                                    val boldedText = cell.getElementsByTag("b").eachText() // TODO Odczyt opcjonalnych grup, dat rozpoczęcia i zakończenia
                                    val kind: Char = boldedText[1][1]
                                    schedule.add(Lesson(
                                        name = name,
                                        kind = kind,
                                        teacher = teacher,
                                        place = place,
                                        weekDay = index - 1,
                                        startTime = time,
                                        endTime = time.plusHours(1),
                                    ))
                                }
                            }
                            Log.d("READ SCHEDULE", schedule.toString())
                        }

                        parsedResult = schedule.toString()
                    }
                } catch (e: Exception) {
                    parsedResult = "ERROR"
                }
            }
        }
    }

    /* TODO Obsługa śrątków i innych przeniesionych dni
    * TODO Wybór, czy pokazywać weekend
    * TODO MOŻE PÓŹNIEJ połączenie z botem Kalendarz za pomocą ID kalendarza i ID Serwera
    *  powiadomienia również byłyby fajne
    * */
    Column {
        TextButton(
            onClick = { filePickerLauncher.launch(arrayOf("text/html")) },
            colors = ButtonDefaults.buttonColors()
        ) {
            Text(text = stringResource(R.string.import_schedule_button))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = parsedResult)
    }
}

private fun readTextFromUri(context: Context, uri: Uri): String? {
    return context.contentResolver.openInputStream(uri)?.use { inputStream ->
        inputStream.bufferedReader().use { reader -> reader.readText() }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    WidgETITheme {
        SettingsScreen()
    }
}