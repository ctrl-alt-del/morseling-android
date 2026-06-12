package com.morseling.audio

import kotlinx.coroutines.flow.Flow

interface MorsePlayer {
    fun play(input: String, wpm: Int): Flow<Int>
    fun release()
    fun isAvailable(): Boolean
}
