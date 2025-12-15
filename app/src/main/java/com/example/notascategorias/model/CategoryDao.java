package com.example.notascategorias.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CategoryDao {

    // Insertar una nueva categoría
    @Insert
    long insertCategory(Category category); // Retorna el ID generado

    // Actualizar una categoría existente
    @Update
    void updateCategory(Category category);

    // Eliminar una categoría
    @Delete
    void deleteCategory(Category category);

    // Obtener todas las categorías
    @Query("SELECT * FROM categories ORDER BY category_name ASC")
    List<Category> getAllCategories();

    // Obtener una categoría por ID
    @Query("SELECT * FROM categories WHERE category_id = :categoryId")
    Category getCategoryById(int categoryId);

    // Obtener categoría con todas sus notas (relación 1:N)
    @Transaction
    @Query("SELECT * FROM categories WHERE category_id = :categoryId")
    CategoryWithNotes getCategoryWithNotes(int categoryId);

    // Obtener todas las categorías con sus notas
    @Transaction
    @Query("SELECT * FROM categories ORDER BY category_name ASC")
    List<CategoryWithNotes> getAllCategoriesWithNotes();

    // Contar cuántas notas tiene cada categoría
    @Query("SELECT categories.*, COUNT(notes.note_id) as note_count " +
            "FROM categories " +
            "LEFT JOIN notes ON categories.category_id = notes.category_id " +
            "GROUP BY categories.category_id " +
            "ORDER BY category_name ASC")
    List<CategoryWithCount> getCategoriesWithCount();

    // Verificar si existe una categoría con ese nombre
    @Query("SELECT COUNT(*) FROM categories WHERE category_name = :name")
    int categoryExists(String name);

    // Eliminar todas las categorías
    @Query("DELETE FROM categories")
    void deleteAllCategories();
}