package pt.ipt.dam.a25269a24639.keeply

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import pt.ipt.dam.a25269a24639.keeply.data.Note
import pt.ipt.dam.a25269a24639.keeply.data.NoteDatabase
import pt.ipt.dam.a25269a24639.keeply.data.NoteRepository

class NoteDetailActivity : AppCompatActivity() {
    private lateinit var noteRepository: NoteRepository
    private var noteId: Long = -1

    // Obter uma imagem da galeria
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            currentPhotoUri = it.toString()
            findViewById<ImageView>(R.id.noteImage).apply {
                visibility = View.VISIBLE
                setImageURI(uri)
            }
        }
    }

    private var currentPhotoUri: String? = null
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // receber o URI da foto tirada
            val photoUri = result.data?.getStringExtra("photo_uri")
            if (photoUri != null) {
                currentPhotoUri = photoUri
                // mostrar a foto na ImageView
                findViewById<ImageView>(R.id.noteImage).apply {
                    visibility = View.VISIBLE
                    setImageURI(Uri.parse(photoUri))
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_detail)

        val database = NoteDatabase.getDatabase(this)
        noteRepository = NoteRepository(database.noteDao())

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val titleInput = findViewById<TextInputEditText>(R.id.titleInput)
        val contentInput = findViewById<TextInputEditText>(R.id.contentInput)

        // Se estivermos a editar uma nota existente, obtemos os dados da nota
        noteId = intent.getLongExtra("note_id", -1)
        if (noteId != -1L) {
            lifecycleScope.launch {
                noteRepository.getNoteById(noteId)?.let { note ->
                    titleInput.setText(note.title)
                    contentInput.setText(note.content)
                    // carregar a foto da nota se existir
                    note.photoUri?.let { uri ->
                        currentPhotoUri = uri
                        findViewById<ImageView>(R.id.noteImage).apply {
                            visibility = View.VISIBLE
                            setImageURI(Uri.parse(uri))
                        }
                    }
                }
            }
        }
        val noteTitle = intent.getStringExtra("note_title") ?: ""
        val noteContent = intent.getStringExtra("note_content") ?: ""

        titleInput.setText(noteTitle)
        contentInput.setText(noteContent)

        // TODO: Isto não está a ser mostrado, fix
        toolbar.title = if (noteId == -1L) "Nova Nota" else "Editar Nota"

        findViewById<FloatingActionButton>(R.id.saveFab).setOnClickListener {
            val title = titleInput.text.toString()
            val content = contentInput.text.toString()

            if (title.isNotEmpty() && content.isNotEmpty()) {
                lifecycleScope.launch {
                    val note = Note(
                        id = noteId,
                        title = title,
                        content = content,
                        photoUri = currentPhotoUri,
                        synced = false
                    )
                    if (noteId == -1L) {
                        noteRepository.insert(note)
                    } else {
                        noteRepository.update(note)
                    }
                    Toast.makeText(this@NoteDetailActivity, "Nota salva!", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<FloatingActionButton>(R.id.deleteFab).setOnClickListener {
            if (noteId != -1L) {
                lifecycleScope.launch {
                    noteRepository.getNoteById(noteId)?.let { note ->
                        noteRepository.delete(note)
                        Toast.makeText(this@NoteDetailActivity, "Nota apagada!", Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    }
                }
            } else {
                finish()
            }
        }

        toolbar.setNavigationOnClickListener {
            finish()
        }

        // tirar foto
        findViewById<FloatingActionButton>(R.id.cameraFab).setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Adicionar foto")
                .setItems(arrayOf("Tirar foto", "Escolher da galeria")) { _, which ->
                    when (which) {
                        0 -> {
                            // isto lança a atividade da câmara
                            val intent = Intent(this, CameraActivity::class.java)
                            cameraLauncher.launch(intent)
                        }
                        1 -> {
                            // isto lança a atividade da galeria
                            pickImage.launch("image/*")
                        }
                    }
                }
                .show()
        }
    }
}