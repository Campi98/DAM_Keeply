package pt.ipt.dam.a25269a24639.keeply.api

import pt.ipt.dam.a25269a24639.keeply.data.domain.User
import retrofit2.Response
import retrofit2.http.*

data class LoginRequest(
    val email: String,
    val password: String
)

data class LogoutRequest(
    val email: String
)

interface UserApi {
    @GET("api/users/{id}")
    suspend fun getUser(@Path("id") id: Int): User

    @POST("api/users")
    suspend fun createUser(@Body user: User): Response<User>
    
    @POST("api/users/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<User>

    @POST("api/users/logout")
    suspend fun logout(@Body logoutRequest: LogoutRequest): Response<User>

    //delete user
    @DELETE("api/users/{id}")
    suspend fun deleteUser(@Path("id") id: Int)
}