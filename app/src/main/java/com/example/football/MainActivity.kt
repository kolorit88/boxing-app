package com.example.football

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    Scaffold(
                        bottomBar = {
                            NavigationBar {
                                val navBackStackEntry by navController.currentBackStackEntryAsState()
                                val currentRoute = navBackStackEntry?.destination?.route

                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Home, contentDescription = "Главная") },
                                    label = { Text("Матчи") },
                                    selected = currentRoute == "home",
                                    onClick = {
                                        navController.navigate("home") {
                                            popUpTo(navController.graph.startDestinationId) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )

                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Search, contentDescription = "Поиск") },
                                    label = { Text("Поиск") },
                                    selected = currentRoute == "search",
                                    onClick = {
                                        navController.navigate("search") {
                                            popUpTo(navController.graph.startDestinationId) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        },
                        topBar = {
                            TopAppBar(
                                title = { Text("Футбольные матчи") },
                                actions = {
                                    // Кнопка для InfoActivity (XML + ComposeView)
                                    IconButton(
                                        onClick = {
                                            val intent = Intent(this@MainActivity, InfoActivity::class.java)
                                            startActivity(intent)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = "Информация"
                                        )
                                    }

                                    // Кнопка краш
                                    IconButton(
                                        onClick = {
                                            CrashReporterManager.instance.logMessage("Test crash button clicked")

                                            if (Random.nextBoolean()) {
                                                throw NullPointerException("Manual crash for testing - DEMONSTRATION PURPOSE")
                                            } else {
                                                Toast.makeText(
                                                    this@MainActivity,
                                                    "Повезло! Краша не произошло :)",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.BugReport,
                                            contentDescription = "Test Crash"
                                        )
                                    }

                                    // Кнопка для Firebase теста
                                    IconButton(
                                        onClick = {
                                            val intent = Intent(this@MainActivity, FirebaseTestActivity::class.java)
                                            startActivity(intent)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Cloud,
                                            contentDescription = "Firebase"
                                        )
                                    }

                                    // Кнопка для AI прогноза
                                    IconButton(
                                        onClick = {
                                            val intent = Intent(this@MainActivity, PredictionActivity::class.java)
                                            startActivity(intent)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Psychology,
                                            contentDescription = "AI Прогноз"
                                        )
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
                                    onMatchClick = { matchId ->
                                        navController.navigate("match/$matchId")
                                    }
                                )
                            }

                            composable("search") {
                                SearchScreen(
                                    viewModel = koinViewModel(),
                                    onMatchClick = { matchId ->
                                        navController.navigate("match/$matchId")
                                    }
                                )
                            }

                            composable(
                                route = "match/{matchId}",
                                arguments = listOf(
                                    navArgument("matchId") {
                                        type = NavType.StringType
                                    }
                                )
                            ) { backStackEntry ->
                                val matchId = backStackEntry.arguments?.getString("matchId") ?: ""
                                MatchDetailScreen(
                                    viewModel = koinViewModel(),
                                    matchId = matchId,
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