package pt.ipt.dam.a25269a24639.keeply.api

import pt.ipt.dam.a25269a24639.keeply.data.domain.User
import pt.ipt.dam.a25269a24639.keeply.data.dto.UserDTO
import retrofit2.http.*

class LoginRequest(val email: String, val password: String)

interface UserApi {
    @POST("api/users/login")
    suspend fun login(@Body credentials: LoginRequest): User

    @POST("api/users/register")
    suspend fun register(@Body user: UserDTO): User

    @GET("api/users")
    suspend fun getAllUsers(): List<User>


}