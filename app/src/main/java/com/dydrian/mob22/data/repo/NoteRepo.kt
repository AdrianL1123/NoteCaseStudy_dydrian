package com.dydrian.mob22.data.repo

import com.dydrian.mob22.data.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepo {
    fun getNotes(): Flow<List<Note>>
    suspend fun addNote(note: Note)
}