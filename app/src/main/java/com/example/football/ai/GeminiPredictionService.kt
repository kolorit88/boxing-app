package com.example.football.ai

import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.tasks.await

object GeminiPredictionService {

    private const val API_KEY = "AIzaSyAzUw3whrP4ZNyzWEo6VyH-pdNDDAiND9w"

    private val generativeModel = GenerativeModel(
        modelName = "gemini-3.5-flash",
        apiKey = API_KEY
    )

    suspend fun getMatchPrediction(
        homeTeam: String,
        awayTeam: String,
        league: String
    ): Result<String> {
        return try {
            val prompt = """
                Ты — опытный футбольный аналитик. Дай краткий прогноз на матч.
                Команды: $homeTeam против $awayTeam.
                Лига: $league.
                Ответ должен быть на русском языке и содержать:
                1. Вероятный исход матча (победа одной из команд или ничья).
                2. Предполагаемый счет.
                3. Краткое обоснование (одним предложением).
                4. Смешную шутку для поднятия настроения.
                Ответ должен быть коротким и информативным, не более 3-х предложений.
            """.trimIndent()

            val response = generativeModel.generateContent(prompt)
            val prediction = response.text ?: "Не удалось получить прогноз от AI."

            Result.success(prediction)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}