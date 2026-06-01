package com.example.feature.match

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import com.example.domain.model.MatchStatus

private val BlackPure    = Color(0xFF000000)
private val BlackDark    = Color(0xFF0A0A0A)
private val BlackCard    = Color(0xFF111111)
private val BlackSurface = Color(0xFF1A1A1A)
private val RedPrimary   = Color(0xFFCC0000)
private val RedDark      = Color(0xFF8B0000)
private val RedBright    = Color(0xFFFF2222)
private val WhiteText    = Color(0xFFF5F5F5)
private val GrayText     = Color(0xFF999999)
private val GrayDivider  = Color(0xFF2A2A2A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(
    viewModel: MatchViewModel,
    matchId: String,
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(matchId) { viewModel.loadMatch(matchId) }

    Scaffold(
        containerColor = BlackDark,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "ДЕТАЛИ БОЯ",
                        color = WhiteText,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 3.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text("←", color = RedPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlackPure)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BlackDark)
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when (val s = state) {
                is MatchDetailState.Loading -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = RedPrimary, strokeWidth = 3.dp)
                        Spacer(Modifier.height(12.dp))
                        Text("ЗАГРУЗКА...", color = GrayText, letterSpacing = 2.sp, fontSize = 12.sp)
                    }
                }
                is MatchDetailState.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(s.message, color = RedBright, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadMatch(matchId) },
                            colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                            shape = RoundedCornerShape(4.dp)
                        ) { Text("ПОВТОРИТЬ", fontWeight = FontWeight.Bold, letterSpacing = 2.sp) }
                    }
                }
                is MatchDetailState.Success -> FightDetailContent(match = s.match)
            }
        }
    }
}

@Composable
fun FightDetailContent(match: Match) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(12.dp))

        // Категория / организация
        Text(
            text = match.league ?: "",
            color = RedPrimary,
            fontSize = 12.sp,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(24.dp))

        // Боксёры VS
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(BlackCard)
                .border(0.5.dp, GrayDivider, RoundedCornerShape(4.dp))
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🥊", fontSize = 32.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = match.homeTeam,
                        color = WhiteText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (match.score != null) {
                        Text(
                            text = match.score!!,
                            color = RedPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            letterSpacing = 1.sp
                        )
                    } else {
                        Text(
                            "VS",
                            color = RedPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp,
                            letterSpacing = 3.sp
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🥊", fontSize = 32.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = match.awayTeam,
                        color = WhiteText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Статус бейдж
        when (match.status) {
            MatchStatus.LIVE -> Box(
                modifier = Modifier
                    .background(RedPrimary.copy(alpha = 0.15f), RoundedCornerShape(3.dp))
                    .border(0.5.dp, RedBright, RoundedCornerShape(3.dp))
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                Text("● В ЭФИРЕ", color = RedBright, fontWeight = FontWeight.Bold, letterSpacing = 2.sp, fontSize = 13.sp)
            }
            MatchStatus.FINISHED -> Box(
                modifier = Modifier
                    .background(Color(0xFF1A1A1A), RoundedCornerShape(3.dp))
                    .border(0.5.dp, GrayDivider, RoundedCornerShape(3.dp))
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                Text("ЗАВЕРШЁН", color = GrayText, letterSpacing = 2.sp, fontSize = 12.sp)
            }
            MatchStatus.UPCOMING -> Box(
                modifier = Modifier
                    .background(RedDark.copy(alpha = 0.2f), RoundedCornerShape(3.dp))
                    .border(0.5.dp, RedDark, RoundedCornerShape(3.dp))
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                Text("ПРЕДСТОЯЩИЙ", color = RedPrimary, letterSpacing = 2.sp, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(Modifier.height(24.dp))

        // Информация
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(BlackCard)
                .border(0.5.dp, GrayDivider, RoundedCornerShape(4.dp))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                FightInfoRow("Дата и время", match.dateTime)
                Divider(color = GrayDivider, thickness = 0.5.dp)
                FightInfoRow("Страна", match.country ?: "—")
                Divider(color = GrayDivider, thickness = 0.5.dp)
                FightInfoRow("Статус", when (match.status) {
                    MatchStatus.LIVE -> "Идёт сейчас"
                    MatchStatus.FINISHED -> "Завершён"
                    MatchStatus.UPCOMING -> "Предстоящий"
                })
                if (match.score != null) {
                    Divider(color = GrayDivider, thickness = 0.5.dp)
                    FightInfoRow("Результат", match.score!!)
                }
            }
        }
    }
}

@Composable
fun FightInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = GrayText, fontSize = 13.sp)
        Text(value, color = WhiteText, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

// Псевдоним для совместимости с HomeScreen (MatchCard использует InfoRow)
@Composable
fun InfoRow(label: String, value: String?) {
    FightInfoRow(label, value ?: "—")
}
