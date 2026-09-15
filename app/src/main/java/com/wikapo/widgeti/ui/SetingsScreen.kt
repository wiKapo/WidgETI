package com.wikapo.widgeti.ui

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wikapo.widgeti.LocalSettings
import com.wikapo.widgeti.LocalUserPreferencesRepository
import com.wikapo.widgeti.R
import com.wikapo.widgeti.components.showShortToast
import com.wikapo.widgeti.data.AppDatabase
import com.wikapo.widgeti.data.Settings
import com.wikapo.widgeti.parseGroupName
import com.wikapo.widgeti.parseSchedule
import com.wikapo.widgeti.ui.theme.WidgETITheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private val horizontalPadding = 16.dp
private val verticalPadding = 12.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    db: AppDatabase?,
    onEditScheduleClicked: () -> Unit = {},
    preview: Boolean = false
) {
    val context = LocalContext.current
    val resources = LocalResources.current
    val coroutineScope = rememberCoroutineScope()
    val lessonDao = db?.lessonDao()
    val settings = if (preview) Settings(true, "Preview") else LocalSettings.current
    val userPreferencesRepository = LocalUserPreferencesRepository.current
    var availableGroups by remember { mutableStateOf(emptyList<Char>()) }
    var openDeleteScheduleDialog by remember { mutableStateOf(false) }
    var openSelectGroupDialog by remember { mutableStateOf(false) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    val htmlContent = readTextFromUri(context, uri)

                    val schedule = parseSchedule(htmlContent)
                    userPreferencesRepository?.updateScheduleName(parseGroupName(htmlContent))
                    if (schedule.isNotEmpty()) lessonDao?.insertAll(*schedule.toTypedArray())
                } catch (e: Exception) {
                    Log.e("parseSchedule", e.toString())
                }
            }
        }
    }

    LaunchedEffect(settings.scheduleName) {
        coroutineScope.launch {
            availableGroups = lessonDao?.getGroups() ?: emptyList()
        }
    }

    /* TODO Obsługa śrątków i innych przeniesionych dni
    * TODO MOŻE PÓŹNIEJ połączenie z botem Kalendarz za pomocą ID kalendarza i ID Serwera
    *  powiadomienia również byłyby fajne
    * */
    Column {
        if (settings.scheduleName == null) {
            Label(text = stringResource(R.string.manage_schedule))
            LabelWithContent(
                text = stringResource(R.string.import_schedule_html),
                onClick = { filePickerLauncher.launch(arrayOf("text/html")) })
            LabelWithContent(
                text = stringResource(R.string.import_schedule_todo), onClick = { TODO() })
        } else {
            Label(text = stringResource(R.string.schedule_content))
            LabelWithContent(text = stringResource(R.string.show_breaks), onClick = {
                coroutineScope.launch {
                    userPreferencesRepository?.updateShowBreaks(!settings.showBreaks)
                }
            }) {
                Switch(checked = settings.showBreaks, onCheckedChange = null)
            }
            LabelWithContent(text = stringResource(R.string.show_weekends), onClick = {
                coroutineScope.launch {
                    userPreferencesRepository?.updateShowWeekends(!settings.showWeekends)
                }
            }) {
                Switch(checked = settings.showWeekends, onCheckedChange = null)
            }
            Label(text = stringResource(R.string.manage_schedule))
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(horizontal = horizontalPadding),
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                        append(stringResource(R.string.selected_schedule))
                    }
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    ) { append(settings.scheduleName) }
                }
            )
            LabelWithContent(
                text = stringResource(R.string.edit_schedule),
                onClick = { onEditScheduleClicked() })
            LabelWithContent(
                text = stringResource(R.string.select_group),
                description = if (settings.selectedGroup != null)
                    stringResource(R.string.group, settings.selectedGroup[0]) else null,
                onClick = { openSelectGroupDialog = !openSelectGroupDialog })
            LabelWithContent(
                text = stringResource(R.string.set_day_changes),
                onClick = { showShortToast(context, "WIP") })
            LabelWithContent(
                text = stringResource(R.string.remove_schedule),
                onClick = { openDeleteScheduleDialog = !openDeleteScheduleDialog })
        }
        Label(text = stringResource(R.string.manage_calendar))
        LabelWithContent(
            text = stringResource(R.string.add_calendar),
            description = stringResource(R.string.add_calendar_desc),
            onClick = { showShortToast(context, "WIP") }
        )
    }

    when {
        openDeleteScheduleDialog -> AlertDialog(
            onDismissRequest = { openDeleteScheduleDialog = false },
            onConfirmation = {
                coroutineScope.launch(Dispatchers.IO) {
                    lessonDao?.deleteAll()
                    userPreferencesRepository?.clearSchedulePreferences()
                }
                openDeleteScheduleDialog = false
            },
            dialogTitle = stringResource(R.string.remove_schedule),
            dialogText = buildAnnotatedString {
                append(stringResource(R.string.remove_schedule_desc))
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(settings.scheduleName)
                }
                append("?")
            }
        )

        openSelectGroupDialog -> {
            RadioSelectDialog(
                onDismissRequest = { openSelectGroupDialog = false },
                onConfirmation = { group ->
                    Log.d("RadioSelectDialog", "selected group: [$group]")
                    coroutineScope.launch {
                        userPreferencesRepository?.updateSelectedGroup(group)
                    }
                    if (settings.selectedGroup != null)
                        showShortToast(
                            context,
                            resources.getString(R.string.selected_group, settings.selectedGroup[0])
                        )
                    openSelectGroupDialog = false
                },
                onClearSelected = {
                    coroutineScope.launch {
                        userPreferencesRepository?.updateSelectedGroup(null)
                    }
                    openSelectGroupDialog = false
                },
                dialogTitle = stringResource(R.string.select_group),
                defaultValue = settings.selectedGroup?.get(0),
                options = availableGroups,
                optionLabelResource = R.string.group
            )
        }

    }
}


@Composable
private fun Label(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontSize = MaterialTheme.typography.labelLarge.fontSize,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = modifier
            .padding(horizontal = horizontalPadding)
            .padding(top = horizontalPadding, bottom = verticalPadding / 2)
    )
}

@Composable
private fun LabelWithContent(
    text: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Column {
            Text(
                text = text, fontSize = MaterialTheme.typography.titleLarge.fontSize
            )
            if (description != null) Text(
                text = description,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        content()
    }
}

@Composable
fun RadioSelectDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (Char?) -> Unit,
    onClearSelected: () -> Unit,
    dialogTitle: String,
    defaultValue: Char?,
    options: List<Char>,
    optionLabelResource: Int
) {
    var selectedOption by remember { mutableStateOf(defaultValue) }

    AlertDialog(title = { Text(text = dialogTitle) }, text = {
        Column(modifier = Modifier.selectableGroup()) {
            options.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .selectable(
                            selected = (option == selectedOption),
                            onClick = { selectedOption = option },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (option == selectedOption), onClick = null
                    )
                    Text(
                        text = stringResource(optionLabelResource, option),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
    }, onDismissRequest = { onDismissRequest() }, confirmButton = {
        TextButton(onClick = { onConfirmation(selectedOption) }) {
            Text(stringResource(R.string.confirm))
        }
    }, dismissButton = {
        Row {
            TextButton(onClick = { onDismissRequest() }) {
                Text(stringResource(R.string.cancel))
            }
            TextButton(onClick = { onClearSelected() }) {
                Text(stringResource(R.string.clear))
            }
        }
    })
}

@Composable
fun AlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: AnnotatedString,
) {
    AlertDialog(
        icon = {
            Icon(
                painter = painterResource(R.drawable.info),
                contentDescription = stringResource(R.string.info_icon)
            )
        },
        title = { Text(text = dialogTitle) },
        text = { Text(text = dialogText) },
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton(onClick = { onConfirmation() }) {
                Text(stringResource(R.string.yes))
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text(stringResource(R.string.no))
            }
        })
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

@Preview(showBackground = true)
@Composable
fun SettingsScreenWithSchedulePreview() {
    WidgETITheme {
        SettingsScreen(db = null, preview = true)
    }
}