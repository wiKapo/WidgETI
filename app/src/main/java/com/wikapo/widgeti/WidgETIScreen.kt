package com.wikapo.widgeti

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.wikapo.widgeti.data.AppDatabase
import com.wikapo.widgeti.data.Settings
import com.wikapo.widgeti.data.UserPreferencesRepository
import com.wikapo.widgeti.data.dataStore
import com.wikapo.widgeti.ui.ScheduleScreen
import com.wikapo.widgeti.ui.SettingsScreen
import com.wikapo.widgeti.ui.theme.WidgETITheme
import java.time.LocalDate

enum class WidgETIScreen(@StringRes val title: Int) {
    Start(title = R.string.app_name),
    Settings(title = R.string.settings),
    ManageSchedule(title = R.string.manage_schedule),
    ManageCalendar(title = R.string.manage_calendar),
}

val LocalSettings = compositionLocalOf { Settings() }
val LocalUserPreferencesRepository = staticCompositionLocalOf<UserPreferencesRepository?> { null }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgETIAppBar(
    currentScreen: WidgETIScreen,
    canNavigateBack: Boolean,
    navigateToSettings: () -> Unit,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                stringResource(currentScreen.title),
                fontWeight = FontWeight(400),
                color = MaterialTheme.colorScheme.onSurface
            )
        },
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
        },
        actions = {
            if (currentScreen.name == WidgETIScreen.Start.name)
                IconButton(
                    onClick = { navigateToSettings() },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.settings),
                        contentDescription = stringResource(R.string.settings)
                    )
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
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)

    val userPreferencesRepository = remember(context) { UserPreferencesRepository(context.dataStore) }
    val settings by userPreferencesRepository.userSettingsFlow.collectAsState(initial = Settings())

    Scaffold(
        topBar = {
            WidgETIAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                navigateToSettings = { navController.navigate(WidgETIScreen.Settings.name) }
            )
        },
        bottomBar = { WidgETIFooter() }
    ) { paddingValues ->
        CompositionLocalProvider(
            LocalSettings provides settings,
            LocalUserPreferencesRepository provides userPreferencesRepository
        ) {
            NavHost(
                navController = navController,
                startDestination = WidgETIScreen.Start.name,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(route = WidgETIScreen.Start.name) {
                    ScheduleScreen(startingDate = startingDate, db = db)
                }
                composable(route = WidgETIScreen.Settings.name) {
                    SettingsScreen(db = db)
                }
            }
        }
    }
}

@Composable
fun WidgETIFooter() {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.primaryContainer)
            .fillMaxWidth()
            .padding(bottom = 20.dp)
    ) {
        Text(
            text = stringResource(R.string.footer) + " ",
            fontWeight = FontWeight(400),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = stringResource(R.string.author),
            fontWeight = FontWeight(800),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WidgETIAppBarPreview() {
    WidgETITheme {
        WidgETIAppBar(
            currentScreen = WidgETIScreen.Start,
            canNavigateBack = false,
            navigateUp = {},
            navigateToSettings = {})
    }
}

@Preview(showBackground = true)
@Composable
fun WidgETIFooterPreview() {
    WidgETITheme {
        WidgETIFooter()
    }
}