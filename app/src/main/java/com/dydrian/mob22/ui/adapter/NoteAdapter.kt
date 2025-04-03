package com.dydrian.mob22.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dydrian.mob22.data.model.Note
import com.dydrian.mob22.databinding.ItemNoteBinding

class NoteAdapter(
    private var notes: List<Note>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var listener: Listener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemNoteBinding.inflate(inflater, parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = notes[position]
        if (holder is NoteViewHolder) {
            holder.bind(item)
        }
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    fun setNotes(notes: List<Note>) {
        this.notes = notes
        notifyDataSetChanged()
    }

    inner class NoteViewHolder(
        private var binding: ItemNoteBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note) {
            binding.tvTitle.text = note.title
            binding.tvDesc.text = note.desc
            binding.mcvNote.setCardBackgroundColor(note.color)
            binding.mcvNote.setOnClickListener {
                listener?.onClickItem(note)
            }

            binding.mcvNote.setOnLongClickListener {
                // If listener is null, it returns false to avoid NullPointerException.
                // cuz long click is boolean
                listener?.onLongClickItem(note)?: false
            }
        }
    }

    interface Listener {
        fun onClickItem(item: Note)
        fun onLongClickItem(item: Note) : Boolean
    }
}