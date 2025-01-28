package pt.ipt.dam.a25269a24639.keeply.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,
    val email: String,
    val password: String,
    val tipo: String
)
