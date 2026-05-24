package com.example.data.repository

import com.example.data.mock.MockDataProvider
import com.example.domain.model.Match
import com.example.domain.repository.MatchRepository
import kotlinx.coroutines.delay

class MockMatchRepository : MatchRepository {

    override suspend fun getUpcomingMatches(days: Int): Result<List<Match>> {
        delay(500)
        return try {
            Result.success(MockDataProvider.getUpcomingMatches())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchMatches(
        league: String,
        timeRange: String,
        isArchive: Boolean
    ): Result<List<Match>> {
        delay(500)
        return try {
            val allMatches = MockDataProvider.getAllMatches()

            val matchesByStatus = if (isArchive) {
                allMatches.filter { it.status == com.example.domain.model.MatchStatus.FINISHED }
            } else {
                allMatches.filter { it.status != com.example.domain.model.MatchStatus.FINISHED }
            }

            val filteredMatches = if (league.isNotBlank()) {
                matchesByStatus.filter { it.league.equals(league, ignoreCase = true) }
            } else {
                matchesByStatus
            }

            Result.success(filteredMatches)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMatchById(matchId: String): Result<Match> {
        delay(300)
        return try {
            val match = MockDataProvider.getMatchById(matchId)
            if (match != null) {
                Result.success(match)
            } else {
                Result.failure(Exception("Матч не найден"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

