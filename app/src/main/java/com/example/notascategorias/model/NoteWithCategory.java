package com.example.notascategorias.model;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;

public class NoteWithCategory {

    @Embedded
    public Note note;

    @ColumnInfo(name = "category_name")
    public String categoryName;

    // Constructor vacío
    public NoteWithCategory() {
    }

    // Getters y Setters
    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}