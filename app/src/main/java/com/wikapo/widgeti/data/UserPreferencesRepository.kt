package com.wikapo.widgeti.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

object PreferencesKeys {
    val SHOW_BREAKS = booleanPreferencesKey("show_breaks")
    val SHOW_WEEKENDS = booleanPreferencesKey("show_weekends")
    val SCHEDULE_NAME = stringPreferencesKey("schedule_name")
}

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    val userSettingsFlow: Flow<Settings> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            Settings(
                showBreaks = preferences[PreferencesKeys.SHOW_BREAKS] ?: false,
                showWeekends = preferences[PreferencesKeys.SHOW_WEEKENDS] ?: false,
                scheduleName = preferences[PreferencesKeys.SCHEDULE_NAME]
            )
        }

    suspend fun updateShowBreaks(showBreaks: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_BREAKS] = showBreaks
        }
    }

    suspend fun updateShowWeekends(showWeekends: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_WEEKENDS] = showWeekends
        }
    }

    suspend fun setScheduleName(scheduleName: String?) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SCHEDULE_NAME] = scheduleName ?: ""
        }
    }

    suspend fun removeScheduleName() {
        dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.SCHEDULE_NAME)
        }
    }
}
