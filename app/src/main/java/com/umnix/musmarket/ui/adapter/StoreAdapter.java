package com.umnix.musmarket.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.umnix.musmarket.R;
import com.umnix.musmarket.model.Store;
import com.umnix.musmarket.ui.OnItemClickListener;

import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {
    private List<Store> itemList;
    private OnItemClickListener<Store> listener;

    public StoreAdapter(List<Store> itemList, OnItemClickListener<Store> listener) {
        this.itemList = itemList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Store store = itemList.get(position);

        holder.nameText.setText(store.getName());
        holder.view.setOnClickListener(v -> listener.onItemClick(store));
        holder.infoIcon.setOnClickListener(v -> listener.onInfoIconClick(store));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView nameText;
        private ImageView infoIcon;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            nameText = view.findViewById(R.id.store_title);
            infoIcon = view.findViewById(R.id.info_icon);
        }
    }
}
