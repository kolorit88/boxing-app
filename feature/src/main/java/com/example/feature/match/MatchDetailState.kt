package com.example.feature.match


import com.example.domain.model.Match

sealed interface MatchDetailState {
    data object Loading : MatchDetailState
    data class Success(val match: Match) : MatchDetailState
    data class Error(val message: String) : MatchDetailState
}