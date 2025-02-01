package pt.ipt.dam.a25269a24639.keeply.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import pt.ipt.dam.a25269a24639.keeply.R
import pt.ipt.dam.a25269a24639.keeply.api.LogoutRequest
import pt.ipt.dam.a25269a24639.keeply.api.UserApi
import pt.ipt.dam.a25269a24639.keeply.data.NoteAdapter
import pt.ipt.dam.a25269a24639.keeply.data.infrastructure.Note.NoteDatabase
import pt.ipt.dam.a25269a24639.keeply.data.infrastructure.Note.NoteRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var noteRepository: NoteRepository
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://keeplybackend-production.up.railway.app/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(UserApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val database = NoteDatabase.getDatabase(this)
        noteRepository = NoteRepository(database.noteDao())

        val recyclerView = findViewById<RecyclerView>(R.id.notesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val noteAdapter = NoteAdapter(emptyList())
        recyclerView.adapter = noteAdapter

        // TODO: Ver se é necessário usar este lifecycleScope ou se há outra forma
        lifecycleScope.launch {
            val userIdFromAppPrefs = getSharedPreferences("AppPrefs", MODE_PRIVATE).getInt("userId", 0).toLong()
            if (userIdFromAppPrefs == 0L) {
                Toast.makeText(this@MainActivity, "User not found", Toast.LENGTH_SHORT).show()
                return@launch
            }
            noteRepository.syncNotes(userIdFromAppPrefs)
            noteRepository.getAllNotes(userIdFromAppPrefs).collect { notes ->
                noteAdapter.updateNotes(notes)
            }
        }

        findViewById<FloatingActionButton>(R.id.addNoteFab).setOnClickListener {
            startActivity(Intent(this, NoteDetailActivity::class.java))
        }

        val logoutBtn = findViewById<ImageButton>(R.id.logoutButton)
        logoutBtn.setOnClickListener {
            lifecycleScope.launch {
                val emailFromPrefs = getSharedPreferences("AppPrefs", MODE_PRIVATE).getString("email", "")!!
                val response = api.logout(LogoutRequest(emailFromPrefs))

                if (response.isSuccessful == false) {
                    Toast.makeText(this@MainActivity, "Logout failed", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val loggedOutUser = response.body()!!

                // limpar o estado de login no SharedPreferences
                getSharedPreferences("AppPrefs", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isLoggedIn", loggedOutUser.loggedIn)
                    .remove("email")
                    .remove("userId")
                    .apply()

                // voltar ao ecrã de login
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                // limpar a stack de activities para não ser possível voltar atrás
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }

        // este botão serve para sincronizar as notas com o servidor
        val syncButton = findViewById<ImageButton>(R.id.syncButton)
        syncButton.setOnClickListener {
            lifecycleScope.launch {
                try {
                    noteRepository.syncNotes(getSharedPreferences("AppPrefs", MODE_PRIVATE).getInt("userId", 0).toLong())
                    Toast.makeText(this@MainActivity, "Notes synced!", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Sync failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}