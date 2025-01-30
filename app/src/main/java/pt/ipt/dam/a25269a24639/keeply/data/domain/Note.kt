package pt.ipt.dam.a25269a24639.keeply.data.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = -1L,
    // vai ter que ter o userid
    val title: String,
    val content: String,
    val photoUri: String? = null,
    val photoBase64: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val synced: Boolean = false
)