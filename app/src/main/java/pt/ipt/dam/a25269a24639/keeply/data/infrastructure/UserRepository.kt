package pt.ipt.dam.a25269a24639.keeply.data.infrastructure

import android.util.Log
import kotlinx.coroutines.flow.Flow
import pt.ipt.dam.a25269a24639.keeply.api.UserApi
import pt.ipt.dam.a25269a24639.keeply.data.domain.User
import pt.ipt.dam.a25269a24639.keeply.data.dto.UserDTO
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class UserRepository(private val userDao: UserDao) {


    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    private val api = retrofit.create(UserApi::class.java)

    // Observe todos os usuários como Flow
    val allUsers: Flow<List<User>> = userDao.getAllUsers()

    /**
     * Função de login que sincroniza dados e autentica o usuário localmente.
     *
     * @param email Email do usuário.
     * @param password Senha do usuário.
     * @return Objeto User se o login for bem-sucedido, null caso contrário.
     */
    suspend fun login(email: String, password: String): User? {
        return try {
            // 1. Sincronizar dados antes de autenticar
            syncUsers()

            // 2. Autenticar localmente
            val user = userDao.loginUser(email, password)
            if (user != null) {
                Log.d("UserRepository", "Login bem-sucedido para o usuário: $email")
                user
            } else {
                Log.w("UserRepository", "Falha no login: Credenciais inválidas para o email $email")
                null
            }
        } catch (e: Exception) {
            // Erro durante a sincronização ou autenticação
            Log.e("UserRepository", "Erro durante o login para o email $email", e)
            null
        }
    }

    suspend fun register(user: User) {
        val userDTO = UserDTO(user.name, user.email, user.password)
        try {
            // Primeiro, cria o user no servidor
            val syncedUser = api.register(userDTO)
            // Depois, guardar localmente como sincronizada
            userDao.insertUser(syncedUser.copy(synced = true))
        } catch (e: Exception) {
            // Se falhar, guardar localmente como não sincronizada
            userDao.insertUser(user.copy(synced = false))
            Log.e("UserRepository", "Error creating user on server", e)
        }
    }


    suspend fun update(user: User) {
        userDao.updateUser(user)   // 1) atualiza local
        try {
            // 2) atualiza no servidor
            val userDTO = UserDTO(user.name, user.email, user.password)
            api.updateUser(user.userId, userDTO)
            // 3) se deu certo, marca local como synced
            userDao.updateUser(user.copy(synced = true))
        } catch (e: Exception) {
            // se falhar, fica synced = false
            userDao.updateUser(user.copy(synced = false))
            Log.e("UserRepository", "Error updating user on server", e)
        }
    }

    suspend fun delete(user: User) {
        try {
            // 1) Deleta no servidor
            api.deleteUser(user.userId)
            // 2) Deleta local
            userDao.deleteUser(user)
        } catch (e: Exception) {
            // Apaga local mesmo assim
            userDao.deleteUser(user)
            Log.e("UserRepository", "Error deleting user from server", e)
        }
    }

    suspend fun getUserById(userId: Long): User? {
        syncUsers()
        return userDao.getUserById(userId)
    }

    suspend fun findUserByEmail(email: String): User? {
        syncUsers()
        return userDao.findUserByEmail(email)
    }

    suspend fun findUserByName(name: String): User? {
        syncUsers()
        return userDao.findUserByName(name)
    }

    /**
     * Sincroniza todos os users não sincronizados com o servidor.
     * Método "push only": envia apenas os users com synced = false para o servidor.
     */
    suspend fun syncUsers() {
        try {
            // 1. Pegar todos os usuários locais que ainda não foram sincronizados
            val unsyncedUsers = userDao.getUnsyncedUsersList()

            for (localUser in unsyncedUsers) {
                try {
                    if (localUser.userId == 0L) {
                        // 1.a. User novo: tentar registar no servidor
                        val userDTO = UserDTO(
                            name = localUser.name,
                            email = localUser.email,
                            password = localUser.password
                        )
                        val createdUser = api.register(userDTO)

                        // Atualizar local com o userId retornado pelo servidor e marcar como sincronizado
                        userDao.updateUser(
                            localUser.copy(
                                userId = createdUser.userId,
                                synced = true
                            )
                        )
                        Log.d("UserRepository", "User registado: ${createdUser.email}")
                    } else {
                        // 2.a. Usuário existente: tentar atualizar no servidor
                        val userDTO = UserDTO(
                            name = localUser.name,
                            email = localUser.email,
                            password = localUser.password
                        )
                        val updatedUser = api.updateUser(localUser.userId, userDTO)

                        // Marcar como sincronizado
                        userDao.updateUser(updatedUser.copy(synced = true))
                        Log.d("UserRepository", "User atualizado: ${updatedUser.email}")
                    }
                } catch (e: HttpException) {
                    if (e.code() == 409) {

                        // 1.b. Conflito: e-mail já existe no servidor
                        // Marcar como sincronizado para evitar tentativas futuras

                        userDao.updateUser(localUser.copy(synced = true))

                        Log.w("UserRepository", "User com e-mail ${localUser.email} já existe no servidor.")

                     } else {
                        // Outros erros HTTP: manter synced = false para tentar novamente depois
                        Log.e("UserRepository", "Erro ao sincronizar user ${localUser.email}: ${e.message}")
                    }
                } catch (e: IOException) {
                    // Erro de rede: manter synced = false para tentar novamente
                    Log.e("UserRepository", "Erro de rede ao sincronizar user ${localUser.email}", e)
                } catch (e: Exception) {
                    // Outros erros inesperados: manter synced = false para tentar novamente
                    Log.e("UserRepository", "Erro inesperado ao sincronizar user ${localUser.email}", e)
                }
            }

            Log.d("UserRepository", "Sincronização de users concluída.")

        } catch (e: Exception) {
            // Erro geral durante a sincronização
            Log.e("UserRepository", "Erro geral durante sincronização de users", e)
        }
    }

}
