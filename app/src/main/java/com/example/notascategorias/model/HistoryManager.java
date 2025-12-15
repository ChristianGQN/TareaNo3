package com.example.notascategorias.model;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoryManager {

    public static final String ACTION_INSERT_CATEGORY = "insert_category";
    public static final String ACTION_UPDATE_CATEGORY = "update_category";
    public static final String ACTION_DELETE_CATEGORY = "delete_category";
    public static final String ACTION_INSERT_NOTE = "insert_note";
    public static final String ACTION_UPDATE_NOTE = "update_note";
    public static final String ACTION_DELETE_NOTE = "delete_note";

    private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";

    private AppDatabase database;
    private ExecutorService executorService;
    private static HistoryManager instance;

    private HistoryManager(Context context) {
        database = AppDatabase.getInstance(context);
        executorService = Executors.newSingleThreadExecutor();
    }

    public static synchronized HistoryManager getInstance(Context context) {
        if (instance == null) {
            instance = new HistoryManager(context.getApplicationContext());
        }
        return instance;
    }

    public void logAction(String action, String details) {
        executorService.execute(() -> {
            String timestamp = getCurrentTimestamp();
            History history = new History(action, timestamp, details);
            database.historyDao().insertHistory(history);
        });
    }

    public void logCategoryAction(String action, String categoryName) {
        String actionText = getActionText(action);
        String details = "Categoría " + actionText + ": \"" + categoryName + "\"";
        logAction(action, details);
    }

    public void logNoteAction(String action, String noteTitle, String categoryName) {
        String actionText = getActionText(action);
        String details = "Nota " + actionText + ": \"" + noteTitle + "\" en la categoría \"" + categoryName + "\"";
        logAction(action, details);
    }

    private String getActionText(String action) {
        switch (action) {
            case ACTION_INSERT_CATEGORY:
                return "creada";
            case ACTION_UPDATE_CATEGORY:
                return "actualizada";
            case ACTION_DELETE_CATEGORY:
                return "eliminada";
            case ACTION_INSERT_NOTE:
                return "creada";
            case ACTION_UPDATE_NOTE:
                return "actualizada";
            case ACTION_DELETE_NOTE:
                return "eliminada";
            default:
                return "Acción desconocida";
        }
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        return dateFormat.format(new Date());
    }

    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}