package com.dydrian.mob22.ui.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dydrian.mob22.core.service.AuthService
import com.dydrian.mob22.data.model.Note
import com.dydrian.mob22.data.repo.NoteRepo
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: NoteRepo,
    private val authService: AuthService
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _query = MutableStateFlow("")

    init {
        handleIntent(HomeIntent.GetNotes)
    }

    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.GetNotes -> getNotes()
        }
    }

    fun getProfileUrl(): Uri? {
        return authService.getLoggedInUser()?.photoUrl
    }

    fun getUserInfo(): FirebaseUser? {
        return authService.getLoggedInUser()
    }

    fun logout() {
        return authService.logout()
    }

    val filteredNotes = combine(_state, _query) { state, query ->
        if (query.isBlank()) {
            state.notes
        }
        state.notes.filter { it.title.contains(query, ignoreCase = true) }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun setQuery(query: String) {
        _query.value = query
    }

    private fun getNotes() {
        viewModelScope.launch {
            repo.getNotes().collect { items ->
                _state.update { it.copy(notes = items) }
            }
        }
    }

    fun deleteNote(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteNote(id)
            getNotes()
        }
    }

}

sealed class HomeIntent {
    object GetNotes : HomeIntent()
}

data class HomeState(
    val notes: List<Note> = emptyList(),
    val query: String = ""
)


