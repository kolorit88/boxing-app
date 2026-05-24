package com.example.core.dto

import kotlinx.serialization.Serializable

@Serializable
data class MatchDto(
    val id: String,
    val home_team: String,
    val away_team: String,
    val date: String,
    val time: String? = null,
    val league: String,
    val country: String,
    val status: String,
    val home_score: String? = null,
    val away_score: String? = null,
    val venue: String? = null,
    val referee: String? = null
)