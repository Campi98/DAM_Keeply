package pt.ipt.dam.a25269a24639.keeply.data.infrastructure.User

import kotlinx.coroutines.flow.Flow
import pt.ipt.dam.a25269a24639.keeply.data.domain.User

/**
 * Repositório para gestão de utilizadores.
 *
 * Esta classe é responsável por:
 * - Gerir operações CRUD de utilizadores
 * - Fornecer métodos de autenticação
 * - Gerir o acesso à base de dados local
 * - Implementar lógica de negócio relacionada com utilizadores
 */
class UserRepository(private val userDao: UserDao) {

    /**
     * Observa todos os utilizadores na base de dados.
     * Utiliza Flow para atualizações reativas.
     */
    val allUsers: Flow<List<User>> = userDao.getAllUsers()

    /**
     * Insere um novo utilizador na base de dados.
     * @param user Utilizador a inserir
     */
    suspend fun insert(user: User) {
        userDao.insertUser(user)
    }

    /**
     * Atualiza os dados de um utilizador existente.
     * @param user Utilizador com dados atualizados
     */
    suspend fun update(user: User) {
        userDao.updateUser(user)
    }

    /**
     * Remove um utilizador da base de dados.
     * @param user Utilizador a eliminar
     */
    suspend fun delete(user: User) {
        userDao.deleteUser(user)
    }

    /**
     * Obtém um utilizador pelo seu ID.
     * @param userId ID do utilizador a procurar
     * @return Utilizador encontrado ou null se não existir
     */
    suspend fun getUserById(userId: Long): User? {
        return userDao.getUserById(userId)
    }

    /**
     * Procura um utilizador pelo seu email.
     * @param email Email do utilizador a procurar
     * @return Utilizador encontrado ou null se não existir
     */
    suspend fun findUserByEmail(email: String): User? {
        return userDao.findUserByEmail(email)
    }

    /**
     * Procura um utilizador pelo seu nome.
     * @param name Nome do utilizador a procurar
     * @return Utilizador encontrado ou null se não existir
     */
    suspend fun findUserByName(name: String): User? {
        return userDao.findUserByName(name)
    }

    /**
     * Autentica um utilizador com email e password.
     * @param email Email do utilizador
     * @param password Password do utilizador
     * @return Utilizador autenticado ou null se as credenciais forem inválidas
     */
    suspend fun login(email: String, password: String): User? {
        return userDao.loginUser(email, password)
    }

}
