package com.example.football

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import com.example.data.worker.MatchesSyncWorker
import com.example.domain.model.Match
import com.example.domain.model.MatchStatus
import com.example.domain.repository.MatchRepository
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*

class InfoActivity : AppCompatActivity() {

    private val repository: MatchRepository by inject()
    private lateinit var tvAppVersion: TextView
    private lateinit var tvBuildType: TextView
    private lateinit var tvLastSync: TextView
    private lateinit var tvInfoTitle: TextView
    private lateinit var composeView: ComposeView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        setupViews()
        setupToolbar()
        updateXmlData()
        setupComposeView()
    }

    private fun setupViews() {
        tvInfoTitle = findViewById(R.id.tvInfoTitle)
        tvAppVersion = findViewById(R.id.tvAppVersion)
        tvBuildType = findViewById(R.id.tvBuildType)
        tvLastSync = findViewById(R.id.tvLastSync)
        composeView = findViewById(R.id.composeView)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Информация"

        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun updateXmlData() {
        tvAppVersion.text = "Версия: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
        tvBuildType.text = "Сборка: ${if (BuildConfig.DEBUG) "Debug" else "Release"}"

        val lastSync = MatchesSyncWorker.getLastSyncTime(this)
        if (lastSync > 0) {
            val date = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(Date(lastSync))
            tvLastSync.text = "Последняя синхронизация: $date"
        } else {
            tvLastSync.text = "Последняя синхронизация: никогда"
        }
    }

    private fun setupComposeView() {
        composeView.setContent {
            MaterialTheme {
                InfoStatsContent(repository = repository)
            }
        }
    }
}

@Composable
fun InfoStatsContent(repository: MatchRepository) {
    var matches by remember { mutableStateOf<List<Match>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            val result = repository.getUpcomingMatches(30)
            result.onSuccess {
                matches = it
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = if (isLoading) "Загрузка статистики..." else "Статистика матчей",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Карточка с общей статистикой
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Общая статистика",
                        style = MaterialTheme.typography.titleMedium
                    )
                    StatRow("Всего матчей", matches.size.toString())
                    StatRow("Предстоящих", matches.count { it.status == MatchStatus.UPCOMING }.toString())
                    StatRow("LIVE", matches.count { it.status == MatchStatus.LIVE }.toString())
                    StatRow("Завершенных", matches.count { it.status == MatchStatus.FINISHED }.toString())
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Карточка со списком лиг
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "🏆 Доступные лиги",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val leagues = matches.map { it.league }.distinct()
                    LazyColumn(
                        modifier = Modifier.height(200.dp)
                    ) {
                        items(leagues) { league ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "• $league")
                                Text(
                                    text = "${matches.count { it.league == league }} матчей",
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}