package com.morseling.ui

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.runBlocking
import androidx.compose.ui.unit.dp
import com.morseling.BuildConfig
import com.morseling.R
import com.morseling.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
    onOpenLicenses: () -> Unit,
) {
    val settings by viewModel.settings.collectAsState()
    var showLanguageDialog by remember { mutableStateOf(false) }
    val activity = LocalContext.current as Activity

    val languageOptions = listOf(
        "system" to stringResource(R.string.lang_system_default),
        "en" to "English",
        "zh-rCN" to "简体中文",
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_settings)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.action_back))
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
        ) {
            // ── Playback section ──
            Text(
                text = stringResource(R.string.section_playback).uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp),
            )

            // Language
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_language)) },
                supportingContent = {
                    Text(
                        languageOptions.find { it.first == settings.language }?.second
                            ?: stringResource(R.string.lang_system_default),
                    )
                },
                leadingContent = { Icon(Icons.Default.Language, null) },
                trailingContent = { Icon(Icons.Default.ChevronRight, null) },
                modifier = Modifier.clickable { showLanguageDialog = true },
            )

            // Default mode
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_default_mode)) },
                supportingContent = {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("audio" to stringResource(R.string.mode_audio), "flash" to stringResource(R.string.mode_flash)).forEach { (mode, label) ->
                            FilterChip(
                                selected = settings.defaultMode == mode,
                                onClick = { viewModel.setDefaultMode(mode) },
                                label = { Text(label) },
                            )
                        }
                    }
                },
                leadingContent = { Icon(Icons.Default.Tune, null) },
            )

            // Default speed
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_default_wpm)) },
                supportingContent = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            stringResource(R.string.label_wpm, settings.defaultWpm),
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.width(48.dp),
                        )
                        Slider(
                            value = settings.defaultWpm.toFloat(),
                            onValueChange = { viewModel.setDefaultWpm(it.toInt()) },
                            valueRange = 5f..40f,
                            steps = 6,
                            modifier = Modifier.weight(1f),
                        )
                    }
                },
                leadingContent = { Icon(Icons.Default.Speed, null) },
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            // ── About section ──
            Text(
                text = stringResource(R.string.section_about).uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
            )

            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_version)) },
                supportingContent = { Text(BuildConfig.VERSION_NAME) },
                leadingContent = { Icon(Icons.Default.Info, null) },
            )

            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_licenses)) },
                leadingContent = { Icon(Icons.Default.Description, null) },
                trailingContent = { Icon(Icons.Default.ChevronRight, null) },
                modifier = Modifier.clickable { onOpenLicenses() },
            )
        }
    }

    if (showLanguageDialog) {
        val selectedLanguage = settings.language
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(stringResource(R.string.settings_language)) },
            text = {
                Column {
                    languageOptions.forEach { (code, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    runBlocking { viewModel.setLanguageAndAwait(code) }
                                    activity.recreate()
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = selectedLanguage == code,
                                onClick = {
                                    runBlocking { viewModel.setLanguageAndAwait(code) }
                                    activity.recreate()
                                    showLanguageDialog = false
                                },
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(label, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
            confirmButton = {},
        )
    }
}
