package com.morseling.audio

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlashlightMorsePlayer @Inject constructor(
    @ApplicationContext private val context: Context
) : MorsePlayer {

    private val cameraManager: CameraManager =
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    private val cameraId: String? = cameraManager.cameraIdList.firstOrNull { id ->
        try {
            cameraManager.getCameraCharacteristics(id)
                .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        } catch (e: CameraAccessException) {
            false
        }
    }

    override fun isAvailable(): Boolean = cameraId != null

    override fun play(input: String, wpm: Int): Flow<Int> = flow {
        val id = cameraId ?: return@flow
        val durations = MorseCodeConverter.durations(wpm)
        val sanitized = MorseCodeConverter.sanitize(input)

        try {
            for ((index, char) in sanitized.withIndex()) {
                emit(index)
                when (char) {
                    '0' -> {
                        cameraManager.setTorchMode(id, true)
                        delay(durations.dot)
                        cameraManager.setTorchMode(id, false)
                    }
                    '1' -> delay(durations.space)
                    '2' -> {
                        cameraManager.setTorchMode(id, true)
                        delay(durations.dash)
                        cameraManager.setTorchMode(id, false)
                    }
                    '3' -> delay(durations.slash)
                }
            }
        } finally {
            cameraManager.setTorchMode(id, false)
        }
    }.flowOn(Dispatchers.Default)

    override fun release() {}
}
