package pt.ipt.dam.a25269a24639.keeply

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import pt.ipt.dam.a25269a24639.keeply.data.Note
import pt.ipt.dam.a25269a24639.keeply.data.NoteAdapter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        // Configurar a Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Configurar o RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.notesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Exemplo de notas para teste
        val sampleNotes = listOf(
            Note(title = "Primeira Nota", content = "Conteúdo da primeira nota..."),
            Note(title = "Segunda Nota", content = "Conteúdo da segunda nota...")
        )

        recyclerView.adapter = NoteAdapter(sampleNotes)

        // Configurar o FAB
        val fab = findViewById<FloatingActionButton>(R.id.addNoteFab)
        fab.setOnClickListener {
            // TODO: Implementar adição de nota
            Toast.makeText(this, "Adicionar nota", Toast.LENGTH_SHORT).show()
        }
    }
}