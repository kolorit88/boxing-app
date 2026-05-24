package com.example.domain.usecase

import com.example.domain.model.Match
import com.example.domain.repository.MatchRepository

class GetUpcomingMatchesUseCase(
    private val repository: MatchRepository
) {
    suspend operator fun invoke(): Result<List<Match>> {
        return repository.getUpcomingMatches()
    }
}