package pt.ipt.dam.a25269a24639.keeply.data.infrastructure.User

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import pt.ipt.dam.a25269a24639.keeply.data.domain.User

/**
 * Interface DAO (Data Access Object) para operações com utilizadores na base de dados.
 *
 * Esta interface utiliza:
 * - Room para persistência local
 * - Coroutines Flow para observação reativa de dados
 * - Operações suspensas para execução assíncrona
 */
@Dao
interface UserDao {
    /**
     * Obtém todos os utilizadores da base de dados.
     * @return Flow com lista de utilizadores para observação contínua
     */
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>> // Retorna Flow para ser observado

/**
     * Carrega múltiplos utilizadores pelos seus IDs.
     * @param userIds Array com os IDs dos utilizadores a carregar
     * @return Lista dos utilizadores encontrados
     */
    @Query("SELECT * FROM users WHERE userId IN (:userIds)")
    suspend fun loadAllUsersByIds(userIds: IntArray): List<User>

/**
     * Obtém um utilizador específico pelo seu ID.
     * @param idUser ID do utilizador a procurar
     * @return Utilizador encontrado ou null se não existir
     */
    @Query("SELECT * FROM users WHERE userId = :idUser")
    suspend fun getUserById(idUser: Long): User?

/**
     * Procura um utilizador pelo seu email.
     * @param email Email do utilizador a procurar
     * @return Utilizador encontrado ou null se não existir
     */
    @Query("SELECT * FROM users WHERE email LIKE :email LIMIT 1")
    suspend fun findUserByEmail(email: String): User? // Tornar a operação segura ao retornar null caso não encontre

/**
     * Procura um utilizador pelo seu nome.
     * @param name Nome do utilizador a procurar
     * @return Utilizador encontrado ou null se não existir
     */
    @Query("SELECT * FROM users WHERE name LIKE :name LIMIT 1")
    suspend fun findUserByName(name: String): User? // Tornar a operação segura ao retornar null caso não encontre


/**
     * Insere um ou mais utilizadores na base de dados.
     * @param users Utilizadores a inserir
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Evitar duplicados
    suspend fun insertUser(vararg users: User)

/**
     * Remove um utilizador da base de dados.
     * @param user Utilizador a eliminar
     */
    @Delete
    suspend fun deleteUser(user: User)

/**
     * Atualiza os dados de um utilizador.
     * @param user Utilizador com dados atualizados
     */
    @Update
    suspend fun updateUser(user: User)

/**
     * Autentica um utilizador com email e password.
     * @param email Email do utilizador
     * @param password Password do utilizador
     * @return Utilizador autenticado ou null se as credenciais forem inválidas
     */
    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun loginUser(email: String, password: String): User?

}
