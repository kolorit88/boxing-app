package com.example.football

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.football.analytics.CrashReporterManager
import com.example.feature.home.HomeScreen
import com.example.feature.match.MatchDetailScreen
import com.example.feature.search.SearchScreen
import org.koin.androidx.compose.koinViewModel
import android.widget.Toast
import androidx.compose.ui.unit.dp
import kotlin.random.Random

private val BlackPure  = Color(0xFF000000)
private val BlackDark  = Color(0xFF0A0A0A)
private val RedPrimary = Color(0xFFCC0000)
private val GrayText   = Color(0xFF999999)
private val GrayDivider= Color(0xFF222222)
private val WhiteText  = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = RedPrimary,
                    background = BlackDark,
                    surface = Color(0xFF111111),
                    onSurface = WhiteText,
                    onBackground = WhiteText,
                    surfaceVariant = Color(0xFF1A1A1A),
                    onSurfaceVariant = GrayText,
                    error = Color(0xFFFF4444),
                    primaryContainer = Color(0xFF330000),
                    onPrimaryContainer = WhiteText,
                    tertiary = Color(0xFF444444),
                    onTertiary = WhiteText,
                    errorContainer = Color(0xFF1A0000),
                    onErrorContainer = Color(0xFFFF6666),
                    secondaryContainer = Color(0xFF1A1A1A),
                    onSecondaryContainer = WhiteText
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize().background(BlackDark),
                    color = BlackDark
                ) {
                    val navController = rememberNavController()

                    Scaffold(
                        containerColor = BlackDark,
                        bottomBar = {
                            NavigationBar(
                                containerColor = BlackPure,
                                tonalElevation = 0.dp
                            ) {
                                val navBackStackEntry by navController.currentBackStackEntryAsState()
                                val currentRoute = navBackStackEntry?.destination?.route

                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Home, contentDescription = "Главная", tint = if (currentRoute == "home") RedPrimary else GrayText) },
                                    label = { Text("БОИ", fontSize = 10.sp, letterSpacing = 1.sp, color = if (currentRoute == "home") RedPrimary else GrayText) },
                                    selected = currentRoute == "home",
                                    onClick = {
                                        navController.navigate("home") {
                                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = RedPrimary,
                                        indicatorColor = Color(0xFF1A0000)
                                    )
                                )

                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Search, contentDescription = "Поиск", tint = if (currentRoute == "search") RedPrimary else GrayText) },
                                    label = { Text("ПОИСК", fontSize = 10.sp, letterSpacing = 1.sp, color = if (currentRoute == "search") RedPrimary else GrayText) },
                                    selected = currentRoute == "search",
                                    onClick = {
                                        navController.navigate("search") {
                                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = RedPrimary,
                                        indicatorColor = Color(0xFF1A0000)
                                    )
                                )
                            }
                        },
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        "🥊 BOXING",
                                        color = RedPrimary,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 20.sp,
                                        letterSpacing = 3.sp
                                    )
                                },
                                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlackPure),
                                actions = {
                                    IconButton(onClick = {
                                        startActivity(Intent(this@MainActivity, InfoActivity::class.java))
                                    }) {
                                        Icon(Icons.Default.Info, contentDescription = "Информация", tint = GrayText)
                                    }
                                    IconButton(onClick = {
                                        CrashReporterManager.instance.logMessage("Test crash button clicked")
                                        if (Random.nextBoolean()) {
                                            throw NullPointerException("Manual crash for testing")
                                        } else {
                                            Toast.makeText(this@MainActivity, "Нокаут не состоялся!", Toast.LENGTH_SHORT).show()
                                        }
                                    }) {
                                        Icon(Icons.Default.BugReport, contentDescription = "Test Crash", tint = GrayText)
                                    }
                                    IconButton(onClick = {
                                        startActivity(Intent(this@MainActivity, FirebaseTestActivity::class.java))
                                    }) {
                                        Icon(Icons.Default.Cloud, contentDescription = "Firebase", tint = GrayText)
                                    }
                                    IconButton(onClick = {
                                        startActivity(Intent(this@MainActivity, PredictionActivity::class.java))
                                    }) {
                                        Icon(Icons.Default.Psychology, contentDescription = "AI Прогноз", tint = RedPrimary)
                                    }
                                }
                            )
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "home",
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable("home") {
                                HomeScreen(
                                    viewModel = koinViewModel(),
                                    onMatchClick = { matchId -> navController.navigate("match/$matchId") }
                                )
                            }
                            composable("search") {
                                SearchScreen(
                                    viewModel = koinViewModel(),
                                    onMatchClick = { matchId -> navController.navigate("match/$matchId") }
                                )
                            }
                            composable(
                                route = "match/{matchId}",
                                arguments = listOf(navArgument("matchId") { type = NavType.StringType })
                            ) { backStackEntry ->
                                MatchDetailScreen(
                                    viewModel = koinViewModel(),
                                    matchId = backStackEntry.arguments?.getString("matchId") ?: "",
                                    onBackClick = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
