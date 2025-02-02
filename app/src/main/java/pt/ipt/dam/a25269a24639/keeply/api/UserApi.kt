package pt.ipt.dam.a25269a24639.keeply.api

import pt.ipt.dam.a25269a24639.keeply.data.domain.User
import retrofit2.Response
import retrofit2.http.*

/**
 * Classe de dados para pedidos de autenticação.
 * @property email Email do utilizador
 * @property password Palavra-passe do utilizador
 */
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * Classe de dados para pedidos de término de sessão.
 * @property email Email do utilizador a terminar sessão
 */
data class LogoutRequest(
    val email: String
)

/**
 * Interface da API para gestão de utilizadores.
 *
 * Esta interface define todos os endpoints necessários para:
 * - Gerir contas de utilizador (criar, obter, eliminar)
 * - Gerir sessões (login, logout)
 * - Autenticar operações
 */
interface UserApi {
    /**
     * Obtém os dados de um utilizador específico.
     * @param id ID do utilizador a obter
     * @return Dados do utilizador
     */
    @GET("api/users/{id}")
    suspend fun getUser(@Path("id") id: Int): User

    /**
     * Cria um novo utilizador no sistema.
     * @param user Dados do utilizador a criar
     * @return Resposta com os dados do utilizador criado
     */
    @POST("api/users")
    suspend fun createUser(@Body user: User): Response<User>

    /**
     * Autentica um utilizador no sistema.
     * @param loginRequest Dados de autenticação
     * @return Resposta com os dados do utilizador autenticado
     */
    @POST("api/users/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<User>

    /**
     * Termina a sessão de um utilizador.
     * @param logoutRequest Dados do utilizador a terminar sessão
     * @return Resposta com confirmação
     */
    @POST("api/users/logout")
    suspend fun logout(@Body logoutRequest: LogoutRequest): Response<User>

    /**
     * Elimina uma conta de utilizador.
     * @param id ID do utilizador a eliminar
     */
    @DELETE("api/users/{id}")
    suspend fun deleteUser(@Path("id") id: Int)
}