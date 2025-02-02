package pt.ipt.dam.a25269a24639.keeply.data.infrastructure.Note

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import pt.ipt.dam.a25269a24639.keeply.data.domain.Note

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE userId = :userId AND isDeleted = 0 ORDER BY timestamp DESC")
    fun getAllNotes(userId: Long): Flow<List<Note>>

    @Query("SELECT * FROM notes")
    suspend fun getAllNotesList(): List<Note>

    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: Long): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT * FROM notes WHERE synced = 0")
    fun getUnsyncedNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE synced = 0")
    suspend fun getUnsyncedNotesList(): List<Note>

    @Query("DELETE FROM notes WHERE userId = :userId")
    suspend fun deleteAllUserNotes(userId: Long)
}