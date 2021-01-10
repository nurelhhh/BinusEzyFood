package com.example.binusezyfood;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TransactionItemAdapter extends RecyclerView.Adapter<TransactionItemAdapter.ViewHolder> {

    private final int[] itemIds;
    private final int[] qtys;
    private final int[] subtotalPrices;

    public TransactionItemAdapter(int[] itemIds, int[] qtys, int[] subtotalPrices) {
        this.itemIds = itemIds;
        this.qtys = qtys;
        this.subtotalPrices = subtotalPrices;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_item_layout, parent, false);

        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LinearLayout layout = holder.linearLayout;

        SQLiteOpenHelper dbHelper = new DBHelper(layout.getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("ITEMS", new String[]{"NAME", "PRICE"}, "_id = ?", new String[]{String.valueOf(itemIds[position])}, null, null, null);


        String namePrice = String.valueOf(itemIds[position]);
        if (cursor.moveToFirst()) {
            namePrice = cursor.getString(0) + " @ " + cursor.getInt(1);
        }
        holder.namePriceText.setText(namePrice);

        cursor.close();
        db.close();

        holder.subtotalPriceText.setText(String.valueOf(subtotalPrices[position]));
        holder.qtyText.setText(String.valueOf("x" + qtys[position]));

    }

    @Override
    public int getItemCount() {
        return itemIds.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout linearLayout;
        public TextView namePriceText;
        public TextView qtyText;
        public TextView subtotalPriceText;

        public ViewHolder(@NonNull LinearLayout layout) {
            super(layout);

            linearLayout = layout;
            namePriceText = (TextView) layout.getChildAt(0);
            qtyText = (TextView) layout.getChildAt(1);
            subtotalPriceText = (TextView) layout.getChildAt(2);
        }
    }
}
