package pt.ipt.dam.a25269a24639.keeply.data.dto

data class NoteDTO(
    val title: String,
    val content: String,
    val photoUri: String? = null,
    val photoBase64: String? = null
)