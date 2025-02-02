package pt.ipt.dam.a25269a24639.keeply.activity

import android.app.Activity
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import pt.ipt.dam.a25269a24639.keeply.R
import pt.ipt.dam.a25269a24639.keeply.util.ImageUtils

/**
 * Activity para visualização de imagens em ecrã inteiro.
 *
 * Esta activity permite:
 * - Visualizar imagens em ecrã inteiro
 * - Remover imagens das notas
 * - Suportar imagens tanto em URI como em Base64
 */
class FullscreenImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen_image)

        val imageView = findViewById<ImageView>(R.id.fullscreenImage)
        val deleteButton = findViewById<FloatingActionButton>(R.id.deleteImageFab)

        // Obter dados da imagem do intent
        val photoUri = intent.getStringExtra("photo_uri")
        val photoBase64 = intent.getStringExtra("photo_base64")

// Carregar a imagem com base no formato disponível
        when {
            photoUri != null -> {
                imageView.setImageURI(android.net.Uri.parse(photoUri))
            }

            photoBase64 != null -> {
                val bitmap = ImageUtils.base64ToBitmap(photoBase64)
                imageView.setImageBitmap(bitmap)
            }
        }

        // Gerir clique no botão de eliminar
        deleteButton.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Remover foto")
                .setMessage("Tem certeza que deseja remover esta foto da nota?")
                .setPositiveButton("Sim") { _, _ ->
                    // define resultado para indiciar que a foto deve ser apagada
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                .setNegativeButton("Não", null)
                .show()
        }
    }
}