package pt.ipt.dam.a25269a24639.keeply.data

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import pt.ipt.dam.a25269a24639.keeply.activity.NoteDetailActivity
import pt.ipt.dam.a25269a24639.keeply.R
import pt.ipt.dam.a25269a24639.keeply.data.domain.Note

class NoteAdapter(private var notes: List<Note>) :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView = view.findViewById(R.id.noteTitle)
        val contentView: TextView = view.findViewById(R.id.noteContent)
        val cardView: MaterialCardView = view as MaterialCardView
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