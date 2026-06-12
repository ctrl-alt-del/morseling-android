package com.morseling.viewmodel

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.morseling.audio.MorsePlayer
import com.morseling.data.AppSettings
import com.morseling.data.ConversionHistoryDao
import com.morseling.data.ConversionHistoryEntity
import com.morseling.data.ConversionRepository
import com.morseling.data.SettingsProvider
import com.morseling.model.PlaybackMode
import com.morseling.model.PlaybackUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MorseConverterViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeAudioPlayer: FakeMorsePlayer
    private lateinit var fakeFlashPlayer: FakeMorsePlayer
    private lateinit var fakeRepository: ConversionRepository
    private lateinit var fakeDao: FakeDao
    private lateinit var settingsProvider: SettingsProvider
    private lateinit var viewModel: MorseConverterViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeAudioPlayer = FakeMorsePlayer(available = true)
        fakeFlashPlayer = FakeMorsePlayer(available = false)
        fakeDao = FakeDao()
        fakeRepository = ConversionRepository(fakeDao)
        settingsProvider = FakeSettingsProvider()
        viewModel = MorseConverterViewModel(
            fakeAudioPlayer,
            fakeFlashPlayer,
            fakeRepository,
            settingsProvider,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialStateHasDefaultText() {
        val state = viewModel.uiState.value
        assertEquals(PlaybackUiState.DEFAULT_TEXT, state.textInput)
        assertEquals("", state.morseOutput)
        assertEquals(PlaybackMode.AUDIO, state.playbackMode)
        assertFalse(state.isPlaying)
        assertNull(state.playingCharIndex)
        assertEquals(20, state.wpm)
        assertNull(state.error)
    }

    @Test
    fun updateTextInputChangesTextAndClearsError() {
        viewModel.updateTextInput("hello")
        assertEquals("hello", viewModel.uiState.value.textInput)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun convertTextWithEmptyInputShowsError() {
        viewModel.updateTextInput("")
        viewModel.convertText()
        assertNotNull(viewModel.uiState.value.error)
        assertEquals("", viewModel.uiState.value.morseOutput)
    }

    @Test
    fun convertTextWithValidInputProducesMorse() {
        viewModel.updateTextInput("SOS")
        viewModel.convertText()
        assertEquals("... --- ...", viewModel.uiState.value.morseOutput)
    }

    @Test
    fun playWithEmptyMorseShowsError() {
        viewModel.play()
        assertNotNull(viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isPlaying)
    }

    @Test
    fun playWithInvalidMorseCharsShowsError() {
        viewModel.updateMorseOutput("abc")
        viewModel.play()
        assertNotNull(viewModel.uiState.value.error)
    }

    @Test
    fun playWithValidMorseDelegatesToPlayer() = runTest {
        viewModel.updateMorseOutput("...")
        viewModel.play()
        advanceUntilIdle()
        assertEquals(1, fakeAudioPlayer.playCalls.size)
    }

    @Test
    fun stopThenPlayAgainDelegatesCorrectly() = runTest {
        viewModel.updateMorseOutput("...")
        viewModel.play()
        advanceUntilIdle()
        assertEquals(1, fakeAudioPlayer.playCalls.size)
        viewModel.stop()
        advanceUntilIdle()
        viewModel.updateMorseOutput("---")
        viewModel.play()
        advanceUntilIdle()
        assertEquals(2, fakeAudioPlayer.playCalls.size)
    }

    @Test
    fun setWpmClampsToRange() {
        viewModel.setWpm(3)
        assertEquals(5, viewModel.uiState.value.wpm)
        viewModel.setWpm(50)
        assertEquals(40, viewModel.uiState.value.wpm)
        viewModel.setWpm(20)
        assertEquals(20, viewModel.uiState.value.wpm)
    }

    @Test
    fun loadFromHistorySetsBothFieldsAndSaves() = runTest {
        viewModel.loadFromHistory("hello", ".... . .-.. .-.. ---")
        advanceUntilIdle()
        assertEquals("hello", viewModel.uiState.value.textInput)
        assertEquals(".... . .-.. .-.. ---", viewModel.uiState.value.morseOutput)
    }

    @Test
    fun clearErrorSetsErrorToNull() {
        viewModel.updateTextInput("")
        viewModel.convertText()
        assertNotNull(viewModel.uiState.value.error)
        viewModel.clearError()
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun setPlaybackModeToAudioWorks() {
        viewModel.setPlaybackMode(PlaybackMode.AUDIO)
        assertEquals(PlaybackMode.AUDIO, viewModel.uiState.value.playbackMode)
    }

    @Test
    fun setPlaybackModeToFlashWorksWhenAvailable() = runTest {
        viewModel = MorseConverterViewModel(
            fakeAudioPlayer,
            FakeMorsePlayer(available = true),
            fakeRepository,
            settingsProvider,
        )
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isFlashAvailable)
        viewModel.setPlaybackMode(PlaybackMode.FLASH)
        assertEquals(PlaybackMode.FLASH, viewModel.uiState.value.playbackMode)
    }

    @Test
    fun playDelegatesToCorrectPlayer() = runTest {
        viewModel = MorseConverterViewModel(
            fakeAudioPlayer,
            FakeMorsePlayer(available = true),
            fakeRepository,
            settingsProvider,
        )
        advanceUntilIdle()
        viewModel.updateMorseOutput(".")
        viewModel.setPlaybackMode(PlaybackMode.FLASH)
        viewModel.play()
        advanceUntilIdle()
        assertEquals(0, fakeAudioPlayer.playCalls.size)
    }

    private class FakeMorsePlayer(
        private val available: Boolean = true,
    ) : MorsePlayer {
        var playCalls = mutableListOf<String>()
        var released = false

        override fun isAvailable(): Boolean = available

        override fun play(input: String, wpm: Int): Flow<Int> = flow {
            playCalls.add(input)
            input.indices.forEach { emit(it) }
        }

        override fun release() {
            released = true
        }
    }

    private class FakeDao : ConversionHistoryDao {
        private val data = mutableListOf<ConversionHistoryEntity>()
        private var nextId = 1L

        override fun getAll(): Flow<List<ConversionHistoryEntity>> = flow { emit(data.toList()) }
        override suspend fun insert(entity: ConversionHistoryEntity): Long {
            val e = entity.copy(id = nextId++)
            data.add(e)
            return e.id
        }
        override suspend fun deleteById(id: Long) { data.removeAll { it.id == id } }
        override suspend fun deleteAll() { data.clear() }
        override suspend fun updateFavorite(id: Long, isFavorite: Boolean) {
            val i = data.indexOfFirst { it.id == id }
            if (i >= 0) data[i] = data[i].copy(isFavorite = isFavorite)
        }
        override suspend fun findByTextInput(textInput: String): ConversionHistoryEntity? =
            data.firstOrNull { it.textInput == textInput }
        override suspend fun updateTimestamp(id: Long, timestamp: Long) {
            val i = data.indexOfFirst { it.id == id }
            if (i >= 0) data[i] = data[i].copy(timestamp = timestamp)
        }
    }

    private class FakeSettingsProvider : SettingsProvider {
        override val settings: Flow<com.morseling.data.AppSettings> = flowOf(com.morseling.data.AppSettings())
        override suspend fun setLanguage(language: String) {}
        override suspend fun setDefaultMode(mode: String) {}
        override suspend fun setDefaultWpm(wpm: Int) {}
    }
}
