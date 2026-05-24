package com.example.core.dto

import kotlinx.serialization.Serializable

@Serializable
data class GamesListResponse(
    val status: String,
    val count: Int,
    val data: List<GameSummary>
)

@Serializable
data class GameSummary(
    val id: Int,
    val flashId: String?,
    val date: String,
    val dateUtc: Long,
    val status: Int,
    val periods: List<String>,
    val statusName: String,
    val elapsed: Int? = null,
    val homeResult: Int,
    val awayResult: Int,
    val homeHTResult: Int?,
    val awayHTResult: Int?,
    val homeFTResult: Int,
    val awayFTResult: Int,
    val homeTeam: TeamInfo,
    val awayTeam: TeamInfo,
    val season: SeasonInfo,
    val roundName: String,
    val odds: List<OddsMarket>? = null
)

@Serializable
data class OddsMarket(
    val marketId: Int,
    val marketName: String? = null,
    val odds: List<Outcome>
)

