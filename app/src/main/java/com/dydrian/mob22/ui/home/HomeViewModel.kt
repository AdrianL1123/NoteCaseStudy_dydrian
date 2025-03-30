package com.dydrian.mob22.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dydrian.mob22.data.model.Note
import com.dydrian.mob22.data.repo.NoteRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: NoteRepo
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        getNotes()
    }

    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.GetNotes -> getNotes()
        }
    }

    private fun getNotes() {
        viewModelScope.launch {
            repo.getNotes().collect { items ->
                _state.update { it.copy(notes = items) }
            }
        }
    }
}

sealed class HomeIntent {
    object GetNotes : HomeIntent()
}

data class HomeState(
    val notes: List<Note> = emptyList()
)



