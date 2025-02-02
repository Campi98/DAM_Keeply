package pt.ipt.dam.a25269a24639.keeply.data

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import pt.ipt.dam.a25269a24639.keeply.activity.NoteDetailActivity
import pt.ipt.dam.a25269a24639.keeply.R
import pt.ipt.dam.a25269a24639.keeply.data.domain.Note
import pt.ipt.dam.a25269a24639.keeply.util.ImageUtils
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Adaptador para exibir notas numa RecyclerView.
 *
 * Esta classe é responsável por:
 * - Gerir a exibição de notas numa lista
 * - Carregar e exibir imagens associadas às notas
 * - Gerir interações com as notas (cliques)
 *
 * @property notes Lista de notas a exibir
 */
class NoteAdapter(private var notes: List<Note>) :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    /**
     * ViewHolder para cada item da lista de notas.
     *
     * @property titleView TextView para o título da nota
     * @property contentView TextView para o conteúdo da nota
     * @property cardView CardView que contém a nota completa
     */
    class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView = view.findViewById(R.id.noteTitle)
        val contentView: TextView = view.findViewById(R.id.noteContent)
        val cardView: MaterialCardView = view as MaterialCardView
    }


    /**
     * Carrega uma imagem codificada em base64 num ImageView.
     *
     * @param imageView View onde a imagem será exibida
     * @param base64String String da imagem em formato base64
     */
    private fun loadBase64Image(imageView: ImageView, base64String: String?) {
        if (base64String != null) {
            try {
                val bitmap = ImageUtils.base64ToBitmap(base64String)
                imageView.setImageBitmap(bitmap)
            } catch (e: Exception) {
                Log.e("NoteAdapter", "Error decoding base64 image", e)
                imageView.setImageDrawable(null)
            }
        } else {
            imageView.setImageDrawable(null)
        }
    }

    /**
     * Atualiza a lista de notas e notifica o adaptador.
     *
     * @param newNotes Nova lista de notas a exibir
     */
    fun updateNotes(newNotes: List<Note>) {
        notes = newNotes
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.titleView.text = note.title
        holder.contentView.text = note.content

        // tenta carregar a imagem do URI local primeiro, depois do base64
        val imageView = holder.itemView.findViewById<ImageView>(R.id.noteImage)
        if (note.photoUri != null || note.photoBase64 != null) {
            imageView.visibility = View.VISIBLE

            if (note.photoUri != null) {
                // vê se o ficheiro existe no caminho do URI
                val file = Uri.parse(note.photoUri).path?.let { File(it) }
                if (file?.exists() == true) {
                    try {
                        imageView.setImageURI(Uri.parse(note.photoUri))
                    } catch (e: Exception) {
                        Log.e("NoteAdapter", "Error loading image URI: ${note.photoUri}", e)
                        // Se falhar, tentar base64
                        loadBase64Image(imageView, note.photoBase64)
                    }
                } else {
                    // se o ficheiro não existir, tentar base64
                    loadBase64Image(imageView, note.photoBase64)
                }
            } else {
                // Se não houver URI, tentar base64
                loadBase64Image(imageView, note.photoBase64)
            }
        } else {
            imageView.visibility = View.GONE
        }


        // configura o clique numa nota para abrir a atividade de detalhes
        holder.cardView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, NoteDetailActivity::class.java).apply {
                putExtra("note_id", note.id)
                putExtra("note_title", note.title)
                putExtra("note_content", note.content)
                putExtra("photo_uri", note.photoUri)
            }
            context.startActivity(intent)
        }

        val instant = Instant.ofEpochMilli(note.timestamp)
        val dateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        val timestampLabel = if (note.synced) "Nota Editada: " else "Nota Criada: "

        holder.itemView.findViewById<TextView>(R.id.noteTimestamp).apply {
            text = "$timestampLabel${dateTime.format(formatter)}"
        }
    }

    override fun getItemCount() = notes.size
}