package com.morseling.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

interface SettingsProvider {
    val settings: Flow<AppSettings>
    suspend fun setLanguage(language: String)
    suspend fun setDefaultMode(mode: String)
    suspend fun setDefaultWpm(wpm: Int)
}
