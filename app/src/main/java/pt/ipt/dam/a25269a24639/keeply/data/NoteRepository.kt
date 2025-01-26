package pt.ipt.dam.a25269a24639.keeply.data

import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {
    val allNotes: Flow<List<Note>> = noteDao.getAllNotes()
    val unsyncedNotes: Flow<List<Note>> = noteDao.getUnsyncedNotes()    // TODO: Implementar sincronização

    suspend fun insert(note: Note) = noteDao.insertNote(note)

    suspend fun update(note: Note) = noteDao.updateNote(note)

    suspend fun delete(note: Note) = noteDao.deleteNote(note)

    suspend fun getNoteById(id: Long) = noteDao.getNoteById(id)
}