package com.example.data.mock

import com.example.domain.model.Match
import com.example.domain.model.MatchStatus

object MockDataProvider {

    // Предстоящие бои
    fun getUpcomingMatches(): List<Match> = listOf(
        Match("1", "Саул Альварес", "Дмитрий Бивол", "20.09.2025 04:00", "Суперсредний вес • WBC", "США", MatchStatus.UPCOMING, null),
        Match("2", "Тайсон Фьюри", "Александр Усик", "19.07.2025 03:00", "Абсолютный чемпион • IBF/WBA/WBC/WBO", "Великобритания", MatchStatus.UPCOMING, null),
        Match("3", "Деонтей Уайлдер", "Жозуэ Маркес", "05.07.2025 03:30", "Тяжёлый вес • WBC", "США", MatchStatus.UPCOMING, null),
        Match("4", "Эрол Спенс", "Тиофимо Лопес", "12.07.2025 04:00", "Первый полусредний вес • IBF", "США", MatchStatus.UPCOMING, null),
        Match("5", "Артур Бетербиев", "Дмитрий Бивол", "12.10.2025 03:00", "Полутяжёлый вес • IBF/WBC/WBO/WBA", "Канада", MatchStatus.UPCOMING, null),
        Match("6", "Наоя Иноуэ", "Луис Нери", "09.08.2025 15:00", "Второй легчайший вес • WBA/IBF/WBC/WBO", "Япония", MatchStatus.UPCOMING, null),
        Match("7", "Джермалл Чарло", "Джон Риел Касимеро", "26.07.2025 04:00", "Второй средний вес • WBC", "США", MatchStatus.UPCOMING, null),
        Match("8", "Василий Ломаченко", "Хорхе Линарес", "30.08.2025 04:00", "Лёгкий вес • WBA", "Украина", MatchStatus.UPCOMING, null),
        Match("9", "Энтони Джошуа", "Отто Валлин", "14.06.2025 22:00", "Тяжёлый вес • WBA", "Великобритания", MatchStatus.UPCOMING, null),
        Match("10", "Джейк Пол", "Майк Перри", "06.07.2025 04:00", "Крейсерский вес", "США", MatchStatus.UPCOMING, null)
    )

    // Завершённые бои
    fun getFinishedMatches(): List<Match> = listOf(
        Match("101", "Александр Усик", "Тайсон Фьюри", "18.05.2024 02:00", "Тяжёлый вес • Абсолют", "Саудовская Аравия", MatchStatus.FINISHED, "UD • 115-113"),
        Match("102", "Саул Альварес", "Хайме Мунгия", "04.05.2024 04:00", "Суперсредний вес • WBC/WBA/IBF/WBO", "Мексика", MatchStatus.FINISHED, "UD • 118-110"),
        Match("103", "Наоя Иноуэ", "Маршалл Марлон", "06.07.2024 15:00", "Второй легчайший вес", "Япония", MatchStatus.FINISHED, "TKO 7"),
        Match("104", "Артур Бетербиев", "Дмитрий Бивол", "12.10.2024 04:00", "Полутяжёлый вес • Абсолют", "Саудовская Аравия", MatchStatus.FINISHED, "MD • 114-114"),
        Match("105", "Деонтей Уайлдер", "Джозеф Паркер", "01.03.2024 04:00", "Тяжёлый вес", "США", MatchStatus.FINISHED, "KO 5"),
        Match("106", "Джермалл Чарло", "Мостафа Лакраа", "16.03.2024 04:00", "Второй средний вес", "США", MatchStatus.FINISHED, "TKO 6"),
        Match("107", "Василий Ломаченко", "Джордж Камбосос", "11.05.2024 15:00", "Лёгкий вес", "Австралия", MatchStatus.FINISHED, "UD • 117-111"),
        Match("108", "Эрол Спенс", "Тенси Кроуфорд", "29.07.2023 04:00", "Полусредний вес • Абсолют", "США", MatchStatus.FINISHED, "SD • 114-113"),
        Match("109", "Хасан Усман", "Бад Кроуфорд", "23.03.2024 04:00", "Первый полусредний вес", "США", MatchStatus.FINISHED, "UD • 119-109"),
        Match("110", "Энтони Джошуа", "Нганну Франсис", "08.03.2024 23:00", "Тяжёлый вес", "Саудовская Аравия", MatchStatus.FINISHED, "KO 2")
    )

    // Бои в прямом эфире
    fun getLiveMatches(): List<Match> = listOf(
        Match("201", "Саул Альварес", "Дэвид Бенавидес", "Сегодня 04:00", "Суперсредний вес • WBC", "США", MatchStatus.LIVE, "Раунд 8"),
        Match("202", "Тайсон Фьюри", "Джозеф Паркер", "Сегодня 02:30", "Тяжёлый вес", "Великобритания", MatchStatus.LIVE, "Раунд 5"),
        Match("203", "Наоя Иноуэ", "Тасуки Акаи", "Сегодня 15:00", "Второй легчайший вес", "Япония", MatchStatus.LIVE, "Раунд 3")
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
        "Тяжёлый вес",
        "Полутяжёлый вес",
        "Суперсредний вес",
        "Средний вес",
        "Полусредний вес",
        "Лёгкий вес"
    )

    fun getLiveMatchesCount(): Int = getLiveMatches().size
}
