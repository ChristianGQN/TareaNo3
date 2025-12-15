package com.example.notascategorias.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Category.class, Note.class, History.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // Nombre de la base de datos
    private static final String DATABASE_NAME = "notes_categories_db";

    // Instancia única de la base de datos (patrón Singleton)
    private static AppDatabase instance;

    // Métodos abstractos para obtener los DAOs
    public abstract CategoryDao categoryDao();
    public abstract NoteDao noteDao();
    public abstract HistoryDao historyDao();


    // Metodo para obtener la instancia de la base de datos
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME
                    )
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    // Metodo para destruir la instancia (útil para testing)
    public static void destroyInstance() {
        instance = null;
    }
}