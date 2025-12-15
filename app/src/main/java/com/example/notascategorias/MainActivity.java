package com.example.notascategorias;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notascategorias.model.AppDatabase;
import com.example.notascategorias.model.CategoryWithNotes;
import com.example.notascategorias.model.Note;
import com.example.notascategorias.view.CategoryAdapter;
import com.example.notascategorias.model.HistoryManager;
import com.example.notascategorias.model.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements CategoryAdapter.OnNoteClickListener {

    // Vistas
    private EditText etSearch;
    private Button btnSearch;
    private Button btnAddCategory;
    private Button btnAddNote;
    private Button btnViewHistory;
    private RecyclerView recyclerViewCategories;
    private TextView tvEmptyMessage;

    private CategoryAdapter categoryAdapter;
    private AppDatabase database;
    private ExecutorService executorService;

    private boolean isSearchMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        initViews();

        setupRecyclerView();

        setupListeners();
    }

    private void initViews() {
        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        btnAddCategory = findViewById(R.id.btnAddCategory);
        btnAddNote = findViewById(R.id.btnAddNote);
        btnViewHistory = findViewById(R.id.btnViewHistory);
        recyclerViewCategories = findViewById(R.id.recyclerViewCategories);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
    }

    private void setupRecyclerView() {
        categoryAdapter = new CategoryAdapter();
        categoryAdapter.setOnNoteClickListener(this);

        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCategories.setAdapter(categoryAdapter);
    }

    private void setupListeners() {
        btnAddCategory.setOnClickListener(v -> openAddCategory());
        btnAddNote.setOnClickListener(v -> openAddNote(-1));
        btnViewHistory.setOnClickListener(v -> openHistory());
        btnSearch.setOnClickListener(v -> performSearch());

        etSearch.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && etSearch.getText().toString().trim().isEmpty()) {
                clearSearch();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isSearchMode) {
            performSearch();
        } else {
            loadAllCategories();
        }
    }

    private void loadAllCategories() {
        isSearchMode = false;
        executorService.execute(() -> {
            List<CategoryWithNotes> categories = database.categoryDao().getAllCategoriesWithNotes();

            runOnUiThread(() -> {
                categoryAdapter.setCategoryList(categories);

                if (categories.isEmpty()) {
                    tvEmptyMessage.setVisibility(View.VISIBLE);
                    recyclerViewCategories.setVisibility(View.GONE);
                } else {
                    tvEmptyMessage.setVisibility(View.GONE);
                    recyclerViewCategories.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    private void performSearch() {
        String searchText = etSearch.getText().toString().trim();

        if (searchText.isEmpty()) {
            Toast.makeText(this, "Ingrese un texto de búsqueda", Toast.LENGTH_SHORT).show();
            return;
        }

        isSearchMode = true;

        executorService.execute(() -> {
            // Buscar en notas (título y contenido)
            List<Note> notesResults = database.noteDao().searchNotes(searchText);

            // Buscar notas por nombre de categoría
            List<Note> categoryResults = database.noteDao().searchNotesByCategoryName(searchText);

            // Combinar resultados evitando duplicados
            List<Note> combinedResults = new ArrayList<>(notesResults);
            for (Note note : categoryResults) {
                boolean isDuplicate = false;
                for (Note existing : combinedResults) {
                    if (existing.getNoteId() == note.getNoteId()) {
                        isDuplicate = true;
                        break;
                    }
                }
                if (!isDuplicate) {
                    combinedResults.add(note);
                }
            }

            // Agrupar las notas encontradas por categoría
            List<CategoryWithNotes> filteredCategories = groupNotesByCategory(combinedResults);

            runOnUiThread(() -> {
                categoryAdapter.setCategoryList(filteredCategories);

                if (filteredCategories.isEmpty()) {
                    tvEmptyMessage.setText("No hay notas encontradas para: \"" + searchText + "\"");
                    tvEmptyMessage.setVisibility(View.VISIBLE);
                    recyclerViewCategories.setVisibility(View.GONE);
                } else {
                    tvEmptyMessage.setVisibility(View.GONE);
                    recyclerViewCategories.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Notas encontradas: " + combinedResults.size(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private List<CategoryWithNotes> groupNotesByCategory(List<Note> notes) {
        List<CategoryWithNotes> result = new ArrayList<>();

        // Obtener todas las categorías involucradas
        for (Note note : notes) {
            int categoryId = note.getCategoryId();

            // Buscar si ya tenemos esta categoría en el resultado
            CategoryWithNotes existingCategory = null;
            for (CategoryWithNotes cwn : result) {
                if (cwn.getCategory().getCategoryId() == categoryId) {
                    existingCategory = cwn;
                    break;
                }
            }

            // Si no existe, crearla y agregarla
            if (existingCategory == null) {
                CategoryWithNotes newCategory = new CategoryWithNotes();
                newCategory.setCategory(database.categoryDao().getCategoryById(categoryId));
                newCategory.setNotes(new ArrayList<>());
                result.add(newCategory);
                existingCategory = newCategory;
            }

            // Agregar la nota a la categoría
            existingCategory.getNotes().add(note);
        }

        return result;
    }

    private void clearSearch() {
        etSearch.setText("");
        isSearchMode = false;
        loadAllCategories();
    }

    private void openAddCategory() {
        Intent intent = new Intent(this, AddCategoryActivity.class);
        startActivity(intent);
    }

    private void openAddNote(int noteId) {
        Intent intent = new Intent(this, AddNoteActivity.class);
        if (noteId != -1) {
            intent.putExtra("NOTE_ID", noteId);
        }
        startActivity(intent);
    }

    @Override
    public void onNoteClick(Note note) {
        openAddNote(note.getNoteId());
    }

    @Override
    public void onNoteDelete(Note note) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar nota")
                .setMessage("¿Estás seguro de eliminar esta nota?")
                .setPositiveButton("Eliminar", (dialog, which) -> deleteNote(note))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void deleteNote(Note note) {
        executorService.execute(() -> {
            // Obtener información antes de eliminar
            String noteTitle = note.getNoteTitle();
            Category category = database.categoryDao().getCategoryById(note.getCategoryId());
            String categoryName = category != null ? category.getCategoryName() : "Error";

            // Eliminar la nota
            database.noteDao().deleteNote(note);

            // Registrar en historial
            HistoryManager.getInstance(this).logNoteAction(
                    HistoryManager.ACTION_DELETE_NOTE,
                    noteTitle,
                    categoryName
            );

            runOnUiThread(() -> {
                Toast.makeText(this, "Nota eliminada", Toast.LENGTH_SHORT).show();
                if (isSearchMode) {
                    performSearch();
                } else {
                    loadAllCategories();
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    private void openHistory() {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }
}