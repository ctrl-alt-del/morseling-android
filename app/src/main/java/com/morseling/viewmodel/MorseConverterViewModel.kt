package com.morseling.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.morseling.audio.MorsePlayer
import com.morseling.data.ConversionRepository
import com.morseling.data.SettingsProvider
import com.morseling.model.PlaybackMode
import com.morseling.model.PlaybackUiState
import com.morseling.util.MorseTranslator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.coroutineContext

@HiltViewModel
class MorseConverterViewModel @Inject constructor(
    @Named("audio") private val audioPlayer: MorsePlayer,
    @Named("flash") private val flashPlayer: MorsePlayer,
    private val repository: ConversionRepository,
    settingsProvider: SettingsProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        PlaybackUiState(isFlashAvailable = flashPlayer.isAvailable())
    )
    val uiState: StateFlow<PlaybackUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                settingsProvider.settings.collect { s ->
                    if (!_uiState.value.isPlaying) {
                        _uiState.update {
                            it.copy(
                                playbackMode = if (s.defaultMode == "flash" && flashPlayer.isAvailable())
                                    PlaybackMode.FLASH else PlaybackMode.AUDIO,
                                wpm = s.defaultWpm.coerceIn(5, 40),
                            )
                        }
                    }
                }
            } catch (_: Exception) {
                // Settings unavailable — use defaults
            }
        }
    }

    private var playJob: Job? = null

    private val activePlayer: MorsePlayer
        get() = if (_uiState.value.playbackMode == PlaybackMode.FLASH) flashPlayer else audioPlayer

    fun updateTextInput(input: String) {
        _uiState.update { it.copy(textInput = input, error = null) }
    }

    fun convertText() {
        val text = _uiState.value.textInput.trim()
        if (text.isEmpty()) {
            _uiState.update { it.copy(error = "Enter text to convert", morseOutput = "") }
            return
        }
        if (!MorseTranslator.hasConvertibleChars(text)) {
            _uiState.update { it.copy(error = "No text can be converted to Morse code", morseOutput = "") }
            return
        }

        val morse = MorseTranslator.textToMorse(text)
        val unconvertible = MorseTranslator.unconvertibleChars(text)
        val error = if (unconvertible.isNotEmpty())
            "Some characters could not be converted" else null

        _uiState.update { it.copy(morseOutput = morse, error = error) }

        viewModelScope.launch {
            repository.saveConversion(text, morse)
        }
    }

    fun updateMorseOutput(input: String) {
        _uiState.update { it.copy(morseOutput = input, error = null) }
    }

    fun loadFromHistory(textInput: String, morseOutput: String) {
        _uiState.update { it.copy(textInput = textInput, morseOutput = morseOutput, error = null) }
        viewModelScope.launch {
            repository.saveConversion(textInput, morseOutput)
        }
    }

    fun setPlaybackMode(mode: PlaybackMode) {
        _uiState.update { it.copy(playbackMode = mode, error = null) }
    }

    fun setWpm(wpm: Int) {
        _uiState.update { it.copy(wpm = wpm.coerceIn(5, 40)) }
    }

    fun play() {
        val input = _uiState.value.morseOutput.trim()
        if (input.isEmpty()) {
            _uiState.update { it.copy(error = "Enter Morse code to play") }
            return
        }
        if (input.any { it !in validMorseChars }) {
            _uiState.update { it.copy(error = "Invalid character in Morse code. Use: . - space /") }
            return
        }

        playJob?.cancel()
        val currentJob = viewModelScope.launch {
            try {
                _uiState.update { it.copy(isPlaying = true, playingCharIndex = 0, error = null) }
                val wpm = _uiState.value.wpm
                activePlayer.play(input, wpm).collect { index ->
                    _uiState.update { it.copy(playingCharIndex = index) }
                }
            } catch (e: CancellationException) {
                // User stopped playback — normal
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Playback failed") }
            } finally {
                if (playJob === coroutineContext[Job]) {
                    _uiState.update { it.copy(isPlaying = false, playingCharIndex = null) }
                }
            }
        }
        playJob = currentJob
    }

    fun stop() {
        playJob?.cancel()
        _uiState.update { it.copy(isPlaying = false, playingCharIndex = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    override fun onCleared() {
        super.onCleared()
        playJob?.cancel()
        audioPlayer.release()
        flashPlayer.release()
    }

    companion object {
        private val validMorseChars = setOf('.', '-', ' ', '/')
    }
}
