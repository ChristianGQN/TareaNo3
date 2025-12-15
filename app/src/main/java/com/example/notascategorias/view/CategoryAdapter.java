package com.example.notascategorias.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notascategorias.R;
import com.example.notascategorias.model.CategoryWithNotes;
import com.example.notascategorias.model.Note;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<CategoryWithNotes> categoryList;
    private OnNoteClickListener noteClickListener;

    // Interface para manejar clicks en las notas
    public interface OnNoteClickListener {
        void onNoteClick(Note note);
        void onNoteDelete(Note note);
    }

    // Constructor
    public CategoryAdapter() {
        this.categoryList = new ArrayList<>();
    }

    // Metodo para establecer el listener
    public void setOnNoteClickListener(OnNoteClickListener listener) {
        this.noteClickListener = listener;
    }

    // Metodo para actualizar la lista de categorías con notas
    public void setCategoryList(List<CategoryWithNotes> categoryList) {
        this.categoryList = categoryList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryWithNotes currentCategory = categoryList.get(position);

        // Establecer nombre de categoría
        holder.tvCategoryName.setText(currentCategory.getCategory().getCategoryName());

        // Mostrar cantidad de notas
        int noteCount = currentCategory.getNotes().size();
        holder.tvNoteCount.setText(noteCount + (noteCount == 1 ? " nota" : " notas"));

        // Configurar el RecyclerView de notas dentro de la categoría
        if (noteCount > 0) {
            holder.recyclerViewNotes.setVisibility(View.VISIBLE);
            holder.tvEmptyNotes.setVisibility(View.GONE);

            // Crear y configurar el NoteAdapter
            NoteAdapter noteAdapter = new NoteAdapter();
            noteAdapter.setNoteList(currentCategory.getNotes());

            // Pasar los eventos del NoteAdapter al CategoryAdapter
            noteAdapter.setOnNoteClickListener(new NoteAdapter.OnNoteClickListener() {
                @Override
                public void onNoteClick(Note note) {
                    if (noteClickListener != null) {
                        noteClickListener.onNoteClick(note);
                    }
                }

                @Override
                public void onNoteDelete(Note note) {
                    if (noteClickListener != null) {
                        noteClickListener.onNoteDelete(note);
                    }
                }
            });

            holder.recyclerViewNotes.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
            holder.recyclerViewNotes.setAdapter(noteAdapter);
        } else {
            // No hay notas en esta categoría
            holder.recyclerViewNotes.setVisibility(View.GONE);
            holder.tvEmptyNotes.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    // ViewHolder
    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName;
        TextView tvNoteCount;
        RecyclerView recyclerViewNotes;
        TextView tvEmptyNotes;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvNoteCount = itemView.findViewById(R.id.tvNoteCount);
            recyclerViewNotes = itemView.findViewById(R.id.recyclerViewNotes);
            tvEmptyNotes = itemView.findViewById(R.id.tvEmptyNotes);
        }
    }
}