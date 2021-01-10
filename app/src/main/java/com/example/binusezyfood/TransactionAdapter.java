package com.example.binusezyfood;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;


public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private final int[] ids;
    private final int[] rest_ids;
    private final int[] total_prices;
    private final String[] dates;
    private final String[] receiver_addresses;

    public TransactionAdapter(int[] ids, int[] rest_ids, int[] total_prices, String[] dates, String[] receiver_addresses) {
        this.ids = ids;
        this.rest_ids = rest_ids;
        this.total_prices = total_prices;
        this.dates = dates;
        this.receiver_addresses = receiver_addresses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MaterialCardView cardView = (MaterialCardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_layout, parent, false);

        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MaterialCardView layout = holder.cardView;
        holder.idText.setText("ID: " + ids[position]);
        holder.dateText.setText(dates[position]);
        holder.totalPriceText.setText(Utils.toRupiah(total_prices[position]));
        holder.receiverAddressText.setText(receiver_addresses[position]);

        Cursor cursor = Utils.getDb(layout.getContext()).query("RESTAURANTS", new String[] {"NAME", "ADDRESS"}, "_id = ?", new String[] {String.valueOf(rest_ids[position])}, null, null, null, null);

        if (cursor.moveToFirst()) {
            holder.restNameText.setText(cursor.getString(0));
            holder.restAddressText.setText(cursor.getString(1));
        }

        cursor = Utils.getDb(layout.getContext()).query("ITEM_TRANSACTIONS", new String[] {"ITEM_ID", "SUBTOTAL_PRICE", "QUANTITY"}, "TRANSACTION_ID = ?", new String[] {String.valueOf(ids[position])}, null, null, null);

        int len = cursor.getCount();
        int[] itemIds = new int[len];
        int[] qtys = new int[len];
        int[] subtotalPrices = new int[len];

        int counter = 0;
        if (cursor.moveToFirst()) {
            do {
                itemIds[counter] = cursor.getInt(0);
                qtys[counter] = cursor.getInt(2);
                subtotalPrices[counter] = cursor.getInt(1);
                counter++;
            } while (cursor.moveToNext());
        }

        cursor.close();

        holder.detailBtn.setOnClickListener( v -> {
            if (holder.detailLayout.getVisibility() == View.VISIBLE) {
                holder.detailLayout.setVisibility(View.GONE);
                holder.detailBtn.setText("EXPAND");
            } else {
                holder.detailLayout.setVisibility(View.VISIBLE);
                holder.detailBtn.setText("COLLAPSE");
            }
        });

        RecyclerView recyclerView = holder.transItemRecycler;
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(layout.getContext());
        recyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter adapter = new TransactionItemAdapter(itemIds, qtys, subtotalPrices);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return ids.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public MaterialCardView cardView;
        public LinearLayout detailLayout;
        public TextView idText;
        public TextView totalPriceText;
        public TextView dateText;
        public Button detailBtn;
        public TextView restNameText;
        public TextView restAddressText;
        public TextView receiverAddressText;
        public RecyclerView transItemRecycler;

        public ViewHolder(@NonNull MaterialCardView cardView) {
            super(cardView);

            this.cardView = cardView;
            LinearLayout innerLayout1 = (LinearLayout) cardView.getChildAt(0);
            LinearLayout innerInnerLayout1 = (LinearLayout) innerLayout1.getChildAt(0);
            detailLayout = (LinearLayout) innerLayout1.getChildAt(4);

            idText = (TextView) innerInnerLayout1.getChildAt(0);
            totalPriceText = (TextView) innerInnerLayout1.getChildAt(1);

            dateText = (TextView) innerLayout1.getChildAt(1);

            receiverAddressText = (TextView) innerLayout1.getChildAt(3);

            transItemRecycler = (RecyclerView) detailLayout.getChildAt(0);
            restNameText = (TextView) detailLayout.getChildAt(2);
            restAddressText = (TextView) detailLayout.getChildAt(3);

            detailBtn = (Button) innerLayout1.getChildAt(5);

        }
    }
}
