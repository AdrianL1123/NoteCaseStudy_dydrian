package com.dydrian.mob22.ui.manageNote.editNote

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dydrian.mob22.R
import com.dydrian.mob22.data.model.Note
import com.dydrian.mob22.data.repo.NoteRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditNoteViewModel @Inject constructor(
    private val repo: NoteRepo,
    args: SavedStateHandle
) : ViewModel() {
    val noteId = args.get<String>("noteId") ?: "-1"

    private val _state = MutableStateFlow<EditNoteState>(EditNoteState.Idle)
    val state = _state.asStateFlow()

    private val _note = MutableStateFlow<Note?>(null)
    val note = _note.asStateFlow()

    val selectedColor = MutableStateFlow(-1)

    init {
        fetchNote()
    }

    private fun fetchNote() {
        viewModelScope.launch {
            try {
                val existingNote = repo.getNoteById(noteId)

                if (existingNote != null) {
                    _note.value = existingNote
                    selectedColor.value = existingNote.color
                } else {
                    _state.value = EditNoteState.Error(R.string.note_not_found.toString())
                }

            } catch (e: Exception) {
                _state.value = EditNoteState.Error(e.message ?: R.string.error.toString())
            }
        }
    }

    fun handleIntent(intent: EditNoteIntent) {
        when (intent) {
            is EditNoteIntent.Edit -> editNote(
                intent.id,
                intent.title,
                intent.description,
                intent.color
            )
        }
    }

    private fun editNote(noteId: String, title: String, description: String, color: Int) {
        viewModelScope.launch {
            try {
                require(title.isNotEmpty()) { (R.string.empty_title) }
                require(description.isNotEmpty()) { (R.string.empty_desc) }

                val updatedNote =
                    Note(id = noteId, title = title, desc = description, color = color)
                repo.updateNote(updatedNote)

                _state.value = EditNoteState.Success((R.string.success_update.toString()))
                _note.value = updatedNote
            } catch (e: Exception) {
                _state.value = EditNoteState.Error(e.message ?: (R.string.error.toString()))
            }
        }
    }

    fun setSelectedColor(color: Int) {
        selectedColor.value = color
    }
}

sealed class EditNoteIntent {
    data class Edit(val id: String, val title: String, val description: String, val color: Int) :
        EditNoteIntent()
}

sealed class EditNoteState {
    object Idle : EditNoteState()
    data class Success(val msg: String) : EditNoteState()
    data class Error(val msg: String) : EditNoteState()
}
