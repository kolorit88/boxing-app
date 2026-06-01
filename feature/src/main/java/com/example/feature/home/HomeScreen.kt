package com.example.feature.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.Match
import com.example.domain.model.MatchStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ─── Цветовая палитра ────────────────────────────────────────────────────────
private val BlackPure    = Color(0xFF000000)
private val BlackDark    = Color(0xFF0A0A0A)
private val BlackCard    = Color(0xFF111111)
private val BlackSurface = Color(0xFF1A1A1A)
private val BlackElevated= Color(0xFF222222)
private val RedPrimary   = Color(0xFFCC0000)
private val RedDark      = Color(0xFF8B0000)
private val RedBright    = Color(0xFFFF2222)
private val RedDim       = Color(0xFF660000)
private val WhiteText    = Color(0xFFF5F5F5)
private val GrayText     = Color(0xFF999999)
private val GrayDivider  = Color(0xFF2A2A2A)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onMatchClick: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }

    val rotationAngle by animateFloatAsState(
        targetValue = if (isRefreshing) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "refresh_rotation"
    )

    Scaffold(
        containerColor = BlackDark,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "🥊 BOXING",
                            color = RedPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp,
                            letterSpacing = 4.sp
                        )
                        Text(
                            text = "РАСПИСАНИЕ БОЁВ",
                            color = GrayText,
                            fontSize = 10.sp,
                            letterSpacing = 3.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BlackPure
                ),
                actions = {
                    IconButton(onClick = {
                        isRefreshing = true
                        viewModel.refresh()
                        scope.launch {
                            delay(2000)
                            isRefreshing = false
                        }
                    }) {
                        Icon(
                            modifier = Modifier.rotate(rotationAngle),
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Обновить",
                            tint = RedPrimary
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BlackDark)
                .padding(padding)
        ) {
            when (val currentState = state) {
                is HomeState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = RedPrimary, strokeWidth = 3.dp)
                            Spacer(Modifier.height(16.dp))
                            Text("ЗАГРУЗКА БОЁВ...", color = GrayText, letterSpacing = 2.sp, fontSize = 12.sp)
                        }
                    }
                }
                is HomeState.Error -> {
                    AnimatedVisibility(visible = true, enter = slideInVertically { -it } + fadeIn()) {
                        Column(
                            Modifier.fillMaxSize().padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("⚠", fontSize = 48.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(currentState.message, color = RedBright, textAlign = TextAlign.Center)
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.loadMatches() },
                                colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
                            ) { Text("ПОВТОРИТЬ", letterSpacing = 2.sp, fontWeight = FontWeight.Bold) }
                        }
                    }
                }
                is HomeState.Success -> {
                    if (currentState.matches.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("НЕТ ПРЕДСТОЯЩИХ БОЁВ", color = GrayText, letterSpacing = 2.sp)
                        }
                    } else {
                        FightsList(matches = currentState.matches, onMatchClick = onMatchClick)
                    }
                }
            }
        }
    }
}

@Composable
fun FightsList(matches: List<Match>, onMatchClick: (String) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        itemsIndexed(matches) { index, match ->
            AnimatedVisibility(
                visible = true,
                enter = slideInHorizontally(
                    initialOffsetX = { 600 },
                    animationSpec = tween(350, delayMillis = index * 60, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(350, delayMillis = index * 60))
            ) {
                MatchCard(match = match, onClick = { onMatchClick(match.id) })
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MatchCard(match: Match, onClick: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    val isLive = match.status == MatchStatus.LIVE

    // Пульсация для LIVE
    val livePulse by animateFloatAsState(
        targetValue = if (isLive) 1f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "live_pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(BlackCard)
            .border(
                width = if (isLive) 1.dp else 0.5.dp,
                color = if (isLive) RedBright.copy(alpha = 0.5f + livePulse * 0.5f) else GrayDivider,
                shape = RoundedCornerShape(4.dp)
            )
            .clickable { onClick() }
    ) {
        // Левая красная полоса
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight()
                .align(Alignment.CenterStart)
                .background(
                    if (isLive) RedBright
                    else when (match.status) {
                        MatchStatus.FINISHED -> Color(0xFF444444)
                        else -> RedDark
                    }
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(start = 16.dp, end = 12.dp, top = 14.dp, bottom = 14.dp)
        ) {
            // Статус и категория
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = match.league ?: "",
                    color = GrayText,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp
                )
                when (match.status) {
                    MatchStatus.LIVE -> {
                        Box(
                            modifier = Modifier
                                .background(
                                    RedBright.copy(alpha = 0.15f + livePulse * 0.15f),
                                    RoundedCornerShape(2.dp)
                                )
                                .border(0.5.dp, RedBright.copy(alpha = 0.6f), RoundedCornerShape(2.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "● LIVE",
                                color = RedBright,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                    MatchStatus.FINISHED -> {
                        Text(
                            "ЗАВЕРШЁН",
                            color = GrayText,
                            fontSize = 10.sp,
                            letterSpacing = 1.sp
                        )
                    }
                    MatchStatus.UPCOMING -> {}
                }
            }

            Spacer(Modifier.height(10.dp))

            // Боксёры
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = match.homeTeam,
                    color = WhiteText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f)
                )

                // Счёт или VS
                if (match.score != null) {
                    AnimatedContent(
                        targetState = match.score,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "score"
                    ) { score ->
                        Text(
                            text = score ?: "",
                            color = RedPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            letterSpacing = 1.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                } else {
                    Text(
                        "VS",
                        color = RedDim,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        letterSpacing = 2.sp,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }

                Text(
                    text = match.awayTeam,
                    color = WhiteText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }

            Spacer(Modifier.height(8.dp))

            // Дата
            Text(
                text = match.dateTime,
                color = GrayText,
                fontSize = 12.sp
            )

            // Раскрывающаяся деталь
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(tween(250, easing = FastOutSlowInEasing)) + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(Modifier.height(10.dp))
                    Divider(color = GrayDivider, thickness = 0.5.dp)
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Страна", color = GrayText, fontSize = 12.sp)
                        Text(match.country ?: "—", color = WhiteText, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
