package pt.ipt.dam.a25269a24639.keeply.data

data class Note(
    val id: Long = 0,
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)