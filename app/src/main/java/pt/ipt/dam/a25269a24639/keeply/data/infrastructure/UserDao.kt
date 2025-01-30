package pt.ipt.dam.a25269a24639.keeply.data.infrastructure

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import pt.ipt.dam.a25269a24639.keeply.data.domain.User

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>> // Retorna Flow para ser observado

    @Query("SELECT * FROM users WHERE userId IN (:userIds)")
    suspend fun loadAllUsersByIds(userIds: IntArray): List<User>

    @Query("SELECT * FROM users WHERE userId = :idUser")
    suspend fun getUserById(idUser: Long): User?

    @Query("SELECT * FROM users WHERE email LIKE :email LIMIT 1")
    suspend fun findUserByEmail(email: String): User? // Tornar a operação segura ao retornar null caso não encontre

    @Query("SELECT * FROM users WHERE name LIKE :name LIMIT 1")
    suspend fun findUserByName(name: String): User? // Tornar a operação segura ao retornar null caso não encontre


    @Insert(onConflict = OnConflictStrategy.REPLACE) // Evitar duplicados
    suspend fun insertUser(vararg users: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun loginUser(email: String, password: String): User?

}
