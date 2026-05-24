package com.example.football.di

import com.example.data.repository.MockMatchRepository
import com.example.domain.repository.MatchRepository
import com.example.domain.usecase.GetArchiveMatchesUseCase
import com.example.domain.usecase.GetUpcomingMatchesUseCase
import com.example.domain.usecase.SearchMatchesUseCase
import com.example.feature.home.HomeViewModel
import com.example.feature.match.MatchViewModel
import com.example.feature.search.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<MatchRepository> { MockMatchRepository() }

    factory { GetUpcomingMatchesUseCase(get()) }
    factory { SearchMatchesUseCase(get()) }
    factory { GetArchiveMatchesUseCase(get()) }

    viewModel { HomeViewModel(get()) }
    viewModel { SearchViewModel(get()) }
    viewModel { parameters ->
        MatchViewModel(
            matchRepository = get(),
            savedStateHandle = parameters.get()
        )
    }
}