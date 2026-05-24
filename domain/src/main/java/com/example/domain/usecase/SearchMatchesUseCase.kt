package com.example.domain.usecase

import com.example.domain.model.Match
import com.example.domain.repository.MatchRepository

class SearchMatchesUseCase(
    private val repository: MatchRepository
) {
    suspend operator fun invoke(
        league: String,
        timeRange: String,
        isArchive: Boolean = false
    ): Result<List<Match>> {
        return if (league.isBlank()) {
            Result.failure(IllegalArgumentException("Лига не может быть пустой"))
        } else {
            repository.searchMatches(league, timeRange, isArchive)
        }
    }
}