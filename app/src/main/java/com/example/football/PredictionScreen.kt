package com.example.football

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.Match
import com.example.domain.repository.MatchRepository
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

private val BlackPure    = Color(0xFF000000)
private val BlackDark    = Color(0xFF0A0A0A)
private val BlackCard    = Color(0xFF111111)
private val BlackSurface = Color(0xFF1A1A1A)
private val RedPrimary   = Color(0xFFCC0000)
private val RedDark      = Color(0xFF8B0000)
private val RedBright    = Color(0xFFFF2222)
private val RedDim       = Color(0xFF660000)
private val WhiteText    = Color(0xFFF5F5F5)
private val GrayText     = Color(0xFF999999)
private val GrayDivider  = Color(0xFF2A2A2A)

class PredictionActivity : ComponentActivity() {
    private val repository: MatchRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(primary = RedPrimary, background = BlackDark)) {
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
    var predictionText by remember { mutableStateOf("Выберите бой и нажмите АНАЛИЗИРОВАТЬ") }
    var isAnalyzing by remember { mutableStateOf(false) }
    var selectedMatch by remember { mutableStateOf<Match?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var hasPrediction by remember { mutableStateOf(false) }

    val pulseAlpha by animateFloatAsState(
        targetValue = if (isAnalyzing) 0.4f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    LaunchedEffect(Unit) {
        scope.launch {
            repository.getUpcomingMatches(30).onSuccess {
                matches = it
                if (matches.isNotEmpty()) selectedMatch = matches.first()
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
            .background(BlackDark)
            .padding(16.dp)
    ) {
        // Заголовок
        Spacer(Modifier.height(8.dp))
        Text("🤖 AI ПРОГНОЗ", color = RedPrimary, fontWeight = FontWeight.Black, fontSize = 22.sp, letterSpacing = 4.sp)
        Text("АНАЛИЗ ОТ GEMINI", color = GrayText, fontSize = 10.sp, letterSpacing = 3.sp)
        Spacer(Modifier.height(20.dp))

        // Выбор боя
        if (!isLoading && matches.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(BlackCard)
                    .border(0.5.dp, GrayDivider, RoundedCornerShape(4.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text("ВЫБЕРИТЕ БОЙ", color = RedPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Spacer(Modifier.height(12.dp))
                    LazyColumn(modifier = Modifier.height(160.dp)) {
                        items(matches) { match ->
                            val isSelected = selectedMatch?.id == match.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(if (isSelected) RedDim.copy(alpha = 0.25f) else Color.Transparent)
                                    .clickable { selectedMatch = match; errorMessage = null }
                                    .padding(horizontal = 8.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "${match.homeTeam} vs ${match.awayTeam}",
                                    color = if (isSelected) WhiteText else GrayText,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    modifier = Modifier.weight(1f)
                                )
                                if (isSelected) Text("◀", color = RedPrimary, fontSize = 12.sp)
                            }
                            if (match != matches.last()) Divider(color = GrayDivider.copy(alpha = 0.4f), thickness = 0.5.dp)
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        // Карточка прогноза
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(4.dp))
                .background(if (hasPrediction) RedDim.copy(alpha = 0.1f) else BlackCard)
                .border(
                    0.5.dp,
                    if (hasPrediction) RedPrimary.copy(alpha = 0.5f) else GrayDivider,
                    RoundedCornerShape(4.dp)
                )
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = RedPrimary, strokeWidth = 3.dp)
                    Spacer(Modifier.height(12.dp))
                    Text("ЗАГРУЗКА ДАННЫХ...", color = GrayText, letterSpacing = 2.sp, fontSize = 11.sp)
                }
                errorMessage != null -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("⚠", fontSize = 36.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(errorMessage ?: "", color = RedBright, textAlign = TextAlign.Center, fontSize = 13.sp)
                }
                else -> AnimatedContent(
                    targetState = predictionText,
                    transitionSpec = { fadeIn(tween(400)) + slideInVertically { 30 } togetherWith fadeOut(tween(200)) + slideOutVertically { -30 } },
                    label = "prediction"
                ) { text ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (hasPrediction) {
                            Text(
                                "🤖 GEMINI AI",
                                color = RedPrimary,
                                fontSize = 11.sp,
                                letterSpacing = 2.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(12.dp))
                            Divider(color = GrayDivider, thickness = 0.5.dp)
                            Spacer(Modifier.height(12.dp))
                        }
                        Text(
                            text = text,
                            color = if (hasPrediction) WhiteText else GrayText,
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            textAlign = if (hasPrediction) TextAlign.Start else TextAlign.Center
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Выбранный бой
        selectedMatch?.let { match ->
            if (!isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(BlackCard)
                        .border(0.5.dp, GrayDivider, RoundedCornerShape(4.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${match.homeTeam}  VS  ${match.awayTeam}", color = WhiteText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(match.league ?: "", color = GrayText, fontSize = 11.sp)
                        }
                        Text(match.dateTime, color = GrayText, fontSize = 11.sp, textAlign = TextAlign.End)
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }

        // Кнопка
        Button(
            onClick = {
                scope.launch {
                    isAnalyzing = true
                    val match = selectedMatch
                    if (match != null) {
                        com.example.football.ai.GeminiPredictionService.getMatchPrediction(
                            homeTeam = match.homeTeam,
                            awayTeam = match.awayTeam,
                            league = match.league ?: "Бокс"
                        ).onSuccess { prediction ->
                            predictionText = prediction
                            hasPrediction = true
                        }.onFailure { error ->
                            predictionText = "Ошибка AI: ${error.message}\n\nПроверьте подключение и API ключ."
                            hasPrediction = false
                        }
                    } else {
                        predictionText = "Выберите бой для анализа"
                    }
                    isAnalyzing = false
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && !isAnalyzing && selectedMatch != null,
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = RedPrimary,
                disabledContainerColor = RedDim.copy(alpha = 0.4f)
            )
        ) {
            if (isAnalyzing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = WhiteText,
                    strokeWidth = 2.dp
                )
                Spacer(Modifier.width(10.dp))
                Text("АНАЛИЗИРУЮ...", fontWeight = FontWeight.Black, letterSpacing = 3.sp)
            } else {
                Text("🤖  АНАЛИЗИРОВАТЬ", fontWeight = FontWeight.Black, letterSpacing = 3.sp, fontSize = 14.sp)
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(
            "AI анализирует статистику боксёров и предсказывает исход боя",
            color = GrayText,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
