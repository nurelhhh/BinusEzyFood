package com.example.binusezyfood;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


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
        LinearLayout layout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_layout, parent, false);

        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LinearLayout layout = holder.linearLayout;
        holder.idText.setText(String.valueOf(ids[position]));
        holder.dateText.setText(dates[position]);
        holder.totalPriceText.setText(String.valueOf(total_prices[position]));
        holder.receiverAddressText.setText(receiver_addresses[position]);

        SQLiteOpenHelper dbHelper = new DBHelper(layout.getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("RESTAURANTS", new String[] {"NAME", "ADDRESS"}, "_id = ?", new String[] {String.valueOf(rest_ids[position])}, null, null, null, null);

        if (cursor.moveToFirst()) {
            holder.restNameText.setText(cursor.getString(0));
            holder.restAddressText.setText(cursor.getString(1));
        }

        cursor = db.query("ITEM_TRANSACTIONS", new String[] {"ITEM_ID", "SUBTOTAL_PRICE", "QUANTITY"}, "TRANSACTION_ID = ?", new String[] {String.valueOf(ids[position])}, null, null, null);

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
        db.close();

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
        public LinearLayout linearLayout;
        public TextView idText;
        public TextView totalPriceText;
        public TextView dateText;
        public Button detailBtn;
        public TextView restNameText;
        public TextView restAddressText;
        public TextView receiverAddressText;
        public RecyclerView transItemRecycler;

        public ViewHolder(@NonNull LinearLayout layout) {
            super(layout);

            linearLayout = layout;
            LinearLayout innerLayout1 = (LinearLayout) layout.getChildAt(0);
            LinearLayout innerLayout2 = (LinearLayout) layout.getChildAt(1);

            LinearLayout innerInnerLayout = (LinearLayout) innerLayout1.getChildAt(0);
            idText = (TextView) innerInnerLayout.getChildAt(0);
            dateText = (TextView) innerInnerLayout.getChildAt(1);
            totalPriceText = (TextView) innerLayout1.getChildAt(1);
            detailBtn = (Button) innerLayout1.getChildAt(2);

            restNameText = (TextView) innerLayout2.getChildAt(0);
            restAddressText = (TextView) innerLayout2.getChildAt(1);
            receiverAddressText = (TextView) innerLayout2.getChildAt(2);
            transItemRecycler = (RecyclerView) innerLayout2.getChildAt(3);
        }
    }
}
