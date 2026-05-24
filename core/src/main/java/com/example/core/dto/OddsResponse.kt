package com.example.core.dto

import kotlinx.serialization.Serializable

@Serializable
data class OddsResponse(
    val status: String,
    val count: Int,
    val data: List<BookmakerOdds>
)

@Serializable
data class BookmakerOdds(
    val bookmakerId: Int,
    val bookmakerName: String,
    val odds: List<Market>
)

@Serializable
data class Market(
    val marketId: Int,
    val marketName: String,
    val odds: List<Outcome>
)

@Serializable
data class Outcome(
    val name: String,
    val value: Double,
    val openingValue: Double? = null
)