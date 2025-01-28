package pt.ipt.dam.a25269a24639.keeply

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import pt.ipt.dam.a25269a24639.keeply.data.Note
import pt.ipt.dam.a25269a24639.keeply.data.NoteAdapter
import pt.ipt.dam.a25269a24639.keeply.data.NoteDatabase
import pt.ipt.dam.a25269a24639.keeply.data.NoteRepository

class MainActivity : AppCompatActivity() {
        private lateinit var noteRepository: NoteRepository
    
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
                noteRepository.syncNotes()
                noteRepository.allNotes.collect { notes ->
                    noteAdapter.updateNotes(notes)
                }
            }
    
            findViewById<FloatingActionButton>(R.id.addNoteFab).setOnClickListener {
                startActivity(Intent(this, NoteDetailActivity::class.java))
            }

            val logoutBtn = findViewById<ImageButton>(R.id.logoutButton)
            logoutBtn.setOnClickListener {
                // limpar o estado de login no SharedPreferences
                getSharedPreferences("AppPrefs", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isLoggedIn", false)
                    .apply()
                
                // voltar ao ecrã de login
                val intent = Intent(this, LoginActivity::class.java)
                // limpar a stack de activities para não ser possível voltar atrás
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
    }
}