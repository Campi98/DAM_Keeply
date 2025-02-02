package pt.ipt.dam.a25269a24639.keeply.api

import pt.ipt.dam.a25269a24639.keeply.data.domain.Note
import pt.ipt.dam.a25269a24639.keeply.data.dto.NoteDTO
import retrofit2.http.*

/**
 * Interface da API para gestão de notas.
 *
 * Esta interface define todos os endpoints necessários para:
 * - Obter notas do servidor
 * - Criar novas notas
 * - Atualizar notas existentes
 * - Eliminar notas
 * - Gerir notas eliminadas
 */
interface NoteApi {

    /**
     * Obtém todas as notas de um utilizador específico.
     * @param userId ID do utilizador
     * @return Lista de notas do utilizador
     */
    @GET("api/notes")
    suspend fun getAllNotes(@Query("userId") userId: Long): List<Note>

    /**
     * Cria uma nova nota no servidor.
     * @param note DTO com os dados da nota a criar
     * @return Nota criada com ID atribuído pelo servidor
     */
    @POST("api/notes")
    suspend fun createNote(@Body note: NoteDTO): Note

    /**
     * Atualiza uma nota existente.
     * @param id ID da nota a atualizar
     * @param note DTO com os novos dados da nota
     * @return Nota atualizada
     */
    @PUT("api/notes/{id}")
    suspend fun updateNote(@Path("id") id: Long, @Body note: NoteDTO): Note

    /**
     * Elimina uma nota específica.
     * @param id ID da nota a eliminar
     */
    @DELETE("api/notes/{id}")
    suspend fun deleteNote(@Path("id") id: Long)

    /**
     * Elimina todas as notas de um utilizador.
     * @param userId ID do utilizador
     */
    @DELETE("api/notes")
    suspend fun deleteAllUserNotes(@Query("userId") userId: Long)

    /**
     * Obtém os IDs das notas eliminadas de um utilizador.
     * @param userId ID do utilizador
     * @return Lista de IDs das notas eliminadas
     */
    @GET("api/notes/deleted")
    suspend fun getDeletedNotes(@Query("userId") userId: Long): List<Long>

    /**
     * Marca uma nota como eliminada no servidor.
     * @param noteId ID da nota a marcar como eliminada
     */
    @POST("api/notes/deleted")
    suspend fun markAsDeleted(@Query("noteId") noteId: Long)
}