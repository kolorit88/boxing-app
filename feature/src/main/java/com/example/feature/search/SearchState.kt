package com.example.feature.search

import com.example.domain.model.Match

data class SearchState(
    val leagues: List<String> = emptyList(),
    val selectedLeague: String = "",
    val selectedTimeRange: String = "day",
    val isArchive: Boolean = false,
    val timeRanges: List<String> = listOf("day", "week", "month"),
    val matches: List<Match> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchPerformed: Boolean = false
)