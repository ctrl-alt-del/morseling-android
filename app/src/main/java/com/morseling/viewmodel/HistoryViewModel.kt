package com.morseling.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.morseling.data.ConversionHistoryEntity
import com.morseling.data.ConversionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: ConversionRepository
) : ViewModel() {

    private val _entries = MutableStateFlow<List<ConversionHistoryEntity>>(emptyList())
    val entryList: StateFlow<List<ConversionHistoryEntity>> = _entries.asStateFlow()

    init {
        viewModelScope.launch {
            repository.allEntries.collect { _entries.value = it }
        }
    }

    fun deleteById(id: Long) {
        viewModelScope.launch { repository.deleteById(id) }
    }

    fun deleteAll() {
        viewModelScope.launch { repository.deleteAll() }
    }

    fun toggleFavorite(id: Long, isFavorite: Boolean) {
        viewModelScope.launch { repository.toggleFavorite(id, isFavorite) }
    }
}
