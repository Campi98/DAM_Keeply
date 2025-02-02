package pt.ipt.dam.a25269a24639.keeply.data.domain

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Entidade que representa uma nota na base de dados.
 *
 * Atributos:
 * @property id Identificador único da nota (gerado automaticamente)
 * @property userId ID do utilizador proprietário da nota
 * @property title Título da nota
 * @property content Conteúdo da nota
 * @property photoUri URI da foto anexada (opcional)
 * @property photoBase64 Imagem codificada em base64 (opcional)
 * @property timestamp Momento da última modificação da nota
 * @property synced Estado de sincronização com o servidor
 * @property isDeleted Indica se a nota foi marcada como eliminada
 */
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = -1L,
    val userId: Int,
    val title: String,
    val content: String,
    val photoUri: String? = null,
    val photoBase64: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val synced: Boolean = false,
    val isDeleted: Boolean = false
)