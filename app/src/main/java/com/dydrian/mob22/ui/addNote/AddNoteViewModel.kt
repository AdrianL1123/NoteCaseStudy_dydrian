package com.dydrian.mob22.ui.addNote

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
class AddNoteViewModel @Inject constructor(
    private val repo: NoteRepo
): ViewModel() {

    private val _state = MutableStateFlow<AddNoteState>(AddNoteState.Idle)
    val state = _state.asStateFlow()
    val selectedColor = MutableStateFlow(-1)



    fun handleIntent(intent: AddNoteIntent) {
        when (intent) {
            // when user "intents" to add new note
            is AddNoteIntent.Add -> addNote(
                intent.title,
                intent.description,
                intent.color
            )
        }
    }


    private fun addNote(title: String, description: String, color: Int) {
        viewModelScope.launch {
            try {
                require(title.isNotEmpty()) { R.string.empty_title }
                require(description.isNotEmpty()) { R.string.empty_desc }

                val note = Note(title = title, desc = description, color = color)
                // add note to the database
                repo.addNote(note)

                _state.value = AddNoteState.Success(R.string.success.toString())
            } catch (e: Exception) {
                _state.value = AddNoteState.Error(e.message ?: R.string.error.toString())
            }
        }
    }

    fun setSelectedColor(color: Int) {
        selectedColor.value = color
    }
}

sealed class AddNoteIntent {
    data class Add(val title: String, val description: String, val color: Int) : AddNoteIntent()
}

sealed class AddNoteState {
    object Idle : AddNoteState()
    data class Success(val msg: String) : AddNoteState()
    data class Error(val msg: String) : AddNoteState()
}
