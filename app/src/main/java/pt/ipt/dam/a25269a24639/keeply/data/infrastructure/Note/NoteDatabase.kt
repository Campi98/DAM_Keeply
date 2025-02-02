package pt.ipt.dam.a25269a24639.keeply.data.infrastructure.Note

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import pt.ipt.dam.a25269a24639.keeply.data.domain.Note
import pt.ipt.dam.a25269a24639.keeply.data.domain.User
import pt.ipt.dam.a25269a24639.keeply.data.infrastructure.User.UserDao

/**
 * Base de dados principal da aplicação utilizando Room.
 *
 * Esta classe:
 * - Define a estrutura da base de dados
 * - Fornece acesso aos DAOs
 * - Implementa o padrão Singleton para gestão de instâncias
 *
 * Entidades:
 * - [User]: Informação dos utilizadores
 * - [Note]: Notas dos utilizadores
 *
 * @property noteDao Acesso às operações com notas
 * @property userDao Acesso às operações com utilizadores
 */
@Database(entities = [User::class, Note::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun userDao(): UserDao

    companion object {
        /**
         * Instância única da base de dados (Singleton).
         * Volatile garante visibilidade imediata entre threads.
         */
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        /**
         * Obtém uma instância da base de dados.
         * Se não existir, cria uma nova instância.
         *
         * @param context Contexto da aplicação
         * @return Instância da base de dados
         */
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