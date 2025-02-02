package pt.ipt.dam.a25269a24639.keeply.data.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade que representa um utilizador na base de dados.
 *
 * Atributos:
 * @property userId Identificador único do utilizador (gerado automaticamente)
 * @property name Nome do utilizador
 * @property email Email do utilizador (usado para autenticação)
 * @property password Palavra-passe do utilizador (deveria ser encriptada)
 * @property loggedIn Estado de autenticação do utilizador
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,
    val name: String,
    val email: String,
    val password: String,
    val loggedIn: Boolean = false
)
