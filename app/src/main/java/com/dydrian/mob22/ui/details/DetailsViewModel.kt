package com.dydrian.mob22.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dydrian.mob22.data.model.Note
import com.dydrian.mob22.data.repo.NoteRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repo: NoteRepo
): ViewModel() {
    private val _state = MutableStateFlow(DetailState())
    val state = _state.asStateFlow()

    fun handleIntent(intent: DetailIntent) {
        when (intent) {
            is DetailIntent.GetNoteById -> getNoteById(intent.id)
            is DetailIntent.DeleteNote -> deleteNote(intent.id)
        }
    }

    private fun getNoteById(id: String) {
        viewModelScope.launch {
            _state.value = DetailState(isLoading = true)
            val note = repo.getNoteById(id)
            _state.value = DetailState(note = note)
        }
    }

    private fun deleteNote(id: String) {
        viewModelScope.launch {
            repo.deleteNote(id)
            _state.value = DetailState(isDeleted = true)
        }
    }
}

sealed class DetailIntent {
    data class GetNoteById(val id: String) : DetailIntent()
    data class DeleteNote(val id: String) : DetailIntent()
}

data class DetailState(
    val note: Note? = null,
    val isLoading: Boolean = false,
    val isDeleted: Boolean = false
)