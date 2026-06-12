package com.morseling.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.morseling.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicensesScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_licenses)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.action_back))
                    }
                },
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(licenses) { lib ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(lib.name, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = lib.license,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = lib.url,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }
}

private data class Library(val name: String, val license: String, val url: String)

private val licenses = listOf(
    Library("Kotlin", "Apache 2.0", "https://github.com/JetBrains/kotlin"),
    Library("Jetpack Compose", "Apache 2.0", "https://developer.android.com/jetpack/compose"),
    Library("Material 3", "Apache 2.0", "https://m3.material.io"),
    Library("Hilt / Dagger", "Apache 2.0", "https://dagger.dev/hilt"),
    Library("Room", "Apache 2.0", "https://developer.android.com/training/data-storage/room"),
    Library("AndroidX", "Apache 2.0", "https://developer.android.com/jetpack/androidx"),
    Library("DataStore", "Apache 2.0", "https://developer.android.com/topic/libraries/architecture/datastore"),
    Library("Navigation Compose", "Apache 2.0", "https://developer.android.com/guide/navigation"),
    Library("Kotlin Coroutines", "Apache 2.0", "https://github.com/Kotlin/kotlinx.coroutines"),
    Library("TinyPinyin", "Apache 2.0", "https://github.com/promeg/tinypinyin"),
    Library("R8 / ProGuard", "GPL 2.0", "https://developer.android.com/build/shrink-code"),
)
