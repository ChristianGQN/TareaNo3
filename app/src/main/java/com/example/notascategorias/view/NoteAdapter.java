package com.example.notascategorias.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notascategorias.R;
import com.example.notascategorias.model.Note;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> noteList;
    private OnNoteClickListener listener;

    // Interface para manejar los clicks
    public interface OnNoteClickListener {
        void onNoteClick(Note note);
        void onNoteDelete(Note note);
    }

    // Constructor
    public NoteAdapter() {
        this.noteList = new ArrayList<>();
    }

    // Metodo para establecer el listener
    public void setOnNoteClickListener(OnNoteClickListener listener) {
        this.listener = listener;
    }

    // Metodo para actualizar la lista de notas
    public void setNoteList(List<Note> noteList) {
        this.noteList = noteList;
        notifyDataSetChanged();
    }

    // Metodo para obtener una nota en una posición específica
    public Note getNoteAt(int position) {
        return noteList.get(position);
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note currentNote = noteList.get(position);

        holder.tvNoteTitle.setText(currentNote.getNoteTitle());
        holder.tvNoteContent.setText(currentNote.getNoteContent());
        holder.tvNoteDate.setText(currentNote.getCreatedAt());

        // Click en la nota completa (para editar)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNoteClick(currentNote);
            }
        });

        // Click en el botón de eliminar
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNoteDelete(currentNote);
            }
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    // ViewHolder
    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView tvNoteTitle;
        TextView tvNoteContent;
        TextView tvNoteDate;
        ImageButton btnDelete;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNoteTitle = itemView.findViewById(R.id.tvNoteTitle);
            tvNoteContent = itemView.findViewById(R.id.tvNoteContent);
            tvNoteDate = itemView.findViewById(R.id.tvNoteDate);
            btnDelete = itemView.findViewById(R.id.btnDeleteNote);
        }
    }
}