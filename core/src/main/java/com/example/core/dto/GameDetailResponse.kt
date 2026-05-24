package com.example.core.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class GameDetailResponse(
    val status: String,
    val data: GameDetail
)

@Serializable
data class GameDetail(
    val id: Int,
    val homeTeam: TeamInfo,
    val awayTeam: TeamInfo,
    val date: String,
    val league: LeagueInfo,
    val statusName: String,
    val homeScore: Int? = null,
    val awayScore: Int? = null,
    val homeFTResult: Int? = null,
    val awayFTResult: Int? = null,
    val venue: VenueInfo? = null,
    val referee: String? = null,
    val roundName: String? = null,
    val periods: List<String>? = null,
    val elapsed: Int? = null,
    val lineups: JsonElement? = null,
    val events: JsonElement? = null,
    val statistics: JsonElement? = null
)

@Serializable
data class VenueInfo(
    val name: String,
    val city: String? = null,
    val capacity: Int? = null
)