package com.example.core.dto

import kotlinx.serialization.Serializable

@Serializable
data class LeaguesResponse(
    val status: String,
    val data: List<League>
)

@Serializable
data class League(
    val id: Int,
    val name: String,
    val country: CountryInfo,
    val seasons: List<Season>,
    val flashScoreId: String? = null
)

@Serializable
data class Season(
    val uid: String,
    val year: Int,
    val dateStart: String,
    val dateEnd: String,
    val flashScoreId: String? = null
)