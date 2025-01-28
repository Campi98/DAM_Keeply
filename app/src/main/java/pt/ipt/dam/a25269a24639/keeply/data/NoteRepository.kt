package pt.ipt.dam.a25269a24639.keeply.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import pt.ipt.dam.a25269a24639.keeply.api.NoteApi
import pt.ipt.dam.a25269a24639.keeply.data.dto.NoteDTO
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NoteRepository(private val noteDao: NoteDao) {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.20:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(NoteApi::class.java)

    // Local storage operations
    val allNotes: Flow<List<Note>> = noteDao.getAllNotes()
    
    suspend fun insert(note: Note) {
        var noteDTO = NoteDTO(note.title, note.content, note.photoUri)
        api.createNote(noteDTO)
        noteDao.insertNote(note.copy(synced = false))
        syncNotes()
    }

    suspend fun update(note: Note) {
        noteDao.updateNote(note.copy(synced = false))
        syncNotes()
    }

    suspend fun delete(note: Note) {
        noteDao.deleteNote(note)
    }

    suspend fun getNoteById(id: Long): Note? {
        return noteDao.getNoteById(id)
    }


    // dar fix ao get (sync notes)
    // user api em vez do dao



    // TODO - EDITAR NOTA COMO O QUE FIZ AGORA
    // GARANTIR QUE O NOTEDTO PARA O UPDATE JÁ TEM O ID

    // Sync with backend
    suspend fun syncNotes() {
        try {
            val unsyncedNotes = noteDao.getUnsyncedNotesList()
            unsyncedNotes.forEach { note ->
                try {
                    val syncedNote = if (note.id == 0L) {
                        var noteDTO = NoteDTO(note.title, note.content, note.photoUri)
                        var noteResponse = api.createNote(noteDTO)
                        Log.d("NoteRepository", "Created note: $noteResponse")
                    } else {
                        api.updateNote(note.id, note)
                    }
                    noteDao.updateNote(note.copy(synced = true))
                } catch (e: Exception) {
                    Log.e("NoteRepository", "Error syncing note", e)
                }
            }
        } catch (e: Exception) {
            Log.e("NoteRepository", "Error during sync", e)
        }
    }
}