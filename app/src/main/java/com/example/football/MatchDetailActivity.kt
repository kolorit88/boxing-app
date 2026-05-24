//package com.example.football
//
//import android.os.Bundle
//import android.widget.TextView
//import androidx.activity.ComponentActivity
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.ComposeView
//import androidx.compose.ui.unit.dp
//import androidx.appcompat.widget.Toolbar
//import androidx.lifecycle.lifecycleScope
//import com.example.domain.model.Match
//import com.example.domain.model.MatchStatus
//import com.example.domain.repository.MatchRepository
//import kotlinx.coroutines.launch
//import org.koin.android.ext.android.inject
//
//class MatchDetailActivity : ComponentActivity() {
//
//    private val repository: MatchRepository by inject()
//    private lateinit var tvTeams: TextView
//    private lateinit var tvDateTime: TextView
//    private lateinit var tvStatus: TextView
//    private lateinit var tvMatchTitle: TextView
//    private lateinit var composeView: ComposeView
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_match_detail)
//
//        val matchId = intent.getStringExtra("match_id") ?: ""
//        val matchTitle = intent.getStringExtra("match_title") ?: "Детали матча"
//
//        setupViews()
//        setupToolbar(matchTitle)
//
//        // Передаем данные в ComposeView
//        loadMatchData(matchId)
//    }
//
//    private fun setupViews() {
//        tvMatchTitle = findViewById(R.id.tvMatchTitle)
//        tvTeams = findViewById(R.id.tvTeams)
//        tvDateTime = findViewById(R.id.tvDateTime)
//        tvStatus = findViewById(R.id.tvStatus)
//        composeView = findViewById(R.id.composeView)
//    }
//
//    private fun setupToolbar(title: String) {
//        val toolbar = findViewById<Toolbar>(R.id.toolbar)
//        setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.title = title
//
//        toolbar.setNavigationOnClickListener {
//            finish()
//        }
//    }
//
//    private fun loadMatchData(matchId: String) {
//        lifecycleScope.launch {
//            val result = repository.getMatchById(matchId)
//            result.onSuccess { match ->
//                // Обновляем XML Views
//                updateXmlViews(match)
//
//                // Обновляем ComposeView с деталями
//                setupComposeView(match)
//            }.onFailure { error ->
//                tvTeams.text = "Ошибка загрузки: ${error.message}"
//            }
//        }
//    }
//
//    private fun updateXmlViews(match: Match) {
//        // Обновляем XML элементы
//        tvTeams.text = "${match.homeTeam}  VS  ${match.awayTeam}"
//        tvDateTime.text = match.dateTime
//
//        when (match.status) {
//            MatchStatus.LIVE -> {
//                tvStatus.text = "LIVE"
//                tvStatus.setBackgroundColor(getColor(android.R.color.holo_red_dark))
//            }
//            MatchStatus.FINISHED -> {
//                tvStatus.text = "Завершен"
//                tvStatus.setBackgroundColor(getColor(android.R.color.holo_blue_dark))
//            }
//            MatchStatus.UPCOMING -> {
//                tvStatus.text = "Предстоящий"
//                tvStatus.setBackgroundColor(getColor(R.color.purple_500))
//            }
//        }
//
//        tvMatchTitle.text = "${match.league} - ${match.country}"
//    }
//
//    private fun setupComposeView(match: Match) {
//        composeView.setContent {
//            MaterialTheme {
//                // Передаем данные матча в Compose
//                MatchStatsContent(match = match)
//            }
//        }
//    }
//}
//
//@Composable
//fun MatchStatsContent(match: Match) {
//    // Анимация загрузки статистики
//    var showStats by remember { mutableStateOf(false) }
//
//    LaunchedEffect(Unit) {
//        kotlinx.coroutines.delay(500)
//        showStats = true
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(
//            text = "Детальная статистика",
//            style = MaterialTheme.typography.titleLarge,
//            color = MaterialTheme.colorScheme.primary
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Счет
//        if (match.score != null) {
//            androidx.compose.animation.AnimatedVisibility(
//                visible = showStats,
//                enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.scaleIn()
//            ) {
//                Card(
//                    modifier = Modifier.fillMaxWidth(),
//                    elevation = CardDefaults.cardElevation(4.dp)
//                ) {
//                    Column(
//                        modifier = Modifier.padding(16.dp),
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//                        Text(
//                            text = "Счет",
//                            style = MaterialTheme.typography.titleMedium
//                        )
//                        Text(
//                            text = match.score,
//                            style = MaterialTheme.typography.displayMedium,
//                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
//                        )
//                    }
//                }
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Дополнительная статистика
//        androidx.compose.animation.AnimatedVisibility(
//            visible = showStats,
//            enter = androidx.compose.animation.slideInHorizontally() + androidx.compose.animation.fadeIn()
//        ) {
//            Card(
//                modifier = Modifier.fillMaxWidth(),
//                elevation = CardDefaults.cardElevation(4.dp)
//            ) {
//                Column(
//                    modifier = Modifier.padding(16.dp),
//                    verticalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    Text(
//                        text = "Информация",
//                        style = MaterialTheme.typography.titleMedium
//                    )
//                    InfoRow("Статус", when (match.status) {
//                        MatchStatus.LIVE -> "В прямом эфире"
//                        MatchStatus.FINISHED -> "Завершен"
//                        MatchStatus.UPCOMING -> "Предстоящий"
//                    })
//                    InfoRow("Лига", match.league)
//                    InfoRow("Страна", match.country)
//                    InfoRow("Дата", match.dateTime)
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun InfoRow(label: String, value: String) {
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.SpaceBetween
//    ) {
//        Text(text = label, style = MaterialTheme.typography.bodyMedium)
//        Text(text = value, style = MaterialTheme.typography.bodyMedium)
//    }
//}