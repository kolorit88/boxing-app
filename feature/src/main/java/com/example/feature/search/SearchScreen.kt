package com.example.feature.search

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feature.home.MatchCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val BlackPure    = Color(0xFF000000)
private val BlackDark    = Color(0xFF0A0A0A)
private val BlackCard    = Color(0xFF111111)
private val BlackSurface = Color(0xFF1A1A1A)
private val RedPrimary   = Color(0xFFCC0000)
private val RedBright    = Color(0xFFFF2222)
private val RedDim       = Color(0xFF660000)
private val WhiteText    = Color(0xFFF5F5F5)
private val GrayText     = Color(0xFF999999)
private val GrayDivider  = Color(0xFF2A2A2A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onMatchClick: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    var isSearching by remember { mutableStateOf(false) }

    val searchRotation by animateFloatAsState(
        targetValue = if (isSearching) 360f else 0f,
        animationSpec = tween(500, easing = LinearEasing),
        label = "search_rotation"
    )

    Scaffold(
        containerColor = BlackDark,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "🔍 ПОИСК",
                            color = WhiteText,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            letterSpacing = 4.sp
                        )
                        Text(
                            text = "ПОИСК БОЁВ",
                            color = GrayText,
                            fontSize = 10.sp,
                            letterSpacing = 3.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlackPure),
                actions = {
                    IconButton(onClick = {
                        isSearching = true
                        viewModel.performSearch()
                        scope.launch {
                            delay(1000)
                            isSearching = false
                        }
                    }) {
                        Icon(
                            modifier = Modifier.rotate(searchRotation),
                            imageVector = Icons.Default.Search,
                            contentDescription = "Поиск",
                            tint = RedPrimary
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BlackDark)
                .padding(padding)
        ) {
            // Фильтры
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(BlackCard)
                    .border(0.5.dp, GrayDivider, RoundedCornerShape(4.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "КАТЕГОРИЯ ВЕСА",
                        color = RedPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Spacer(Modifier.height(12.dp))

                    LazyColumn(modifier = Modifier.height(180.dp)) {
                        items(state.leagues) { league ->
                            val isSelected = state.selectedLeague == league
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(if (isSelected) RedDim.copy(alpha = 0.3f) else Color.Transparent)
                                    .border(
                                        width = if (isSelected) 0.5.dp else 0.dp,
                                        color = if (isSelected) RedPrimary else Color.Transparent,
                                        shape = RoundedCornerShape(3.dp)
                                    )
                                    .clickable { viewModel.onLeagueSelected(league) }
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .border(1.dp, if (isSelected) RedPrimary else GrayText, RoundedCornerShape(7.dp))
                                        .background(
                                            if (isSelected) RedPrimary else Color.Transparent,
                                            RoundedCornerShape(7.dp)
                                        )
                                )
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    text = league,
                                    color = if (isSelected) WhiteText else GrayText,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    Divider(color = GrayDivider, thickness = 0.5.dp)
                    Spacer(Modifier.height(14.dp))

                    // Архив переключатель
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Архив боёв", color = WhiteText, fontSize = 14.sp)
                            Text("Показать завершённые", color = GrayText, fontSize = 11.sp)
                        }
                        Switch(
                            checked = state.isArchive,
                            onCheckedChange = { viewModel.onArchiveToggled() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = WhiteText,
                                checkedTrackColor = RedPrimary,
                                uncheckedThumbColor = GrayText,
                                uncheckedTrackColor = BlackSurface
                            )
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    // Кнопка поиска
                    Button(
                        onClick = {
                            isSearching = true
                            viewModel.performSearch()
                            scope.launch { delay(1000); isSearching = false }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = state.selectedLeague.isNotBlank(),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RedPrimary,
                            disabledContainerColor = RedDim.copy(alpha = 0.4f)
                        )
                    ) {
                        Text(
                            "НАЙТИ БОИ",
                            fontWeight = FontWeight.Black,
                            letterSpacing = 3.sp,
                            fontSize = 13.sp
                        )
                    }

                    if (state.selectedLeague.isNotBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "▶  ${state.selectedLeague}",
                            color = RedPrimary,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            // Ошибка
            if (state.error != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(RedDim.copy(alpha = 0.2f))
                        .border(0.5.dp, RedPrimary.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                        .padding(12.dp)
                ) {
                    Text(state.error ?: "", color = RedBright, fontSize = 13.sp)
                }
            }

            // Результаты
            when {
                state.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = RedPrimary, strokeWidth = 3.dp)
                            Spacer(Modifier.height(12.dp))
                            Text("ПОИСК БОЁВ...", color = GrayText, letterSpacing = 2.sp, fontSize = 12.sp)
                        }
                    }
                }
                state.searchPerformed && state.matches.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🥊", fontSize = 48.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                if (state.isArchive) "Нет завершённых боёв в категории ${state.selectedLeague}"
                                else "Нет предстоящих боёв в категории ${state.selectedLeague}",
                                color = GrayText,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
                state.matches.isNotEmpty() -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.matches) { match ->
                            MatchCard(match = match, onClick = { onMatchClick(match.id) })
                        }
                    }
                }
                else -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🥊", fontSize = 48.sp)
                            Spacer(Modifier.height(8.dp))
                            Text("Выберите категорию и нажмите НАЙТИ БОИ", color = GrayText, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}
