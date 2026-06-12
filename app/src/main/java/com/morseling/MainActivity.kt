package com.morseling

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.morseling.data.SettingsProvider
import com.morseling.navigation.MorselingNavGraph
import com.morseling.ui.theme.MorselingTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var settingsProvider: SettingsProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applySavedLocale()
        enableEdgeToEdge()
        setContent {
            MorselingTheme {
                MorselingNavGraph()
            }
        }
    }

    private fun applySavedLocale() {
        val language = runBlocking {
            withContext(Dispatchers.IO) { settingsProvider.settings.first().language }
        }
        val localeList = when (language) {
            "en" -> LocaleListCompat.forLanguageTags("en")
            "zh-rCN" -> LocaleListCompat.forLanguageTags("zh-rCN")
            else -> return
        }
        AppCompatDelegate.setApplicationLocales(localeList)
    }
}
