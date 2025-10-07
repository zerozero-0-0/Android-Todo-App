package com.example.todox.data.repo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(
    private val dataStore: DataStore<Preferences>
) {

    private val resetHourKey = intPreferencesKey(KEY_RESET_HOUR)

    val resetHour: Flow<Int> = dataStore.data.map { preferences ->
        preferences[resetHourKey]?.coerceIn(0, 23) ?: DEFAULT_RESET_HOUR
    }

    suspend fun setResetHour(hour: Int) {
        val normalized = hour.coerceIn(0, 23)
        dataStore.edit { preferences ->
            preferences[resetHourKey] = normalized
        }
    }

    companion object {
        const val KEY_RESET_HOUR = "resetHour"
        const val DEFAULT_RESET_HOUR = 0
    }
}
