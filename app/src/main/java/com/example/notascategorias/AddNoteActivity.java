package com.example.notascategorias;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notascategorias.model.AppDatabase;
import com.example.notascategorias.model.Category;
import com.example.notascategorias.model.Note;
import com.example.notascategorias.model.HistoryManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddNoteActivity extends AppCompatActivity {

    // Constante para formato de fecha
    private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm";

    // Vistas
    private TextView tvFormTitle;
    private Spinner spinnerCategory;
    private EditText etNoteTitle;
    private EditText etNoteContent;
    private TextView tvCreatedAt;
    private Button btnSave;
    private Button btnCancel;

    // Base de datos
    private AppDatabase database;
    private ExecutorService executorService;

    // Variables para categorías
    private List<Category> categoryList;
    private int selectedCategoryId = -1;

    // Variables para edición
    private Note noteToEdit;
    private boolean isEditMode = false;
    private String selectedCategoryName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        // Inicializar base de datos y ejecutor
        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        // Inicializar vistas
        initViews();

        // Cargar categorías en el Spinner
        loadCategories();

        // Verificar si estamos en modo edición
        checkEditMode();

        // Configurar listeners
        setupListeners();
    }

    private void initViews() {
        tvFormTitle = findViewById(R.id.tvFormTitle);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        etNoteTitle = findViewById(R.id.etNoteTitle);
        etNoteContent = findViewById(R.id.etNoteContent);
        tvCreatedAt = findViewById(R.id.tvCreatedAt);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void loadCategories() {
        executorService.execute(() -> {
            categoryList = database.categoryDao().getAllCategories();

            runOnUiThread(() -> {
                if (categoryList.isEmpty()) {
                    Toast.makeText(this, "Crea una categoría primero", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }

                // Configurar el Spinner con las categorías
                ArrayAdapter<Category> adapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        categoryList
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategory.setAdapter(adapter);

                spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedCategoryId = categoryList.get(position).getCategoryId();
                        selectedCategoryName = categoryList.get(position).getCategoryName();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        selectedCategoryId = -1;
                        selectedCategoryName = "";
                    }
                });
            });
        });
    }

    private void checkEditMode() {
        int noteId = getIntent().getIntExtra("NOTE_ID", -1);

        if (noteId != -1) {
            isEditMode = true;
            tvFormTitle.setText("Editar nota");
            btnSave.setText("Actualizar nota");
            loadNoteData(noteId);
        } else {
            // Modo creación: establecer fecha actual
            String currentDate = getCurrentDate();
            tvCreatedAt.setText(currentDate);
        }
    }

    private void loadNoteData(int noteId) {
        executorService.execute(() -> {
            noteToEdit = database.noteDao().getNoteById(noteId);

            runOnUiThread(() -> {
                if (noteToEdit != null) {
                    etNoteTitle.setText(noteToEdit.getNoteTitle());
                    etNoteContent.setText(noteToEdit.getNoteContent());
                    tvCreatedAt.setText(noteToEdit.getCreatedAt());

                    // Seleccionar la categoría correcta en el Spinner
                    for (int i = 0; i < categoryList.size(); i++) {
                        if (categoryList.get(i).getCategoryId() == noteToEdit.getCategoryId()) {
                            spinnerCategory.setSelection(i);
                            break;
                        }
                    }
                }
            });
        });
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveNote());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void saveNote() {
        String title = etNoteTitle.getText().toString().trim();
        String content = etNoteContent.getText().toString().trim();
        String createdAt = tvCreatedAt.getText().toString();

        // Validaciones
        if (selectedCategoryId == -1) {
            Toast.makeText(this, "Por favor, selecciona una categoría", Toast.LENGTH_SHORT).show();
            return;
        }

        if (title.isEmpty()) {
            etNoteTitle.setError("Titulo requerido");
            etNoteTitle.requestFocus();
            return;
        }

        if (content.isEmpty()) {
            etNoteContent.setError("Contenido requerido");
            etNoteContent.requestFocus();
            return;
        }

        if (createdAt.isEmpty() || createdAt.equals("--/--/----")) {
            Toast.makeText(this, "Fecha invalida", Toast.LENGTH_SHORT).show();
            return;
        }

        // Guardar en segundo plano
        executorService.execute(() -> {
            if (isEditMode && noteToEdit != null) {
                noteToEdit.setNoteTitle(title);
                noteToEdit.setNoteContent(content);
                noteToEdit.setCategoryId(selectedCategoryId);
                database.noteDao().updateNote(noteToEdit);

                HistoryManager.getInstance(this).logNoteAction(
                        HistoryManager.ACTION_UPDATE_NOTE,
                        title,
                        selectedCategoryName
                );

                runOnUiThread(() -> {
                    Toast.makeText(this, "Nota actualizada", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } else {
                Note newNote = new Note(title, content, createdAt, selectedCategoryId);
                database.noteDao().insertNote(newNote);

                HistoryManager.getInstance(this).logNoteAction(
                        HistoryManager.ACTION_INSERT_NOTE,
                        title,
                        selectedCategoryName
                );

                runOnUiThread(() -> {
                    Toast.makeText(this, "Nota creada", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        return dateFormat.format(new Date());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}