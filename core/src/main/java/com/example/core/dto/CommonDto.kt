package com.example.core.dto


import kotlinx.serialization.Serializable

@Serializable
data class TeamInfo(
    val id: Int,
    val name: String,
    val flashId: String? = null,
    val country: CountryInfo? = null
)

@Serializable
data class CountryInfo(
    val code: String,
    val name: String
)

@Serializable
data class LeagueInfo(
    val id: Int,
    val name: String,
    val country: CountryInfo,
    val flashScoreId: String? = null
)

@Serializable
data class SeasonInfo(
    val uid: String,
    val year: Int,
    val league: LeagueInfo? = null
)