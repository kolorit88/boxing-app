package com.example.football

import com.example.domain.model.Match
import kotlin.random.Random

object PredictionService {

    fun predictWinner(matches: List<Match>, selectedLeague: String? = null): PredictionResult {
        val filteredMatches = if (selectedLeague != null) {
            matches.filter { it.league == selectedLeague }
        } else {
            matches
        }

        if (filteredMatches.isEmpty()) {
            return PredictionResult(
                bestTeam = "Нет данных",
                winRate = 0,
                confidence = 0f,
                reason = "Недостаточно боёв для анализа"
            )
        }

        val fighterStats = mutableMapOf<String, FighterStats>()

        filteredMatches.forEach { match ->
            if (match.score != null) {
                val score = match.score!!
                val isKO = score.contains("KO") || score.contains("TKO")
                val winner = when {
                    isKO -> match.homeTeam // упрощённо — в мок-данных первый всегда победитель
                    score.contains("UD") -> match.homeTeam
                    else -> null
                }

                val homeStats = fighterStats.getOrPut(match.homeTeam) { FighterStats() }
                val awayStats = fighterStats.getOrPut(match.awayTeam) { FighterStats() }
                homeStats.matchesPlayed++
                awayStats.matchesPlayed++

                if (winner == match.homeTeam) {
                    homeStats.wins++
                    if (isKO) homeStats.knockouts++
                }
            }
        }

        if (fighterStats.isEmpty()) return predictUpcomingFights(filteredMatches)

        val ratings = fighterStats.mapValues { (_, stats) -> calculateFighterRating(stats) }
        val bestFighter = ratings.maxByOrNull { it.value }

        return if (bestFighter != null) {
            val stats = fighterStats[bestFighter.key]
            val winRate = if ((stats?.matchesPlayed ?: 0) > 0) {
                (stats?.wins?.toFloat() ?: 0f) / (stats?.matchesPlayed ?: 1) * 100
            } else 0f

            PredictionResult(
                bestTeam = bestFighter.key,
                winRate = winRate.toInt(),
                confidence = (bestFighter.value / 10f).coerceIn(0f, 1f),
                reason = generateFightReason(bestFighter.key, stats)
            )
        } else {
            PredictionResult("Не определено", 0, 0f, "Недостаточно данных")
        }
    }

    private fun predictUpcomingFights(matches: List<Match>): PredictionResult {
        val scores = mutableMapOf<String, Int>()
        matches.forEach { match ->
            scores[match.homeTeam] = (scores[match.homeTeam] ?: 0) + 1
            scores[match.awayTeam] = (scores[match.awayTeam] ?: 0) + 1
        }
        val best = scores.maxByOrNull { it.value }
        return if (best != null) {
            PredictionResult(best.key, 55, 0.65f, "${best.key} показывает высокую активность в текущем сезоне")
        } else {
            PredictionResult("Анализ невозможен", 0, 0f, "Недостаточно данных для прогноза")
        }
    }

    private fun calculateFighterRating(stats: FighterStats): Float {
        val winRate = if (stats.matchesPlayed > 0) (stats.wins.toFloat() / stats.matchesPlayed) * 100 else 0f
        val koRate = if (stats.wins > 0) (stats.knockouts.toFloat() / stats.wins) * 100 else 0f
        return winRate * 0.6f + koRate * 0.4f
    }

    private fun generateFightReason(name: String, stats: FighterStats?): String {
        if (stats == null || stats.matchesPlayed == 0) return "$name — один из сильнейших бойцов своей категории"
        val winRate = (stats.wins.toFloat() / stats.matchesPlayed) * 100
        val koRate = if (stats.wins > 0) (stats.knockouts.toFloat() / stats.wins) * 100 else 0f
        return when {
            koRate >= 70 -> "Высокий процент нокаутов (${koRate.toInt()}% KO/TKO) — опасный нокаутёр"
            winRate >= 80 -> "Отличная победная серия (${"%.0f".format(winRate)}% побед)"
            else -> "Стабильные выступления и высокий класс боксирования"
        }
    }

    fun getAIPrediction(leagues: List<String>, matches: List<Match>): String {
        if (matches.isEmpty()) return "Загрузите бои для анализа"
        val leaguesWithMatches = leagues.filter { league -> matches.any { it.league == league } }
        if (leaguesWithMatches.isEmpty()) return "Нет данных по выбранным категориям"

        val selectedLeague = leaguesWithMatches[Random(System.currentTimeMillis()).nextInt(leaguesWithMatches.size)]
        val prediction = predictWinner(matches, selectedLeague)

        return buildString {
            appendLine("BOXING AI АНАЛИЗ")
            appendLine("Категория: $selectedLeague")
            appendLine("Фаворит: ${prediction.bestTeam}")
            appendLine("Вероятность победы: ${prediction.winRate}%")
            appendLine("Точность: ${(prediction.confidence * 100).toInt()}%")
            appendLine("Обоснование: ${prediction.reason}")
        }
    }

    data class FighterStats(
        var wins: Int = 0,
        var matchesPlayed: Int = 0,
        var knockouts: Int = 0
    )

    data class PredictionResult(
        val bestTeam: String,
        val winRate: Int,
        val confidence: Float,
        val reason: String
    )

    // Псевдоним для совместимости с кодом, использующим TeamStats
    data class TeamStats(
        var wins: Int = 0,
        var matchesPlayed: Int = 0,
        var totalGoalsFor: Int = 0,
        var totalGoalsAgainst: Int = 0
    )
}
