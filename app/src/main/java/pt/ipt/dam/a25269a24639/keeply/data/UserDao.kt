package pt.ipt.dam.a25269a24639.keeply.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsers(): List<User>

    @Query("SELECT * FROM users WHERE userId IN (:userIds)")
    fun loadAllUsersByIds(userIds: IntArray): List<User>

    @Query("SELECT * FROM users WHERE userId = :idUser")
    suspend fun getUserById(idUser: Long): User?

    @Query("SELECT * FROM users WHERE email LIKE :email LIMIT 1")
    fun findUserByEmail(email: String): User

    @Insert
    fun insertUser(vararg users: User)

    @Delete
    fun deleteUser(user: User)

    @Update
    suspend fun updateUser(user: User)


}