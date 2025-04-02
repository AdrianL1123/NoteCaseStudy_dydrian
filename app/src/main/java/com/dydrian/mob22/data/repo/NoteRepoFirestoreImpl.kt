package com.dydrian.mob22.data.repo

import com.dydrian.mob22.data.model.Note
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class NoteRepoFirestoreImpl(
    private val db: FirebaseFirestore = Firebase.firestore
) : NoteRepo {
    private fun getCollectionRef(): CollectionReference {
        return db.collection("notes")
    }

    override fun getNotes(): Flow<List<Note>> = callbackFlow {
        val listener = getCollectionRef().addSnapshotListener { value, error ->
            if (error != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            val notes = mutableListOf<Note>()
            value?.documents?.forEach { doc ->
                doc.toObject(Note::class.java)?.let { note ->
                    notes.add(note.copy(id = doc.id))
                }
            }
            trySend(notes)
        }
        awaitClose { listener.remove() }
    }

    override suspend fun addNote(note: Note) {
        val docRef = getCollectionRef().document()
        docRef.set(note.copy(id = docRef.id)).await()
    }

}