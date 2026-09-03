package com.wikapo.widgeti.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.wikapo.widgeti.ui.theme.WidgETITheme


@Composable
fun SettingsScreen(
) {
    Column {
        Text(text = "LOL")
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    WidgETITheme {
        SettingsScreen()
    }
}