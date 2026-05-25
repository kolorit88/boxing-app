package com.example.football

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.domain.model.Match
import com.example.domain.repository.MatchRepository
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class PredictionActivity : ComponentActivity() {

    private val repository: MatchRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                PredictionScreen(repository = repository)
            }
        }
    }
}

@Composable
fun PredictionScreen(repository: MatchRepository) {
    val scope = rememberCoroutineScope()
    var matches by remember { mutableStateOf<List<Match>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var predictionText by remember { mutableStateOf("Нажмите 'Анализировать' для получения AI прогноза") }
    var isAnalyzing by remember { mutableStateOf(false) }
    var selectedMatch by remember { mutableStateOf<Match?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Анимация пульсации для кнопки
    val pulseAlpha by animateFloatAsState(
        targetValue = if (isAnalyzing) 0.5f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_animation"
    )

    // Загрузка данных
    LaunchedEffect(Unit) {
        scope.launch {
            isLoading = true
            val result = repository.getUpcomingMatches(30)
            result.onSuccess {
                matches = it
                if (matches.isNotEmpty()) {
                    selectedMatch = matches.first()
                }
                isLoading = false
            }.onFailure { error ->
                errorMessage = "Ошибка загрузки: ${error.message}"
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Заголовок
        Text(
            text = "AI Прогноз матча",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Анализ от Google Gemini AI",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Выбор матча для прогноза
        if (!isLoading && matches.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Выберите матч для анализа",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.height(150.dp)
                    ) {
                        items(matches) { match ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        selectedMatch = match
                                        errorMessage = null
                                    },
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${match.homeTeam} vs ${match.awayTeam}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (selectedMatch?.id == match.id)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = match.league ?: "Неизвестная лига",  
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Divider()
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Карточка с прогнозом
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (predictionText.contains("Gemini") || predictionText.contains("прогноз"))
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Загрузка данных...")
                        }
                    }
                } else if (errorMessage != null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = errorMessage ?: "Ошибка",
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = {
                                scope.launch {
                                    isLoading = true
                                    val result = repository.getUpcomingMatches(30)
                                    result.onSuccess {
                                        matches = it
                                        if (matches.isNotEmpty()) {
                                            selectedMatch = matches.first()
                                        }
                                        errorMessage = null
                                    }.onFailure { error ->
                                        errorMessage = "Ошибка загрузки: ${error.message}"
                                    }
                                    isLoading = false
                                }
                            }) {
                                Text("Повторить")
                            }
                        }
                    }
                } else {
                    // Анимация появления текста прогноза
                    AnimatedContent(
                        targetState = predictionText,
                        transitionSpec = {
                            fadeIn() + slideInVertically() togetherWith
                                    fadeOut() + slideOutVertically()
                        },
                        label = "prediction_animation"
                    ) { text ->
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (text.contains("Gemini") || text.contains("прогноз")) {
                                Text(
                                    text = "Анализ Gemini AI",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Информация о выбранном матче
        if (selectedMatch != null && !isLoading) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = selectedMatch!!.homeTeam,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                        Text("VS", style = MaterialTheme.typography.bodySmall)
                        Text(
                            text = selectedMatch!!.awayTeam,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = selectedMatch!!.league ?: "Неизвестная лига",  
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = selectedMatch!!.dateTime,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Кнопка анализа
        Button(
            onClick = {
                scope.launch {
                    isAnalyzing = true

                    val match = selectedMatch
                    if (match != null) {
                        val result = com.example.football.ai.GeminiPredictionService.getMatchPrediction(
                            homeTeam = match.homeTeam,
                            awayTeam = match.awayTeam,
                            league = match.league ?: "Неизвестная лига" 
                        )

                        result.onSuccess { prediction ->
                            predictionText = prediction
                        }.onFailure { error ->
                            predictionText = "Ошибка AI: ${error.message}\n\nПроверьте подключение к интернету и API ключ."
                        }
                    } else {
                        predictionText = "Пожалуйста, выберите матч для анализа"
                    }

                    isAnalyzing = false
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && !isAnalyzing && selectedMatch != null
        ) {
            if (isAnalyzing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Женя анализирует данные...")
            } else {
                Text("Получить прогноз")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Информация
        Text(
            text = "Женя анализирует статистику и предсказывает результат матча",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
