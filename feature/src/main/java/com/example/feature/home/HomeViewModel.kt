package com.example.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.GetUpcomingMatchesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getUpcomingMatchesUseCase: GetUpcomingMatchesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<HomeState>(HomeState.Loading)
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadMatches()
    }

    fun loadMatches() {
        viewModelScope.launch {
            _state.value = HomeState.Loading
            getUpcomingMatchesUseCase()
                .onSuccess { matches ->
                    _state.value = HomeState.Success(matches)
                }
                .onFailure { error ->
                    _state.value = HomeState.Error(
                        error.message ?: "Неизвестная ошибка"
                    )
                }
        }
    }

    fun refresh() {
        loadMatches()
    }
}