package com.example.notascategorias.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDao {

    // Insertar una nueva nota
    @Insert
    void insertNote(Note note);

    // Actualizar una nota existente
    @Update
    void updateNote(Note note);

    // Eliminar una nota
    @Delete
    void deleteNote(Note note);

    // Obtener todas las notas
    @Query("SELECT * FROM notes ORDER BY note_id DESC")
    List<Note> getAllNotes();

    // Obtener una nota por ID
    @Query("SELECT * FROM notes WHERE note_id = :noteId")
    Note getNoteById(int noteId);

    // ===== CONSULTAS AVANZADAS =====

    // Obtener notas por categoría (relación 1:N)
    @Query("SELECT * FROM notes WHERE category_id = :categoryId ORDER BY created_at DESC")
    List<Note> getNotesByCategory(int categoryId);

    // Buscar notas por título usando LIKE
    @Query("SELECT * FROM notes WHERE note_title LIKE '%' || :searchText || '%' ORDER BY created_at DESC")
    List<Note> searchNotesByTitle(String searchText);

    // Buscar notas por contenido usando LIKE
    @Query("SELECT * FROM notes WHERE note_content LIKE '%' || :searchText || '%' ORDER BY created_at DESC")
    List<Note> searchNotesByContent(String searchText);

    // Buscar notas por título O contenido usando LIKE
    @Query("SELECT * FROM notes WHERE note_title LIKE '%' || :searchText || '%' " +
            "OR note_content LIKE '%' || :searchText || '%' ORDER BY created_at DESC")
    List<Note> searchNotes(String searchText);

    // Obtener las notas más recientes (límite de resultados)
    @Query("SELECT * FROM notes ORDER BY created_at DESC LIMIT :limit")
    List<Note> getRecentNotes(int limit);

    // Contar cuántas notas tiene una categoría
    @Query("SELECT COUNT(*) FROM notes WHERE category_id = :categoryId")
    int countNotesByCategory(int categoryId);

    // Obtener notas con información de la categoría (JOIN)
    @Query("SELECT notes.*, categories.category_name " +
            "FROM notes " +
            "INNER JOIN categories ON notes.category_id = categories.category_id " +
            "WHERE notes.category_id = :categoryId " +
            "ORDER BY notes.created_at DESC")
    List<NoteWithCategory> getNotesWithCategory(int categoryId);

    // Buscar notas en una categoría específica
    @Query("SELECT * FROM notes WHERE category_id = :categoryId " +
            "AND (note_title LIKE '%' || :searchText || '%' " +
            "OR note_content LIKE '%' || :searchText || '%') " +
            "ORDER BY created_at DESC")
    List<Note> searchNotesInCategory(int categoryId, String searchText);

    // Eliminar todas las notas de una categoría
    @Query("DELETE FROM notes WHERE category_id = :categoryId")
    void deleteNotesByCategory(int categoryId);

    // Eliminar todas las notas
    @Query("DELETE FROM notes")
    void deleteAllNotes();

    // Buscar notas por nombre de categoría usando LIKE con JOIN
    @Query("SELECT notes.* FROM notes " +
            "INNER JOIN categories ON notes.category_id = categories.category_id " +
            "WHERE categories.category_name LIKE '%' || :searchText || '%' " +
            "ORDER BY notes.created_at DESC")
    List<Note> searchNotesByCategoryName(String searchText);
}