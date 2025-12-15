package com.example.notascategorias.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HistoryDao {
    @Insert
    void insertHistory(History history);

    @Query("SELECT * FROM history ORDER BY history_id DESC")
    List<History> getAllHistory();

    @Query("SELECT * FROM history WHERE `action` = :actionType ORDER BY history_id DESC")
    List<History> getHistoryByAction(String actionType);

    @Query("SELECT * FROM history WHERE created_at LIKE :date || '%' ORDER BY history_id DESC")
    List<History> getHistoryByDate(String date);

    @Query("SELECT * FROM history WHERE details LIKE '%' || :searchText || '%' ORDER BY history_id DESC")
    List<History> searchHistory(String searchText);

    @Query("SELECT * FROM history ORDER BY history_id DESC LIMIT :limit")
    List<History> getRecentHistory(int limit);

    @Query("SELECT COUNT(*) FROM history")
    int getTotalActionsCount();

    @Query("SELECT COUNT(*) FROM history WHERE `action` = :actionType")
    int getActionCount(String actionType);

    @Query("DELETE FROM history")
    void deleteAllHistory();

    @Query("SELECT `action`, COUNT(*) as count FROM history GROUP BY `action` ORDER BY count DESC")
    List<ActionCount> getActionStatistics();
}