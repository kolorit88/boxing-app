package com.example.domain.usecase

import com.example.domain.model.Match
import com.example.domain.repository.MatchRepository

class GetArchiveMatchesUseCase(
    private val repository: MatchRepository
) {
    suspend operator fun invoke(
        league: String,
        timeRange: String
    ): Result<List<Match>> {
        if (league.isBlank()) {
            return Result.failure(IllegalArgumentException("Лига не может быть пустой"))
        }
        return repository.searchMatches(league, timeRange, isArchive = true)
    }
}