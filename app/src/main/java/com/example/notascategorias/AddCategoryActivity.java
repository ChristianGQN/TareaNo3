package com.example.notascategorias;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notascategorias.model.AppDatabase;
import com.example.notascategorias.model.Category;
import com.example.notascategorias.model.HistoryManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddCategoryActivity extends AppCompatActivity {

    // Vistas
    private TextView tvFormTitle;
    private EditText etCategoryName;
    private Button btnSave;
    private Button btnCancel;

    // Base de datos
    private AppDatabase database;
    private ExecutorService executorService;

    // Variables para edición
    private Category categoryToEdit;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        // Inicializar base de datos y ejecutor
        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        // Inicializar vistas
        initViews();

        // Verificar si estamos en modo edición
        checkEditMode();

        // Configurar listeners
        setupListeners();
    }

    private void initViews() {
        tvFormTitle = findViewById(R.id.tvFormTitle);
        etCategoryName = findViewById(R.id.etCategoryName);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void checkEditMode() {
        int categoryId = getIntent().getIntExtra("CATEGORY_ID", -1);

        if (categoryId != -1) {
            isEditMode = true;
            tvFormTitle.setText("Editar categoría");
            btnSave.setText("Actualizar categoría");
            loadCategoryData(categoryId);
        }
    }

    private void loadCategoryData(int categoryId) {
        executorService.execute(() -> {
            categoryToEdit = database.categoryDao().getCategoryById(categoryId);

            runOnUiThread(() -> {
                if (categoryToEdit != null) {
                    etCategoryName.setText(categoryToEdit.getCategoryName());
                }
            });
        });
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveCategory());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void saveCategory() {
        String categoryName = etCategoryName.getText().toString().trim();

        // Validaciones
        if (categoryName.isEmpty()) {
            etCategoryName.setError("Nombre de categoría requerido");
            etCategoryName.requestFocus();
            return;
        }

        executorService.execute(() -> {
            int exists = database.categoryDao().categoryExists(categoryName);

            if (exists > 0 && (!isEditMode || !categoryToEdit.getCategoryName().equals(categoryName))) {
                runOnUiThread(() -> {
                    etCategoryName.setError("Category name already exists");
                    etCategoryName.requestFocus();
                });
                return;
            }

            if (isEditMode && categoryToEdit != null) {
                categoryToEdit.setCategoryName(categoryName);
                database.categoryDao().updateCategory(categoryToEdit);

                HistoryManager.getInstance(this).logCategoryAction(
                        HistoryManager.ACTION_UPDATE_CATEGORY,
                        categoryName
                );

                runOnUiThread(() -> {
                    Toast.makeText(this, "Categoría actualizada", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } else {
                Category newCategory = new Category(categoryName);
                database.categoryDao().insertCategory(newCategory);

                HistoryManager.getInstance(this).logCategoryAction(
                        HistoryManager.ACTION_INSERT_CATEGORY,
                        categoryName
                );

                runOnUiThread(() -> {
                    Toast.makeText(this, "Categoría creada", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}