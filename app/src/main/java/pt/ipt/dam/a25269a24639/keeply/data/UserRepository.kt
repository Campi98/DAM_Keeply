package pt.ipt.dam.a25269a24639.keeply.data

import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    // Observe todos os usuários como Flow
    val allUsers: Flow<List<User>> = userDao.getAllUsers()

    suspend fun insert(user: User) {
        userDao.insertUser(user)
    }

    suspend fun update(user: User) {
        userDao.updateUser(user)
    }

    suspend fun delete(user: User) {
        userDao.deleteUser(user)
    }

    suspend fun getUserById(userId: Long): User? {
        return userDao.getUserById(userId)
    }

    suspend fun findUserByEmail(email: String): User? {
        return userDao.findUserByEmail(email)
    }

    suspend fun login(email: String, password: String): User? {
        return userDao.loginUser(email, password)
    }

}
