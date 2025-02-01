package pt.ipt.dam.a25269a24639.keeply.activity

import android.app.Activity
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import pt.ipt.dam.a25269a24639.keeply.R
import pt.ipt.dam.a25269a24639.keeply.util.ImageUtils

class FullscreenImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen_image)

        val imageView = findViewById<ImageView>(R.id.fullscreenImage)
        val deleteButton = findViewById<FloatingActionButton>(R.id.deleteImageFab)

        // Get image data from intent
        val photoUri = intent.getStringExtra("photo_uri")
        val photoBase64 = intent.getStringExtra("photo_base64")

        when {
            photoUri != null -> {
                imageView.setImageURI(android.net.Uri.parse(photoUri))
            }
            photoBase64 != null -> {
                val bitmap = ImageUtils.base64ToBitmap(photoBase64)
                imageView.setImageBitmap(bitmap)
            }
        }

        // Handle delete button click
        deleteButton.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Remover foto")
                .setMessage("Tem certeza que deseja remover esta foto da nota?")
                .setPositiveButton("Sim") { _, _ ->
                    // Set result to indicate photo should be deleted
                    // define resultado para indiciar que a foto deve ser apagada
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                .setNegativeButton("Não", null)
                .show()
        }
    }
}