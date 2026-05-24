package com.example.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.SearchMatchesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchMatchesUseCase: SearchMatchesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    init {
        loadLeagues()
    }

    private fun loadLeagues() {
        _state.update { state ->
            state.copy(
                leagues = listOf(
                    "АПЛ",
                    "Ла Лига",
                    "Бундеслига",
                    "Серия А",
                    "Лига 1",
                    "РПЛ"
                )
            )
        }
    }

    fun onLeagueSelected(league: String) {
        _state.update { it.copy(selectedLeague = league) }
    }

    fun onTimeRangeSelected(timeRange: String) {
        _state.update { it.copy(selectedTimeRange = timeRange) }
    }

    fun onArchiveToggled() {
        _state.update { it.copy(isArchive = !it.isArchive) }
    }

    fun performSearch() {
        val currentState = _state.value
        if (currentState.selectedLeague.isBlank()) {
            _state.update { it.copy(error = "Выберите лигу") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, matches = emptyList(), searchPerformed = false) }

            val result = searchMatchesUseCase(
                league = currentState.selectedLeague,
                timeRange = currentState.selectedTimeRange,
                isArchive = currentState.isArchive
            )

            result.onSuccess { matches ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        matches = matches,
                        searchPerformed = true,
                        error = if (matches.isEmpty()) "Нет матчей для выбранной лиги" else null
                    )
                }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = error.message ?: "Ошибка поиска",
                        searchPerformed = true
                    )
                }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}

