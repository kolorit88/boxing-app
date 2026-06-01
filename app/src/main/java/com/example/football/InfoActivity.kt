package com.example.football

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.worker.MatchesSyncWorker
import com.example.domain.model.Match
import com.example.domain.model.MatchStatus
import com.example.domain.repository.MatchRepository
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*

private val BlackPure    = Color(0xFF000000)
private val BlackDark    = Color(0xFF0A0A0A)
private val BlackCard    = Color(0xFF111111)
private val BlackSurface = Color(0xFF1A1A1A)
private val RedPrimary   = Color(0xFFCC0000)
private val RedDark      = Color(0xFF8B0000)
private val WhiteText    = Color(0xFFF5F5F5)
private val GrayText     = Color(0xFF999999)
private val GrayDivider  = Color(0xFF2A2A2A)

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
        supportActionBar?.title = "О ПРИЛОЖЕНИИ"
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun updateXmlData() {
        tvInfoTitle.text = "🥊 BOXING APP"
        tvAppVersion.text = "Версия: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
        tvBuildType.text = "Сборка: ${if (BuildConfig.DEBUG) "Debug" else "Release"}"

        val lastSync = MatchesSyncWorker.getLastSyncTime(this)
        tvLastSync.text = if (lastSync > 0) {
            "Последняя синхронизация: ${SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(lastSync))}"
        } else {
            "Последняя синхронизация: никогда"
        }
    }

    private fun setupComposeView() {
        composeView.setContent {
            MaterialTheme(colorScheme = darkColorScheme(
                primary = RedPrimary,
                background = BlackDark,
                surface = BlackCard,
                onSurface = WhiteText,
                onBackground = WhiteText
            )) {
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
            repository.getUpcomingMatches(30).onSuccess {
                matches = it
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
        Text(
            text = if (isLoading) "ЗАГРУЗКА..." else "СТАТИСТИКА БОЁВ",
            color = RedPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )

        Spacer(Modifier.height(16.dp))

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RedPrimary, strokeWidth = 3.dp)
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(BlackCard)
                    .border(0.5.dp, GrayDivider, RoundedCornerShape(4.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("ОБЩАЯ СТАТИСТИКА", color = GrayText, fontSize = 11.sp, letterSpacing = 2.sp)
                    BoxingStatRow("Всего боёв", matches.size.toString())
                    Divider(color = GrayDivider, thickness = 0.5.dp)
                    BoxingStatRow("Предстоящих", matches.count { it.status == MatchStatus.UPCOMING }.toString())
                    Divider(color = GrayDivider, thickness = 0.5.dp)
                    BoxingStatRow("LIVE", matches.count { it.status == MatchStatus.LIVE }.toString())
                    Divider(color = GrayDivider, thickness = 0.5.dp)
                    BoxingStatRow("Завершённых", matches.count { it.status == MatchStatus.FINISHED }.toString())
                }
            }

            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(BlackCard)
                    .border(0.5.dp, GrayDivider, RoundedCornerShape(4.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text("КАТЕГОРИИ ВЕСОВ", color = GrayText, fontSize = 11.sp, letterSpacing = 2.sp)
                    Spacer(Modifier.height(12.dp))
                    val leagues = matches.mapNotNull { it.league }.distinct()
                    LazyColumn(modifier = Modifier.height(200.dp)) {
                        items(leagues) { league ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("▸  $league", color = WhiteText, fontSize = 13.sp)
                                Text(
                                    "${matches.count { it.league == league }} боёв",
                                    color = RedPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Divider(color = GrayDivider.copy(alpha = 0.5f), thickness = 0.5.dp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BoxingStatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = GrayText, fontSize = 13.sp)
        Text(value, color = WhiteText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

// Псевдоним для обратной совместимости
@Composable
fun StatRow(label: String, value: String) = BoxingStatRow(label, value)
