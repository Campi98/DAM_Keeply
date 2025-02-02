package pt.ipt.dam.a25269a24639.keeply.data.infrastructure.Note

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import pt.ipt.dam.a25269a24639.keeply.data.domain.Note

/**
 * Interface DAO (Data Access Object) para operações com notas na base de dados.
 *
 * Esta interface utiliza:
 * - Room para persistência local
 * - Coroutines Flow para observação reativa de dados
 * - Operações suspensas para execução assíncrona
 */
@Dao
interface NoteDao {
    /**
     * Obtém todas as notas ativas de um utilizador específico.
     * @param userId ID do utilizador
     * @return Flow com lista de notas ordenadas por data decrescente
     */
    @Query("SELECT * FROM notes WHERE userId = :userId AND isDeleted = 0 ORDER BY timestamp DESC")
    fun getAllNotes(userId: Long): Flow<List<Note>>

    /**
     * Obtém lista completa de todas as notas na base de dados.
     * @return Lista de todas as notas
     */
    @Query("SELECT * FROM notes")
    suspend fun getAllNotesList(): List<Note>

    /**
     * Obtém uma nota específica pelo seu ID.
     * @param noteId ID da nota
     * @return Nota correspondente ou null se não existir
     */
    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: Long): Note?

    /**
     * Insere uma nova nota na base de dados.
     * @param note Nota a inserir
     * @return ID da nota inserida
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    /**
     * Atualiza uma nota existente.
     * @param note Nota com dados atualizados
     */
    @Update
    suspend fun updateNote(note: Note)

    /**
     * Remove uma nota da base de dados.
     * @param note Nota a eliminar
     */
    @Delete
    suspend fun deleteNote(note: Note)

    /**
     * Obtém todas as notas não sincronizadas com o servidor.
     * @return Flow com lista de notas não sincronizadas
     */
    @Query("SELECT * FROM notes WHERE synced = 0")
    fun getUnsyncedNotes(): Flow<List<Note>>

    /**
     * Obtém lista de notas não sincronizadas.
     * @return Lista de notas não sincronizadas
     */
    @Query("SELECT * FROM notes WHERE synced = 0")
    suspend fun getUnsyncedNotesList(): List<Note>

    /**
     * Elimina todas as notas de um utilizador específico.
     * @param userId ID do utilizador
     */
    @Query("DELETE FROM notes WHERE userId = :userId")
    suspend fun deleteAllUserNotes(userId: Long)
}