package com.example.football.ai

import com.google.ai.client.generativeai.GenerativeModel

object GeminiPredictionService {

    private const val API_KEY = ""

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
                Ты — опытный боксёрский аналитик и эксперт по единоборствам.
                Дай краткий профессиональный прогноз на бой.
                Боксёры: $homeTeam против $awayTeam.
                Категория веса / организация: $league.
                Ответ должен быть на русском языке и содержать:
                1. Вероятный победитель (и каким образом — нокаут, единогласное решение и т.д.).
                2. Предполагаемый раунд завершения или "по очкам".
                3. Краткое тактическое обоснование (одним-двумя предложениями).
                4. Уровень уверенности в прогнозе (низкий / средний / высокий).
                Ответ короткий и по делу — не более 4 строк.
            """.trimIndent()

            val response = generativeModel.generateContent(prompt)
            Result.success(response.text ?: "Не удалось получить прогноз от AI.")
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
