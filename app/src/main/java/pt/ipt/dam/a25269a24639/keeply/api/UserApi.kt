package pt.ipt.dam.a25269a24639.keeply.api

import pt.ipt.dam.a25269a24639.keeply.data.domain.User
import pt.ipt.dam.a25269a24639.keeply.data.dto.UserDTO
import retrofit2.http.*

interface UserApi {
    @GET("api/users")
    suspend fun getAllUsers(): List<User>
    
    @POST("api/users")
    suspend fun createUser(@Body user: UserDTO): User
    
    @PUT("api/users/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body user: UserDTO): User
    
    @DELETE("api/users/{id}")
    suspend fun deleteUser(@Path("id") id: Int)

    @POST("api/users/login")
    suspend fun login(@Body credentials: Map<String, String>): User
}