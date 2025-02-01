package pt.ipt.dam.a25269a24639.keeply.data.infrastructure.Note

import android.content.Context.MODE_PRIVATE
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import pt.ipt.dam.a25269a24639.keeply.api.NoteApi
import pt.ipt.dam.a25269a24639.keeply.data.domain.Note
import pt.ipt.dam.a25269a24639.keeply.data.dto.NoteDTO
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NoteRepository(private val noteDao: NoteDao) {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://keeplybackend-production.up.railway.app/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(NoteApi::class.java)

    suspend fun update(note: Note) {
        try {
            // primeiro atualizar nota no servidor
            val noteDTO = NoteDTO(note.title, note.content, note.userId, note.photoUri, note.photoBase64)
            api.updateNote(note.id, noteDTO)
            //
            noteDao.updateNote(note.copy(synced = true))
        } catch (e: Exception) {
            // se falhar, atualizar localmente como não sincronizada
            noteDao.updateNote(note.copy(synced = false))
            Log.e("NoteRepository", "Error updating note on server", e)
        }
    }

    suspend fun delete(note: Note) {
        try {
            // Primeiro, apagar nota no servidor
            api.deleteNote(note.id)
            // Depois apagar localmente
            noteDao.deleteNote(note)
        } catch (e: Exception) {
            noteDao.deleteNote(note)
            Log.e("NoteRepository", "Error deleting note from server", e)
        }
    }

    suspend fun getNoteById(id: Long): Note? {
        return noteDao.getNoteById(id)
    }

   suspend fun getAllNotes(userId: Long): Flow<List<Note>> {
        return noteDao.getAllNotes(userId)
    }

    suspend fun deleteAllUserNotes(userId: Long) {
        noteDao.deleteAllUserNotes(userId)
        try {
            api.deleteAllUserNotes(userId)
        } catch (e: Exception) {
            Log.e("NoteRepository", "Error deleting all user notes from server", e)
        }
    }

    suspend fun insert(note: Note) {
        val noteDTO = NoteDTO(note.title, note.content, note.userId, note.photoUri, note.photoBase64)
        try {
            // Primeiro, criar nota no servidor
            val syncedNote = api.createNote(noteDTO)
            // Depois, guardar localmente como sincronizada
            noteDao.insertNote(syncedNote.copy(synced = true))
        } catch (e: Exception) {
            // Se falhar, guardar localmente como não sincronizada
            noteDao.insertNote(note.copy(synced = false))
            Log.e("NoteRepository", "Error creating note on server", e)
        }
    }
    
    suspend fun syncNotes(userId: Long) {
        try {
            //TODO: checkar se há internet, se não, não deixar dar sync - mostrar erro

            // Vai buscar todas as notas do servidor
            val remoteNotes = api.getAllNotes(userId)

            // Vai buscar todas as notas da base de dados local do user logado
            // Obter TODAS as notas, não apenas as não sincronizadas
            // Isto é porque precisamos de comparar timestamps para determinar qual versão é mais recente
            val localNotes = noteDao.getAllNotes(userId).first()
            
            // criar uma lista de notas que são a junção das notas locais e remotas
            val mergedNotes = (remoteNotes + localNotes)
                .groupBy { it.id } // agrupar por ID para remover duplicados
                .map { (_, notes) ->
                    // para cada ID, escolher a nota mais recente
                    notes.maxBy { it.timestamp }
                }
    
            // atualizar a base de dados local com os resultados da junção anterior
            mergedNotes.forEach { note ->
                try {
                    val localNote = noteDao.getNoteById(note.id)
                    val remoteNote = remoteNotes.find { it.id == note.id }

                    if (localNote == null && remoteNote != null) {
                        // inserir nota no local
                        noteDao.insertNote(note.copy(synced = true))
                    }else if (remoteNote == null && localNote != null) {
                        // inserir nota no remote
                        val noteDTO = NoteDTO(localNote.title, localNote.content, localNote.userId,
                            localNote.photoUri)
                        api.createNote(noteDTO)
                    } else if (localNote != null && localNote.timestamp > note.timestamp) {
                        // update nota remota se a local for mais recente
                        val noteDTO = NoteDTO(localNote.title, localNote.content, localNote.userId,
                            localNote.photoUri)
                        api.updateNote(note.id, noteDTO)
                        noteDao.updateNote(localNote.copy(synced = true))
                    } else {
                        // update nota local se a remota for mais recente
                        noteDao.updateNote(note.copy(synced = true))
                    }
                } catch (e: Exception) {
                    Log.e("NoteRepository", "Error syncing note", e)
                }
            }
            Log.d("NoteRepository", "Synced notes")
        } catch (e: Exception) {
            Log.e("NoteRepository", "Error during sync", e)
        }
    }
}
