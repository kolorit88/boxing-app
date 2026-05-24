package com.example.domain.model

data class Match(
    val id: String,
    val homeTeam: String,
    val awayTeam: String,
    val dateTime: String,
    val league: String?,
    val country: String?,
    val status: MatchStatus,
    val score: String? = null
)

