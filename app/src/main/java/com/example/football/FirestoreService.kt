package com.example.football

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object FirestoreService {

    private val db = Firebase.firestore

    // Коллекции
    private val matchesCollection = db.collection("matches")
    private val settingsCollection = db.collection("settings")
    private val usersCollection = db.collection("users")

    // Сохранение матча
    suspend fun saveMatch(matchId: String, data: Map<String, Any>) {
        matchesCollection.document(matchId).set(data).await()
    }

    // Получение матча
    suspend fun getMatch(matchId: String): MatchData? {
        val document = matchesCollection.document(matchId).get().await()
        return document.toObject<MatchData>()
    }

    // Получение всех матчей
    suspend fun getAllMatches(): List<MatchData> {
        val snapshot = matchesCollection.get().await()
        return snapshot.documents.mapNotNull { it.toObject<MatchData>() }
    }

    // Получение матчей по лиге
    suspend fun getMatchesByLeague(league: String): List<MatchData> {
        val snapshot = matchesCollection
            .whereEqualTo("league", league)
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.toObject<MatchData>() }
    }

    // Реальный поток матчей
    fun observeMatches(): Flow<List<MatchData>> = callbackFlow {
        val subscription = matchesCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val matches = snapshot?.documents?.mapNotNull { it.toObject<MatchData>() } ?: emptyList()
            trySend(matches)
        }
        awaitClose { subscription.remove() }
    }

    // Сохранение настроек пользователя
    suspend fun saveUserSettings(userId: String, settings: UserSettings) {
        usersCollection.document(userId).set(settings).await()
    }

    // Получение настроек пользователя
    suspend fun getUserSettings(userId: String): UserSettings? {
        val document = usersCollection.document(userId).get().await()
        return document.toObject<UserSettings>()
    }
}

// Data классы для Firestore
data class MatchData(
    val id: String = "",
    val homeTeam: String = "",
    val awayTeam: String = "",
    val dateTime: String = "",
    val league: String = "",
    val country: String = "",
    val status: String = "",
    val homeScore: Int? = null,
    val awayScore: Int? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class UserSettings(
    val userId: String = "",
    val favoriteLeague: String = "",
    val notificationsEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)