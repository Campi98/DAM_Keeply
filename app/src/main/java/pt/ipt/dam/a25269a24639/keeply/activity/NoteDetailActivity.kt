package pt.ipt.dam.a25269a24639.keeply.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import pt.ipt.dam.a25269a24639.keeply.R
import pt.ipt.dam.a25269a24639.keeply.data.domain.Note
import pt.ipt.dam.a25269a24639.keeply.data.infrastructure.Note.NoteDatabase
import pt.ipt.dam.a25269a24639.keeply.data.infrastructure.Note.NoteRepository
import pt.ipt.dam.a25269a24639.keeply.util.ImageUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteDetailActivity : AppCompatActivity() {
    private lateinit var noteRepository: NoteRepository
    private var noteId: Long = -1

    private var currentPhotoBase64: String? = null

    private val fullscreenImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // User confirmou que quer apagar a imagem
            currentPhotoUri = null
            currentPhotoBase64 = null
            findViewById<ImageView>(R.id.noteImage).visibility = View.GONE
        }
    }

    private var currentPhotoUri: String? = null
    private val validImageUriPattern = Regex("^file://.+\\.(jpg|jpeg|png|gif|bmp)$", RegexOption.IGNORE_CASE)

    // Obter uma imagem da galeria
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { selectedUri ->
            try {
                // criar nome para a imagem com o timestamp
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val imageFileName = "KEEPLY_${timestamp}.jpg"
                val outputFile = File(filesDir, imageFileName)

                // Copiar a imagem da galeria para o armazenamento privado da app
                contentResolver.openInputStream(selectedUri)?.use { input ->
                    outputFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                    processImage(outputFile)

                    // Usar o mesmo formato de URI para todas as imagens
                    currentPhotoUri = "file://${outputFile.absolutePath}"
                    findViewById<ImageView>(R.id.noteImage).apply {
                        visibility = View.VISIBLE
                        setImageURI(Uri.parse(currentPhotoUri))
                    }
                }
            } catch (e: Exception) {
                Log.e("NoteDetailActivity", "Error processing gallery image", e)
                Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val photoUri = result.data?.getStringExtra("photo_uri")
            if (photoUri != null) {
                try {
                    // converter o URI content:// para file:// para consistência
                    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                    val imageFileName = "KEEPLY_${timestamp}.jpg"
                    val outputFile = File(filesDir, imageFileName)

                    // copiar a imagem da câmara para o armazenamento privado da app
                    contentResolver.openInputStream(Uri.parse(photoUri))?.use { input ->
                        outputFile.outputStream().use { output ->
                            input.copyTo(output)
                        }

                        processImage(outputFile)

                        currentPhotoUri = "file://${outputFile.absolutePath}"
                        findViewById<ImageView>(R.id.noteImage).apply {
                            visibility = View.VISIBLE
                            setImageURI(Uri.parse(currentPhotoUri))
                        }
                    }
                } catch (e: Exception) {
                    Log.e("NoteDetailActivity", "Error processing camera image", e)
                    Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun processImage(file: File) {
        try {
            currentPhotoUri = "file://${file.absolutePath}"
            // Converter a imagem para base64
            val base64Image = ImageUtils.fileToBase64(file)
            currentPhotoBase64 = base64Image

            findViewById<ImageView>(R.id.noteImage).apply {
                visibility = View.VISIBLE
                setImageURI(Uri.parse(currentPhotoUri))
            }
        } catch (e: Exception) {
            Log.e("NoteDetailActivity", "Error processing image", e)
            Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadBase64Image(imageView: ImageView, base64String: String?) {
        if (base64String != null) {
            try {
                val bitmap = ImageUtils.base64ToBitmap(base64String)
                imageView.setImageBitmap(bitmap)
            } catch (e: Exception) {
                Log.e("NoteDetailActivity", "Error decoding base64 image", e)
                imageView.setImageDrawable(null)
            }
        } else {
            imageView.setImageDrawable(null)
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

        findViewById<ImageView>(R.id.noteImage).setOnClickListener {
            if (currentPhotoUri != null || currentPhotoBase64 != null) {
                val intent = Intent(this, FullscreenImageActivity::class.java).apply {
                    putExtra("photo_uri", currentPhotoUri)
                    putExtra("photo_base64", currentPhotoBase64)
                }
                fullscreenImageLauncher.launch(intent)
            }
        }

        // Se estivermos a editar uma nota existente, obtemos os dados da nota
        noteId = intent.getLongExtra("note_id", -1)
        if (noteId != -1L) {
            lifecycleScope.launch {
                noteRepository.getNoteById(noteId)?.let { note ->
                    titleInput.setText(note.title)
                    contentInput.setText(note.content)

                    // obtém a referência para a ImageView
                    val imageView = findViewById<ImageView>(R.id.noteImage)

                    // tenta carregar a imagem a partir do URI primeiro, depois do base64
                    if (note.photoUri != null || note.photoBase64 != null) {
                        imageView.visibility = View.VISIBLE

                        if (note.photoUri != null) {
                            // vê se o ficheiro existe no caminho do URI
                            val file = Uri.parse(note.photoUri).path?.let { File(it) }
                            if (file?.exists() == true) {
                                try {
                                    imageView.setImageURI(Uri.parse(note.photoUri))
                                    currentPhotoUri = note.photoUri
                                } catch (e: Exception) {
                                    Log.e("NoteDetailActivity", "Error loading image URI: ${note.photoUri}", e)
                                    // se falhar, tenta com base64
                                    loadBase64Image(imageView, note.photoBase64)
                                    currentPhotoBase64 = note.photoBase64
                                }
                            } else {
                                // se o ficheiro não existir, tenta com base64 ... outra vez?
                                loadBase64Image(imageView, note.photoBase64)
                                currentPhotoBase64 = note.photoBase64
                            }
                        } else {
                            // sem Uri, tenta com base64... outra vez? xD
                            loadBase64Image(imageView, note.photoBase64)
                            currentPhotoBase64 = note.photoBase64
                        }
                    } else {
                        imageView.visibility = View.GONE
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
                        userId = getSharedPreferences("AppPrefs", MODE_PRIVATE).getInt("userId", -1),
                        title = title,
                        content = content,
                        photoUri = currentPhotoUri,
                        photoBase64 = currentPhotoBase64,
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