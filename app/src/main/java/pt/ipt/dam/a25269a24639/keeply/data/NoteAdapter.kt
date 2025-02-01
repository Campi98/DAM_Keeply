package pt.ipt.dam.a25269a24639.keeply.data

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import pt.ipt.dam.a25269a24639.keeply.activity.NoteDetailActivity
import pt.ipt.dam.a25269a24639.keeply.R
import pt.ipt.dam.a25269a24639.keeply.data.domain.Note
import pt.ipt.dam.a25269a24639.keeply.util.ImageUtils
import java.io.File

class NoteAdapter(private var notes: List<Note>) :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView = view.findViewById(R.id.noteTitle)
        val contentView: TextView = view.findViewById(R.id.noteContent)
        val cardView: MaterialCardView = view as MaterialCardView
    }


    // função para carregar a imagem em base64
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

    fun updateNotes(newNotes: List<Note>) {
        notes = newNotes
        notifyDataSetChanged()      // TODO: Ver se é necessário
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

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
                        // carrega a imagem em base64 se falhar
                        //loadBase64Image(imageView, note.photoBase64)
                    }
                } else {
                    // eu sei que isto está repetido, dou fix depois, não vá isto dar erro
                    //loadBase64Image(imageView, note.photoBase64)
                }
            } else {
                // eu sei que isto está repetido, dou fix depois, não vá isto dar erro
                //loadBase64Image(imageView, note.photoBase64)
            }
        } else {
            imageView.visibility = View.GONE
        }


        
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
    }

    override fun getItemCount() = notes.size
}