package com.morseling.data

data class AppSettings(
    val language: String = "system",
    val defaultMode: String = "audio",
    val defaultWpm: Int = 20,
)
