package com.example.data.mock

import com.example.domain.model.Match
import com.example.domain.model.MatchStatus

object MockDataProvider {

    // Предстоящие матчи
    fun getUpcomingMatches(): List<Match> = listOf(
        Match("1", "Реал Мадрид", "Барселона", "15.03.2025 22:00", "Ла Лига", "Испания", MatchStatus.UPCOMING, null),
        Match("2", "Ливерпуль", "Манчестер Сити", "16.03.2025 20:30", "АПЛ", "Англия", MatchStatus.UPCOMING, null),
        Match("3", "Бавария", "Боруссия Дортмунд", "16.03.2025 19:30", "Бундеслига", "Германия", MatchStatus.UPCOMING, null),
        Match("4", "ПСЖ", "Марсель", "16.03.2025 21:45", "Лига 1", "Франция", MatchStatus.UPCOMING, null),
        Match("5", "Милан", "Интер", "17.03.2025 21:45", "Серия А", "Италия", MatchStatus.UPCOMING, null),
        Match("6", "Зенит", "Спартак", "17.03.2025 19:00", "РПЛ", "Россия", MatchStatus.UPCOMING, null),
        Match("7", "Арсенал", "Челси", "18.03.2025 20:00", "АПЛ", "Англия", MatchStatus.UPCOMING, null),
        Match("8", "Атлетико Мадрид", "Севилья", "18.03.2025 23:00", "Ла Лига", "Испания", MatchStatus.UPCOMING, null),
        Match("9", "Ювентус", "Рома", "19.03.2025 22:00", "Серия А", "Италия", MatchStatus.UPCOMING, null),
        Match("10", "Байер Леверкузен", "РБ Лейпциг", "19.03.2025 20:30", "Бундеслига", "Германия", MatchStatus.UPCOMING, null)
    )

    // Завершенные матчи
    fun getFinishedMatches(): List<Match> = listOf(
        Match("101", "Манчестер Юнайтед", "Тоттенхэм", "10.03.2025 20:00", "АПЛ", "Англия", MatchStatus.FINISHED, "2 - 0"),
        Match("102", "Ювентус", "Наполи", "11.03.2025 22:00", "Серия А", "Италия", MatchStatus.FINISHED, "1 - 1"),
        Match("103", "Ливерпуль", "Эвертон", "12.03.2025 21:00", "АПЛ", "Англия", MatchStatus.FINISHED, "3 - 1"),
        Match("104", "Барселона", "Атлетик Бильбао", "13.03.2025 23:00", "Ла Лига", "Испания", MatchStatus.FINISHED, "4 - 0"),
        Match("105", "Бавария", "Боруссия Мёнхенгладбах", "09.03.2025 19:30", "Бундеслига", "Германия", MatchStatus.FINISHED, "2 - 2"),
        Match("106", "ПСЖ", "Лион", "08.03.2025 21:45", "Лига 1", "Франция", MatchStatus.FINISHED, "3 - 0"),
        Match("107", "Интер", "Аталанта", "08.03.2025 20:00", "Серия А", "Италия", MatchStatus.FINISHED, "1 - 0"),
        Match("108", "Зенит", "ЦСКА", "09.03.2025 19:00", "РПЛ", "Россия", MatchStatus.FINISHED, "2 - 1"),
        Match("109", "Реал Мадрид", "Атлетико Мадрид", "07.03.2025 23:00", "Ла Лига", "Испания", MatchStatus.FINISHED, "1 - 1"),
        Match("110", "Манчестер Сити", "Арсенал", "06.03.2025 21:00", "АПЛ", "Англия", MatchStatus.FINISHED, "2 - 2")
    )

    // LIVE матчи
    fun getLiveMatches(): List<Match> = listOf(
        Match("201", "Реал Мадрид", "Атлетико Мадрид", "Сегодня 22:00", "Ла Лига", "Испания", MatchStatus.LIVE, "1 - 0"),
        Match("202", "Манчестер Сити", "Арсенал", "Сегодня 21:30", "АПЛ", "Англия", MatchStatus.LIVE, "0 - 0"),
        Match("203", "Милан", "Ювентус", "Сегодня 20:00", "Серия А", "Италия", MatchStatus.LIVE, "2 - 2")
    )

    fun getAllMatches(): List<Match> = getUpcomingMatches() + getFinishedMatches() + getLiveMatches()

    fun getMatchById(id: String): Match? {
        return getAllMatches().find { it.id == id }
    }

    fun searchMatches(league: String, isArchive: Boolean): List<Match> {
        val source = if (isArchive) getFinishedMatches() else getUpcomingMatches() + getLiveMatches()
        return if (league.isNotBlank()) {
            source.filter { it.league.equals(league, ignoreCase = true) }
        } else {
            source
        }
    }

    fun getLeagues(): List<String> = listOf(
        "АПЛ", "Ла Лига", "Бундеслига", "Серия А", "Лига 1", "РПЛ"
    )

    fun getLiveMatchesCount(): Int = getLiveMatches().size
}