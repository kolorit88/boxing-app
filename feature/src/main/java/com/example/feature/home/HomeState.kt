package com.example.feature.home

import com.example.domain.model.Match

sealed interface HomeState {
    data object Loading : HomeState
    data class Success(val matches: List<Match>) : HomeState
    data class Error(val message: String) : HomeState
}