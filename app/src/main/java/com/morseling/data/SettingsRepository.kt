package com.morseling.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsProvider {
    override val settings: Flow<AppSettings> = dataStore.data.map { prefs ->
        AppSettings(
            language = prefs[KEY_LANGUAGE] ?: "system",
            defaultMode = prefs[KEY_DEFAULT_MODE] ?: "audio",
            defaultWpm = prefs[KEY_DEFAULT_WPM] ?: 20,
        )
    }

    override suspend fun setLanguage(language: String) {
        dataStore.edit { it[KEY_LANGUAGE] = language }
    }

    override suspend fun setDefaultMode(mode: String) {
        dataStore.edit { it[KEY_DEFAULT_MODE] = mode }
    }

    override suspend fun setDefaultWpm(wpm: Int) {
        dataStore.edit { it[KEY_DEFAULT_WPM] = wpm }
    }

    companion object {
        private val KEY_LANGUAGE = stringPreferencesKey("language")
        private val KEY_DEFAULT_MODE = stringPreferencesKey("default_mode")
        private val KEY_DEFAULT_WPM = intPreferencesKey("default_wpm")
    }
}
