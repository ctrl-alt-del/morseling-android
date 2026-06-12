package com.morseling.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.morseling.audio.MorseCodeConverter
import com.morseling.data.ConversionHistoryDao
import com.morseling.data.ConversionRepository
import com.morseling.data.SettingsProvider
import com.morseling.viewmodel.MorseConverterViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MainScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: MorseConverterViewModel

    @Before
    fun setup() {
        hiltRule.inject()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val audioPlayer = MorseCodeConverter(context.applicationContext as android.app.Application)
        val fakeDao = object : ConversionHistoryDao {
            override fun getAll(): Flow<List<com.morseling.data.ConversionHistoryEntity>> =
                flow { emit(emptyList()) }
            override suspend fun insert(entity: com.morseling.data.ConversionHistoryEntity) = 1L
            override suspend fun deleteById(id: Long) {}
            override suspend fun deleteAll() {}
            override suspend fun updateFavorite(id: Long, isFavorite: Boolean) {}
            override suspend fun findByTextInput(textInput: String) = null
            override suspend fun updateTimestamp(id: Long, timestamp: Long) {}
        }
        val settingsProvider = object : SettingsProvider {
            override val settings = flowOf(com.morseling.data.AppSettings())
            override suspend fun setLanguage(language: String) {}
            override suspend fun setDefaultMode(mode: String) {}
            override suspend fun setDefaultWpm(wpm: Int) {}
        }
        viewModel = MorseConverterViewModel(
            audioPlayer, audioPlayer, ConversionRepository(fakeDao), settingsProvider,
        )
        composeTestRule.setContent {
            MainScreen(viewModel = viewModel, onOpenHistory = {}, onOpenSettings = {})
        }
    }

    @Test
    fun convertButtonIsDisplayed() {
        composeTestRule.onNodeWithText("Convert").assertIsDisplayed()
    }

    @Test
    fun playButtonIsDisplayed() {
        composeTestRule.onNodeWithText("Play").assertIsDisplayed()
    }

    @Test
    fun convertTextPopulatesMorseOutput() {
        composeTestRule.onNodeWithText("Type any text").performTextInput("SOS")
        composeTestRule.onNodeWithText("Convert").performClick()
        composeTestRule.onNodeWithText("... --- ...").assertIsDisplayed()
    }
}
