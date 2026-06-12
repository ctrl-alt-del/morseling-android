package com.morseling.audio

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MorseCodeConverter @Inject constructor(
    @ApplicationContext private val context: Context
) : MorsePlayer {

    private val toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)

    override fun release() {
        toneGenerator.release()
    }

    override fun play(input: String, wpm: Int): Flow<Int> = flow {
        val durations = durations(wpm)
        val sanitized = sanitize(input)

        for ((index, char) in sanitized.withIndex()) {
            emit(index)
            when (char) {
                '0' -> {
                    toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP)
                    delay(durations.dot)
                }
                '1' -> delay(durations.space)
                '2' -> {
                    toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP)
                    delay(durations.dash)
                }
                '3' -> delay(durations.slash)
            }
        }
    }.flowOn(Dispatchers.Default)

    override fun isAvailable(): Boolean = true

    companion object {
        data class Durations(val dot: Long, val dash: Long, val space: Long, val slash: Long)

        fun durations(wpm: Int): Durations {
            val dot = (1200 / wpm).coerceAtLeast(1).toLong()
            return Durations(dot = dot, dash = dot * 3, space = dot * 3, slash = dot * 7)
        }

        fun sanitize(input: String): String =
            input.replace('.', '0').replace(' ', '1').replace('-', '2').replace('/', '3')
    }
}
