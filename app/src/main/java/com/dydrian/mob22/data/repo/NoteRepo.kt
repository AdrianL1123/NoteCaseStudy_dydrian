package com.dydrian.mob22.data.repo

import com.dydrian.mob22.data.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepo {
    fun getNotes(): Flow<List<Note>>
    suspend fun getNoteById(id: String): Note?
    suspend fun addNote(note: Note)
    suspend fun deleteNote(id: String)
    suspend fun updateNote(note: Note)
}