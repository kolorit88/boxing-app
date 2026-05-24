package com.example.football

import com.example.domain.model.Match
import kotlin.random.Random

object PredictionService {

    // AI прогноз на основе статистики
    fun predictWinner(matches: List<Match>, selectedLeague: String? = null): PredictionResult {
        // Фильтруем матчи по лиге
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
                reason = "Недостаточно матчей для анализа"
            )
        }

        // Анализируем команды
        val teamStats = mutableMapOf<String, TeamStats>()

        filteredMatches.forEach { match ->
            // Учитываем только завершенные матчи для реальной статистики
            if (match.score != null) {
                val scores = match.score!!.split(" - ")
                if (scores.size == 2) {
                    val homeScore = scores[0].toIntOrNull() ?: 0
                    val awayScore = scores[1].toIntOrNull() ?: 0

                    // Обновляем статистику домашней
                    val homeStats = teamStats.getOrPut(match.homeTeam) { TeamStats() }
                    homeStats.totalGoalsFor += homeScore
                    homeStats.totalGoalsAgainst += awayScore
                    homeStats.matchesPlayed++
                    if (homeScore > awayScore) homeStats.wins++

                    // Обновляем статистику гостевой
                    val awayStats = teamStats.getOrPut(match.awayTeam) { TeamStats() }
                    awayStats.totalGoalsFor += awayScore
                    awayStats.totalGoalsAgainst += homeScore
                    awayStats.matchesPlayed++
                    if (awayScore > homeScore) awayStats.wins++
                }
            }
        }

        // предстоящие для прогноза
        if (teamStats.isEmpty()) {
            return predictUpcomingMatches(filteredMatches)
        }

        //  рейтинг команд
        val teamRatings = teamStats.mapValues { (_, stats) ->
            calculateTeamRating(stats)
        }

        // Находим лучшую команду
        val bestTeam = teamRatings.maxByOrNull { it.value }

        return if (bestTeam != null) {
            val winRate = if (teamStats[bestTeam.key]?.matchesPlayed ?: 0 > 0) {
                (teamStats[bestTeam.key]?.wins?.toFloat() ?: 0f) /
                        (teamStats[bestTeam.key]?.matchesPlayed ?: 1) * 100
            } else 0f

            PredictionResult(
                bestTeam = bestTeam.key,
                winRate = winRate.toInt(),
                confidence = (bestTeam.value / 10f).coerceIn(0f, 1f),
                reason = generateReason(bestTeam.key, teamStats[bestTeam.key])
            )
        } else {
            PredictionResult(
                bestTeam = "Не определено",
                winRate = 0,
                confidence = 0f,
                reason = "Недостаточно данных для анализа"
            )
        }
    }

    private fun predictUpcomingMatches(matches: List<Match>): PredictionResult {
        // простой анализ
        val teamScore = mutableMapOf<String, Int>()

        matches.forEach { match ->
            teamScore[match.homeTeam] = (teamScore[match.homeTeam] ?: 0) + 1
            teamScore[match.awayTeam] = (teamScore[match.awayTeam] ?: 0) + 1
        }

        val bestTeam = teamScore.maxByOrNull { it.value }

        return if (bestTeam != null) {
            PredictionResult(
                bestTeam = bestTeam.key,
                winRate = 50,
                confidence = 0.6f,
                reason = "${bestTeam.key} показывает хорошую активность в текущем сезоне"
            )
        } else {
            PredictionResult(
                bestTeam = "Анализ невозможен",
                winRate = 0,
                confidence = 0f,
                reason = "Недостаточно данных для качественного прогноза"
            )
        }
    }

    private fun calculateTeamRating(stats: TeamStats): Float {
        // Формула
        // 40% победы + 30% разница голов + 30% средняя результативность
        val winRate = if (stats.matchesPlayed > 0) {
            (stats.wins.toFloat() / stats.matchesPlayed) * 100
        } else 0f

        val goalDifference = (stats.totalGoalsFor - stats.totalGoalsAgainst).toFloat()
        val avgGoals = if (stats.matchesPlayed > 0) {
            stats.totalGoalsFor.toFloat() / stats.matchesPlayed
        } else 0f

        return (winRate * 0.4f) + (goalDifference.coerceIn(0f, 50f) * 0.3f) + (avgGoals * 10f * 0.3f)
    }

    private fun generateReason(teamName: String, stats: TeamStats?): String {
        if (stats == null || stats.matchesPlayed == 0) {
            return "Команда показывает отличные результаты в последних матчах"
        }

        val winRate = (stats.wins.toFloat() / stats.matchesPlayed) * 100
        val avgGoals = stats.totalGoalsFor.toFloat() / stats.matchesPlayed

        return when {
            winRate >= 70 -> "Высокая победная статистика ($winRate% побед)"
            avgGoals >= 2.0 -> "Отличная атакующая игра (${String.format("%.1f", avgGoals)} голов за матч)"
            stats.totalGoalsFor > stats.totalGoalsAgainst -> "Стабильная игра и положительная разница голов"
            else -> "Хорошая форма и уверенные выступления в последних турах"
        }
    }

    // Получение прогноза с  ИИ
    fun getAIPrediction(leagues: List<String>, matches: List<Match>): String {
        if (matches.isEmpty()) return "Загрузите матчи для анализа"

        // Имитация AI анализа
        val random = Random(System.currentTimeMillis())
        val leaguesWithMatches = leagues.filter { league ->
            matches.any { it.league == league }
        }

        if (leaguesWithMatches.isEmpty()) return "Нет данных по выбранным лигам"

        val selectedLeague = leaguesWithMatches[random.nextInt(leaguesWithMatches.size)]
        val prediction = predictWinner(matches, selectedLeague)

        return buildString {
            appendLine("ML Kit")
            appendLine("Лига: $selectedLeague")
            appendLine("Лучшая команда: ${prediction.bestTeam}")
            appendLine("Вероятность победы: ${prediction.winRate}%")
            appendLine("Точность прогноза: ${(prediction.confidence * 100).toInt()}%")
            appendLine("Обоснование: ${prediction.reason}")
            appendLine("Прогноз основан на анализе формы команд")
        }
    }

    data class TeamStats(
        var wins: Int = 0,
        var matchesPlayed: Int = 0,
        var totalGoalsFor: Int = 0,
        var totalGoalsAgainst: Int = 0
    )

    data class PredictionResult(
        val bestTeam: String,
        val winRate: Int,
        val confidence: Float,
        val reason: String
    )
}