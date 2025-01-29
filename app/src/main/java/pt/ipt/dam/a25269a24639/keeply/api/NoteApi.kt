package pt.ipt.dam.a25269a24639.keeply.api

import pt.ipt.dam.a25269a24639.keeply.data.Note
import pt.ipt.dam.a25269a24639.keeply.data.dto.NoteDTO
import retrofit2.http.*

interface NoteApi {

    // estas com o NoteDTO estão a funcionar

    @GET("api/notes")
    suspend fun getAllNotes(): List<Note>
    
    @POST("api/notes")
    suspend fun createNote(@Body note: NoteDTO): Note
    
    @PUT("api/notes/{id}")
    suspend fun updateNote(@Path("id") id: Long, @Body note: NoteDTO): Note
    
    @DELETE("api/notes/{id}")
    suspend fun deleteNote(@Path("id") id: Long)
}