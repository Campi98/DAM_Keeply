package pt.ipt.dam.a25269a24639.keeply.data.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Long = 0L,
    val name: String,
    val email: String,
    val password: String,
    val type: String,
    val synced: Boolean = false
)
