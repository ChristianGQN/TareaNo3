package com.example.notascategorias.model;

import androidx.room.ColumnInfo;
public class ActionCount {
    @ColumnInfo(name = "action")
    public String action;

    @ColumnInfo(name = "count")
    public int count;

    public ActionCount() {
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}