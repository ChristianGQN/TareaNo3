package com.example.notascategorias.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notascategorias.R;
import com.example.notascategorias.model.History;
import com.example.notascategorias.model.HistoryManager;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<History> historyList;

    public HistoryAdapter() {
        this.historyList = new ArrayList<>();
    }

    public void setHistoryList(List<History> historyList) {
        this.historyList = historyList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        History currentHistory = historyList.get(position);

        String actionType = getActionTypeText(currentHistory.getAction());
        holder.tvActionType.setText(actionType);
        holder.tvActionDetails.setText(currentHistory.getDetails());
        holder.tvActionDate.setText(currentHistory.getCreatedAt());
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    private String getActionTypeText(String action) {
        switch (action) {
            case HistoryManager.ACTION_INSERT_CATEGORY:
                return "Categoría creada";
            case HistoryManager.ACTION_UPDATE_CATEGORY:
                return "Categoría actualizada";
            case HistoryManager.ACTION_DELETE_CATEGORY:
                return "Categoría eliminada";
            case HistoryManager.ACTION_INSERT_NOTE:
                return "Nota creada";
            case HistoryManager.ACTION_UPDATE_NOTE:
                return "Nota actualizada";
            case HistoryManager.ACTION_DELETE_NOTE:
                return "Nota eliminada";
            default:
                return "Error";
        }
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvActionType;
        TextView tvActionDetails;
        TextView tvActionDate;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvActionType = itemView.findViewById(R.id.tvActionType);
            tvActionDetails = itemView.findViewById(R.id.tvActionDetails);
            tvActionDate = itemView.findViewById(R.id.tvActionDate);
        }
    }
}