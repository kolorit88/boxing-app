package com.example.core.network

import com.example.core.dto.GameDetailResponse
import com.example.core.dto.GamesListResponse
import com.example.core.dto.LeaguesResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("leagues")
    suspend fun getLeagues(): LeaguesResponse

    @GET("Games/list")
    suspend fun getGamesList(
        @Query("leagueid") leagueId: Int? = null,
        @Query("year") year: Int? = null,
        @Query("team") teamId: Int? = null,
        @Query("from") fromDate: String? = null,
        @Query("to") toDate: String? = null,
        @Query("ended") ended: Boolean? = null,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): GamesListResponse

    @GET("Games/{id}")
    suspend fun getGameDetails(
        @Path("id") gameId: Int
    ): GameDetailResponse
}