package com.wikapo.widgeti.ui

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wikapo.widgeti.LocalUserPreferencesRepository
import com.wikapo.widgeti.R
import com.wikapo.widgeti.LocalSettings
import com.wikapo.widgeti.data.AppDatabase
import com.wikapo.widgeti.data.Lesson
import com.wikapo.widgeti.parseGroupName
import com.wikapo.widgeti.parseSchedule
import com.wikapo.widgeti.ui.theme.WidgETITheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun SettingsScreen(
    db: AppDatabase?
) {
    val schedule = remember { mutableStateSetOf<Lesson>() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val lessonDao = db?.lessonDao()
    val settings = LocalSettings.current
    val userPreferencesRepository = LocalUserPreferencesRepository.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    val htmlContent = readTextFromUri(context, uri)

                    schedule.addAll(parseSchedule(htmlContent))
                    userPreferencesRepository?.setScheduleName(parseGroupName(htmlContent))
                    if (schedule.isNotEmpty()) lessonDao?.insertAll(*schedule.toTypedArray())

                } catch (e: Exception) {
                    Log.e("parseSchedule", e.toString())
                }
            }
        }
    }

    /* TODO Obsługa śrątków i innych przeniesionych dni
    * TODO Wybór, czy pokazywać weekend
    * TODO MOŻE PÓŹNIEJ połączenie z botem Kalendarz za pomocą ID kalendarza i ID Serwera
    *  powiadomienia również byłyby fajne
    * */
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(R.string.show_breaks))
            Switch(
                checked = settings.showBreaks,
                onCheckedChange = { isChecked ->
                    coroutineScope.launch {
                        userPreferencesRepository?.updateShowBreaks(isChecked)
                    }
                }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(R.string.show_weekends))
            Switch(
                checked = settings.showWeekends,
                onCheckedChange = { isChecked ->
                    coroutineScope.launch {
                        userPreferencesRepository?.updateShowWeekends(isChecked)
                    }
                }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (settings.scheduleName == null) {
            TextButton(
                onClick = { filePickerLauncher.launch(arrayOf("text/html")) },
                colors = ButtonDefaults.buttonColors()
            ) {
                Text(text = stringResource(R.string.import_schedule_button))
            }
        } else {
            TextButton(
                onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        lessonDao?.deleteAll()
                        userPreferencesRepository?.removeScheduleName()
                    }
                },
                colors = ButtonDefaults.buttonColors()
            ) {
                Text(text = "DELETE EVERYTHING FROM DB")
            }
            Text(text = stringResource(R.string.schedule_for) + "\n" + settings.scheduleName)
        }
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
        SettingsScreen(db = null)
    }
}