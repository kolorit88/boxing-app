package com.example.feature.match

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.repository.MatchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MatchViewModel(
    savedStateHandle: SavedStateHandle,
    private val matchRepository: MatchRepository
) : ViewModel() {

    private val matchId: String = savedStateHandle.get<String>("matchId") ?: ""

    private val _state = MutableStateFlow<MatchDetailState>(MatchDetailState.Loading)
    val state: StateFlow<MatchDetailState> = _state.asStateFlow()

    init {
        loadMatch(matchId)
    }

    fun loadMatch(matchId: String) {
        viewModelScope.launch {
            _state.value = MatchDetailState.Loading
            matchRepository.getMatchById(matchId)
                .onSuccess { match ->
                    _state.value = MatchDetailState.Success(match)
                }
                .onFailure { error ->
                    _state.value = MatchDetailState.Error(
                        error.message ?: "Не удалось загрузить матч"
                    )
                }
        }
    }
}