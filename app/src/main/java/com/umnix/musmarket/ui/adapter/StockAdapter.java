package com.umnix.musmarket.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.umnix.musmarket.R;
import com.umnix.musmarket.model.Instrument;
import com.umnix.musmarket.model.Stock;

import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.ViewHolder> {
    private List<Stock> itemList;

    public StockAdapter(List<Stock> itemList) {
        this.itemList = itemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stock, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Stock stock = itemList.get(position);
        Instrument instrument = stock.getInstrument();

        holder.typeText.setText(instrument.getType());
        holder.titleText.setText("\"" + instrument.getModel() + "\"");
        holder.brandText.setText(instrument.getBrand());
        holder.priceText.setText(instrument.getPrice() + "");
        holder.quantityText.setText(stock.getQuantity() + "");
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView typeText;
        private TextView titleText;
        private TextView brandText;
        private TextView priceText;
        private TextView quantityText;

        public ViewHolder(View view) {
            super(view);
            typeText = view.findViewById(R.id.instrument_type);
            titleText = view.findViewById(R.id.instrument_title);
            brandText = view.findViewById(R.id.brand);
            priceText = view.findViewById(R.id.price_value);
            quantityText = view.findViewById(R.id.quantity_value);
        }
    }
}
