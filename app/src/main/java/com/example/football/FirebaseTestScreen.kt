package com.example.football

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

class FirebaseTestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                FirebaseTestScreen()
            }
        }
    }
}

@Composable
fun FirebaseTestScreen() {
    val scope = rememberCoroutineScope()
    var remoteConfigValues by remember { mutableStateOf<Map<String, Any>>(emptyMap()) }
    var firestoreMatches by remember { mutableStateOf<List<MatchData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                FirebaseRemoteConfigService.init()

                remoteConfigValues = mapOf(
                    "app_version" to FirebaseRemoteConfigService.getAppVersion(),
                    "force_update" to FirebaseRemoteConfigService.isForceUpdate(),
                    "maintenance_mode" to FirebaseRemoteConfigService.isMaintenanceMode(),
                    "feature_search_enabled" to FirebaseRemoteConfigService.isFeatureSearchEnabled(),
                    "default_league" to FirebaseRemoteConfigService.getDefaultLeague()
                )

                val matches = FirestoreService.getAllMatches()
                firestoreMatches = matches

                message = "Firebase успешно инициализирован!"
            } catch (e: Exception) {
                message = "Ошибка: ${e.message}"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Firebase Integration",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Remote Config",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))

                remoteConfigValues.forEach { (key, value) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(key, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            value.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Firestore Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Firestore (${firestoreMatches.size} матчей)",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (firestoreMatches.isEmpty()) {
                    Text(
                        text = "Нет данных. Добавьте матчи",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.height(200.dp)
                    ) {
                        items(firestoreMatches.take(5)) { match ->
                            Text(
                                text = "${match.homeTeam} vs ${match.awayTeam} - ${match.league}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Status
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (message.contains("успешно"))
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Text(
                text = message,
                modifier = Modifier.padding(16.dp),
                color = if (message.contains("успешно"))
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onErrorContainer
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Add sample data button
        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    try {
                        val sampleMatch = MatchData(
                            id = System.currentTimeMillis().toString(),
                            homeTeam = "Пример Команда 1",
                            awayTeam = "Пример Команда 2",
                            dateTime = "01.01.2025 20:00",
                            league = "Пример Лига",
                            country = "Пример Страна",
                            status = "upcoming"
                        )
                        FirestoreService.saveMatch(sampleMatch.id, mapOf(
                            "id" to sampleMatch.id,
                            "homeTeam" to sampleMatch.homeTeam,
                            "awayTeam" to sampleMatch.awayTeam,
                            "dateTime" to sampleMatch.dateTime,
                            "league" to sampleMatch.league,
                            "country" to sampleMatch.country,
                            "status" to sampleMatch.status
                        ))
                        message = "Тестовый матч добавлен в Firestore!"

                        // Обновляем список
                        val matches = FirestoreService.getAllMatches()
                        firestoreMatches = matches
                    } catch (e: Exception) {
                        message = "Ошибка: ${e.message}"
                    }
                    isLoading = false
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Загрузка...")
            } else {
                Text("➕ Добавить тестовый матч в Firestore")
            }
        }
    }
}