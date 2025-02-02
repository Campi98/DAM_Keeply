package pt.ipt.dam.a25269a24639.keeply.data.infrastructure.Note

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import pt.ipt.dam.a25269a24639.keeply.data.domain.Note
import pt.ipt.dam.a25269a24639.keeply.data.domain.User
import pt.ipt.dam.a25269a24639.keeply.data.infrastructure.User.UserDao

@Database(entities = [User::class , Note::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        fun getDatabase(context: Context): NoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}