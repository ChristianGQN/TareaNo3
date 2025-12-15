package com.example.notascategorias;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.notascategorias.model.History;
import com.example.notascategorias.model.HistoryManager;
import com.example.notascategorias.view.HistoryAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoryActivity extends AppCompatActivity {

    // Vistas
    private TextView tvTotalActions;
    private Button btnFilterAll;
    private Button btnFilterCategories;
    private Button btnFilterNotes;
    private EditText etSearchHistory;
    private Button btnClearHistory;
    private RecyclerView recyclerViewHistory;
    private TextView tvEmptyMessage;

    private HistoryAdapter historyAdapter;
    private AppDatabase database;
    private ExecutorService executorService;

    private String currentFilter = "ALL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        initViews();
        setupRecyclerView();
        setupListeners();
        loadHistory();
    }

    private void initViews() {
        tvTotalActions = findViewById(R.id.tvTotalActions);
        btnFilterAll = findViewById(R.id.btnFilterAll);
        btnFilterCategories = findViewById(R.id.btnFilterCategories);
        btnFilterNotes = findViewById(R.id.btnFilterNotes);
        etSearchHistory = findViewById(R.id.etSearchHistory);
        btnClearHistory = findViewById(R.id.btnClearHistory);
        recyclerViewHistory = findViewById(R.id.recyclerViewHistory);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
    }

    private void setupRecyclerView() {
        historyAdapter = new HistoryAdapter();
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewHistory.setAdapter(historyAdapter);
    }

    private void setupListeners() {
        // Filtros
        btnFilterAll.setOnClickListener(v -> {
            currentFilter = "ALL";
            loadHistory();
        });

        btnFilterCategories.setOnClickListener(v -> {
            currentFilter = "CATEGORIES";
            loadCategoryHistory();
        });

        btnFilterNotes.setOnClickListener(v -> {
            currentFilter = "NOTES";
            loadNoteHistory();
        });

        etSearchHistory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    searchHistory(s.toString());
                } else {
                    applyCurrentFilter();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnClearHistory.setOnClickListener(v -> showClearHistoryDialog());
    }

    private void applyCurrentFilter() {
        switch (currentFilter) {
            case "CATEGORIES":
                loadCategoryHistory();
                break;
            case "NOTES":
                loadNoteHistory();
                break;
            default:
                loadHistory();
                break;
        }
    }

    private void loadHistory() {
        executorService.execute(() -> {
            List<History> historyList = database.historyDao().getAllHistory();
            int totalCount = database.historyDao().getTotalActionsCount();

            runOnUiThread(() -> {
                historyAdapter.setHistoryList(historyList);
                updateUI(historyList, totalCount);
            });
        });
    }

    private void loadCategoryHistory() {
        executorService.execute(() -> {
            List<History> allHistory = database.historyDao().getAllHistory();
            List<History> categoryHistory = new ArrayList<>();

            for (History history : allHistory) {
                String action = history.getAction();
                if (action.equals(HistoryManager.ACTION_INSERT_CATEGORY) ||
                        action.equals(HistoryManager.ACTION_UPDATE_CATEGORY) ||
                        action.equals(HistoryManager.ACTION_DELETE_CATEGORY)) {
                    categoryHistory.add(history);
                }
            }

            int totalCount = database.historyDao().getTotalActionsCount();

            runOnUiThread(() -> {
                historyAdapter.setHistoryList(categoryHistory);
                updateUI(categoryHistory, totalCount);
            });
        });
    }

    private void loadNoteHistory() {
        executorService.execute(() -> {
            List<History> allHistory = database.historyDao().getAllHistory();
            List<History> noteHistory = new ArrayList<>();

            for (History history : allHistory) {
                String action = history.getAction();
                if (action.equals(HistoryManager.ACTION_INSERT_NOTE) ||
                        action.equals(HistoryManager.ACTION_UPDATE_NOTE) ||
                        action.equals(HistoryManager.ACTION_DELETE_NOTE)) {
                    noteHistory.add(history);
                }
            }

            int totalCount = database.historyDao().getTotalActionsCount();

            runOnUiThread(() -> {
                historyAdapter.setHistoryList(noteHistory);
                updateUI(noteHistory, totalCount);
            });
        });
    }

    private void searchHistory(String searchText) {
        executorService.execute(() -> {
            List<History> searchResults = database.historyDao().searchHistory(searchText);
            int totalCount = database.historyDao().getTotalActionsCount();

            runOnUiThread(() -> {
                historyAdapter.setHistoryList(searchResults);
                updateUI(searchResults, totalCount);
            });
        });
    }

    private void updateUI(List<History> historyList, int totalCount) {
        tvTotalActions.setText("Total: " + totalCount + (totalCount == 1 ? " action" : " actions"));

        if (historyList.isEmpty()) {
            tvEmptyMessage.setVisibility(View.VISIBLE);
            recyclerViewHistory.setVisibility(View.GONE);
        } else {
            tvEmptyMessage.setVisibility(View.GONE);
            recyclerViewHistory.setVisibility(View.VISIBLE);
        }
    }

    private void showClearHistoryDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Historial limpio")
                .setMessage("Deseas eliminar el historial?")
                .setPositiveButton("Eliminar", (dialog, which) -> clearHistory())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void clearHistory() {
        executorService.execute(() -> {
            database.historyDao().deleteAllHistory();

            runOnUiThread(() -> {
                Toast.makeText(this, "Historial borrado", Toast.LENGTH_SHORT).show();
                loadHistory();
                etSearchHistory.setText("");
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
}