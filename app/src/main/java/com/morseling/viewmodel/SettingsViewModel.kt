package com.morseling.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.morseling.data.AppSettings
import com.morseling.data.SettingsProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsProvider
) : ViewModel() {

    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    init {
        viewModelScope.launch {
            repository.settings.collect { _settings.value = it }
        }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch { repository.setLanguage(language) }
    }

    suspend fun setLanguageAndAwait(language: String) {
        repository.setLanguage(language)
    }

    fun setDefaultMode(mode: String) {
        viewModelScope.launch { repository.setDefaultMode(mode) }
    }

    fun setDefaultWpm(wpm: Int) {
        viewModelScope.launch { repository.setDefaultWpm(wpm) }
    }
}
