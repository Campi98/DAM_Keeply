package pt.ipt.dam.a25269a24639.keeply.api

import pt.ipt.dam.a25269a24639.keeply.data.domain.Note
import pt.ipt.dam.a25269a24639.keeply.data.dto.NoteDTO
import retrofit2.http.*

interface NoteApi {

    @GET("api/notes")
    suspend fun getAllNotes(@Query("userId") userId: Long): List<Note>
    
    @POST("api/notes")
    suspend fun createNote(@Body note: NoteDTO): Note
    
    @PUT("api/notes/{id}")
    suspend fun updateNote(@Path("id") id: Long, @Body note: NoteDTO): Note
    
    @DELETE("api/notes/{id}")
    suspend fun deleteNote(@Path("id") id: Long)

    @DELETE("api/notes")
    suspend fun deleteAllUserNotes(@Query("userId") userId: Long)

    @GET("api/notes/deleted")
    suspend fun getDeletedNotes(@Query("userId") userId: Long): List<Long>

    @POST("api/notes/deleted")
    suspend fun markAsDeleted(@Query("noteId") noteId: Long)
}