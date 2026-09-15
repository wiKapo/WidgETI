package com.wikapo.widgeti.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wikapo.widgeti.R
import com.wikapo.widgeti.ui.theme.WidgETITheme

@Composable
fun NavigationBar(
    onBackClicked: () -> Unit = {},
    onMiddleClicked: () -> Unit = {},
    onNextClicked: () -> Unit = {},
    middleText: String
) {
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
            onClick = { onBackClicked() },
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
            onClick = { onMiddleClicked() },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.5.dp)
        ) {
            Text(
                text = middleText,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp
            )
        }
        IconButton(
            onClick = { onNextClicked() },
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
fun NavigationBarPreview() {
    WidgETITheme {
        NavigationBar(middleText = "Preview")
    }
}