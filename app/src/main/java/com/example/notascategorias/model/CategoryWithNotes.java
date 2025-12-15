package com.example.notascategorias.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class CategoryWithNotes {

    @Embedded
    public Category category;

    @Relation(
            parentColumn = "category_id",
            entityColumn = "category_id"
    )
    public List<Note> notes;

    // Constructor vacío
    public CategoryWithNotes() {
    }

    // Getters
    public Category getCategory() {
        return category;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }
}