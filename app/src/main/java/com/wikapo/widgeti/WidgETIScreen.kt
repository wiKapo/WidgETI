package com.wikapo.widgeti

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.wikapo.widgeti.ui.ScheduleScreen
import com.wikapo.widgeti.ui.SettingsScreen
import com.wikapo.widgeti.ui.theme.WidgETITheme
import java.time.LocalDate

enum class WidgETIScreen(@StringRes val title: Int) {
    Start(title = R.string.app_name),
    Settings(title = R.string.settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgETIAppBar(
    currentScreen: WidgETIScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_back),
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@Composable
fun WidgETIApp(
    startingDate: LocalDate?,
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen =
        WidgETIScreen.valueOf(backStackEntry?.destination?.route ?: WidgETIScreen.Start.name)

    Scaffold(
        topBar = {
            WidgETIAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        },
        floatingActionButton = {
            if (currentScreen.name != WidgETIScreen.Settings.name)
                FloatingActionButton(
                    onClick = { navController.navigate(WidgETIScreen.Settings.name) },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.settings),
                        contentDescription = stringResource(R.string.settings)
                    )
                }
        },
        bottomBar = { WidgETIFooter() }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = WidgETIScreen.Start.name,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(route = WidgETIScreen.Start.name) {
                ScheduleScreen(startingDate = startingDate)
            }
            composable(route = WidgETIScreen.Settings.name) {
                SettingsScreen()
            }
        }
    }
}

@Composable
fun WidgETIFooter() {
    Row(
        modifier = Modifier
            .padding(bottom = 20.dp)
            .background(color = MaterialTheme.colorScheme.outlineVariant)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row {
                Text(
                    text = stringResource(R.string.footer) + " ",
                    fontWeight = FontWeight(400),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = stringResource(R.string.author),
                    fontWeight = FontWeight(800),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun WidgETIFooterPreview() {
    WidgETITheme {
        WidgETIFooter()
    }
}