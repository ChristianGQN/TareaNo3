package com.example.notascategorias.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "history")
public class History {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "history_id")
    private int historyId;

    @ColumnInfo(name = "action")
    private String action;

    @ColumnInfo(name = "created_at")
    private String createdAt;

    @ColumnInfo(name = "details")
    private String details;

    public History() {
    }

    public History(String action, String createdAt, String details) {
        this.action = action;
        this.createdAt = createdAt;
        this.details = details;
    }

    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}