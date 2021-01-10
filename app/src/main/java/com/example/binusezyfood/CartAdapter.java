package com.example.binusezyfood;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.binusezyfood.DataClasses.ItemCart;

import java.util.Vector;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private final Context mContext;
    private final Vector<ItemCart> itemCarts;

    public CartAdapter(Context mContext, Vector<ItemCart> itemCarts) {
        this.itemCarts = itemCarts;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item_layout, parent, false);

        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LinearLayout layout = holder.linearLayout;

        String priceQty = "";

        SQLiteOpenHelper dbHelper = new DBHelper(layout.getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("ITEMS", new String[] {"NAME", "PRICE", "IMAGE"}, "_id = ?", new String[] {String.valueOf(itemCarts.get(position).getItem_id())}, null, null, null, null);

        if (cursor.moveToFirst()) {
            holder.nameText.setText(cursor.getString(0));
            priceQty += cursor.getInt(1);
            holder.imageView.setImageResource(cursor.getInt(2));
        }

        cursor.close();
        db.close();

        priceQty += " x " + itemCarts.get(position).getQuantity();
        holder.priceQtyText.setText(priceQty);

        holder.subtotalPriceText.setText(String.valueOf(itemCarts.get(position).getSubtotal_price()));
        holder.removeBtn.setOnClickListener(v -> {
            SQLiteOpenHelper dbHelper1 = new DBHelper(layout.getContext());
            SQLiteDatabase db1 = dbHelper1.getReadableDatabase();

            db1.delete("CARTS", "_id = ?", new String[] {String.valueOf(itemCarts.get(position).getId())});

            ((Communicators) mContext).refreshTotalPrice(itemCarts.get(position).getSubtotal_price(), position);

            itemCarts.remove(position);

            db1.close();

            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount()-position);
        });

    }

    @Override
    public int getItemCount() {
        return itemCarts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout linearLayout;
        public ImageView imageView;
        public TextView nameText;
        public TextView priceQtyText;
        public TextView subtotalPriceText;
        public Button removeBtn;

        public ViewHolder(@NonNull LinearLayout layout) {
            super(layout);

            linearLayout = layout;
            LinearLayout innerLayout1 = (LinearLayout) linearLayout.getChildAt(0);
            LinearLayout innerLayout2 = (LinearLayout) linearLayout.getChildAt(1);

            imageView = (ImageView) innerLayout1.getChildAt(0);
            removeBtn = (Button) innerLayout1.getChildAt(1);

            nameText = (TextView) innerLayout2.getChildAt(0);
            priceQtyText = (TextView) innerLayout2.getChildAt(1);
            subtotalPriceText = (TextView) innerLayout2.getChildAt(2);
        }
    }
}
