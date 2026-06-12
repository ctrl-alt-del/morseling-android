package com.morseling.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.morseling.R
import com.morseling.viewmodel.MorseConverterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MorseConverterViewModel,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState.error) {
        uiState.error?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(stringResource(R.string.app_name))
                        Text(
                            text = stringResource(R.string.app_subtitle),
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            Icons.Default.Settings,
                            stringResource(R.string.action_settings),
                        )
                    }
                    IconButton(onClick = onOpenHistory) {
                        Icon(
                            Icons.Default.History,
                            stringResource(R.string.action_history),
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // ── Input Card ──
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                ),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = stringResource(R.string.section_text),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    OutlinedTextField(
                        value = uiState.textInput,
                        onValueChange = { viewModel.updateTextInput(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(stringResource(R.string.hint_type_text)) },
                        singleLine = false,
                        minLines = 2,
                        maxLines = 4,
                        textStyle = MaterialTheme.typography.bodyMedium,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        enabled = !uiState.isPlaying,
                        shape = MaterialTheme.shapes.small,
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        val presets = listOf(
                            "S.O.S." to "SOS",
                            "CQ" to "CQ CQ CQ",
                            "Hello" to "Hello World",
                            "73" to "Best regards 73",
                            "Mayday" to "Mayday",
                            "TU" to "Thank you TU",
                            "HW" to "How copy HW",
                            "Happy BD" to "Happy birthday",
                            "Good luck" to "Good luck",
                            "GN" to "Good night GN",
                            "GA" to "Good afternoon GA",
                            "GE" to "Good evening GE",
                            "CUL" to "See you later CUL",
                            "AR" to "AR",
                            "R" to "R",
                            "Test" to "Testing 123",
                        )
                        presets.forEach { (label, text) ->
                            AssistChip(
                                onClick = { viewModel.updateTextInput(text) },
                                label = { Text(label, maxLines = 1) },
                                enabled = !uiState.isPlaying,
                                shape = MaterialTheme.shapes.small,
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.convertText() },
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        enabled = !uiState.isPlaying,
                        shape = MaterialTheme.shapes.small,
                    ) {
                        Icon(Icons.Default.Translate, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.action_convert))
                    }
                }
            }

            // ── Output Card ──
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                ),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().animateContentSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.section_morse),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }

                    AnimatedContent(
                        targetState = uiState.isPlaying,
                        transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
                    ) { playing ->
                        if (playing) {
                            HighlightedMorseText(
                                morse = uiState.morseOutput,
                                highlightIndex = uiState.playingCharIndex,
                                modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 72.dp),
                            )
                        } else {
                            OutlinedTextField(
                                value = uiState.morseOutput,
                                onValueChange = { viewModel.updateMorseOutput(it) },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text(stringResource(R.string.placeholder_morse)) },
                                singleLine = false,
                                minLines = 3,
                                maxLines = 6,
                                textStyle = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Monospace),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                enabled = false,
                                shape = MaterialTheme.shapes.small,
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                ),
                            )
                        }
                    }

                    if (uiState.isPlaying) {
                        Button(
                            onClick = { viewModel.stop() },
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            shape = MaterialTheme.shapes.small,
                        ) {
                            Icon(Icons.Default.Stop, stringResource(R.string.content_desc_stop), Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.action_stop))
                        }
                    } else {
                        Button(
                            onClick = { viewModel.play() },
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            shape = MaterialTheme.shapes.small,
                        ) {
                            Icon(Icons.Default.PlayArrow, stringResource(R.string.content_desc_play), Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.action_play))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun HighlightedMorseText(
    morse: String,
    highlightIndex: Int?,
    modifier: Modifier = Modifier,
) {
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    val onPrimaryContainer = MaterialTheme.colorScheme.onPrimaryContainer
    val outline = MaterialTheme.colorScheme.outline

    val annotatedString = buildAnnotatedString {
        morse.forEachIndexed { index, char ->
            if (index == highlightIndex) {
                withStyle(SpanStyle(color = onPrimaryContainer, background = primaryContainer, fontWeight = FontWeight.Bold, fontSize = 18.sp, fontFamily = FontFamily.Monospace)) {
                    append(if (char == ' ') "·" else char.toString())
                }
            } else {
                withStyle(SpanStyle(fontFamily = FontFamily.Monospace, fontSize = 16.sp, letterSpacing = 2.sp)) {
                    append(char.toString())
                }
            }
        }
    }

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        border = BorderStroke(1.dp, outline.copy(alpha = 0.4f)),
    ) {
        Text(text = annotatedString, modifier = Modifier.padding(12.dp))
    }
}
