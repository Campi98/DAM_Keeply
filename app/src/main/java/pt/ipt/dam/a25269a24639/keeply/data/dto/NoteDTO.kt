package pt.ipt.dam.a25269a24639.keeply.data.dto


/**
 * Classe de transferência de dados (DTO) para notas.
 *
 * Esta classe é utilizada para:
 * - Transferir dados entre a aplicação e a API
 * - Simplificar a serialização/deserialização JSON
 * - Separar a camada de domínio da camada de transferência
 *
 * @property title Título da nota
 * @property content Conteúdo/texto da nota
 * @property userId Identificador do utilizador proprietário da nota
 * @property photoUri URI da foto anexada (opcional)
 * @property photoBase64 Imagem codificada em base64 (opcional)
 * @property isDeleted Indica se a nota foi marcada como eliminada
 */
data class NoteDTO(
    val title: String,
    val content: String,
    val userId: Int,
    val photoUri: String? = null,
    val photoBase64: String? = null,
    val isDeleted: Boolean = false
)