package com.example.domain.repository

import com.example.domain.model.Match

interface MatchRepository {
    suspend fun getUpcomingMatches(days: Int = 7): Result<List<Match>>
    suspend fun searchMatches(
        league: String,
        timeRange: String,
        isArchive: Boolean = false
    ): Result<List<Match>>
    suspend fun getMatchById(matchId: String): Result<Match>
}