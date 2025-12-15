package com.example.notascategorias.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "notes",
        foreignKeys = @ForeignKey(
                entity = Category.class,
                parentColumns = "category_id",
                childColumns = "category_id",
                onDelete = ForeignKey.CASCADE // Si se borra la categoría, se borran sus notas
        ),
        indices = {@Index("category_id")} // Índice para mejorar búsquedas
)
public class Note{

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "note_id")
    private int noteId;

    @ColumnInfo(name = "note_title")
    private String noteTitle;

    @ColumnInfo(name = "note_content")
    private String noteContent;

    @ColumnInfo(name = "created_at")
    private String createdAt;

    @ColumnInfo(name = "category_id")
    private int categoryId; // Foreign Key

    // Constructor vacío
    public Note(){
    }

    // Constructor completo
    public Note(String noteTitle, String noteContent, String createdAt, int categoryId){
        this.noteTitle = noteTitle;
        this.noteContent = noteContent;
        this.createdAt = createdAt;
        this.categoryId = categoryId;
    }

    // Getters y Setters
    public int getNoteId(){
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}