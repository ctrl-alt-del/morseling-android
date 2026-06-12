package com.morseling.model

data class PlaybackUiState(
    val textInput: String = DEFAULT_TEXT,
    val morseOutput: String = "",
    val playbackMode: PlaybackMode = PlaybackMode.AUDIO,
    val isFlashAvailable: Boolean = false,
    val isPlaying: Boolean = false,
    val playingCharIndex: Int? = null,
    val wpm: Int = 20,
    val error: String? = null
) {
    companion object {
        const val DEFAULT_TEXT = "Hello World"
        const val DEFAULT_MORSE = ".... . .-.. .-.. --- / .-- --- .-. .-.. -.."
    }
}
