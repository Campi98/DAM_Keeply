package pt.ipt.dam.a25269a24639.keeply.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val photoUri: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val synced: Boolean = false
)