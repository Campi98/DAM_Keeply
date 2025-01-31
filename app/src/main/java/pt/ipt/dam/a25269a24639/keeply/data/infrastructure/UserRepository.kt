package pt.ipt.dam.a25269a24639.keeply.data.infrastructure
import android.util.Log
import pt.ipt.dam.a25269a24639.keeply.api.UserApi
import pt.ipt.dam.a25269a24639.keeply.data.domain.User
import pt.ipt.dam.a25269a24639.keeply.data.dto.UserDTO
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserRepository(private val userDao: UserDao) {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://keeplybackend-production.up.railway.app/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(UserApi::class.java)

    suspend fun register(name: String, email: String, password: String): User {
        val userDTO = UserDTO(name, email, password)
        return api.createUser(userDTO)
    }

    suspend fun login(email: String, password: String): User? {
        return try {
            val credentials = mapOf(
                "email" to email,
                "password" to password
            )
            api.login(credentials)
        } catch (e: Exception) {
            Log.e("UserRepository", "Login failed", e)
            null
        }
    }
}