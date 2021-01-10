package com.example.binusezyfood;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.binusezyfood.DataClasses.ItemType;

import java.util.Vector;

public class ItemTypeAdapter extends RecyclerView.Adapter<ItemTypeAdapter.ViewHolder> {

    private final Vector<ItemType> itemTypes;

    public ItemTypeAdapter(Vector<ItemType> itemTypes) {
        this.itemTypes = itemTypes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FrameLayout layout = (FrameLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_type_layout, parent, false);
        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Button btn = holder.btn;
        btn.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(btn.getContext(), itemTypes.get(position).getImage()), null, null);
        btn.setText(itemTypes.get(position).getName());

        SQLiteOpenHelper dbHelper = new DBHelper(btn.getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("ITEMS", new String[] {"_id", "TYPE_ID", "NAME", "PRICE", "IMAGE"}, "TYPE_ID = ?", new String[] {Integer.toString(itemTypes.get(position).getId())}, null, null, null, null);

        int len = cursor.getCount();
        int[] ids = new int[len];
        int[] type_ids = new int[len];
        String[] names = new String[len];
        int[] prices = new int[len];
        int[] images = new int[len];

        int counter = 0;
        if (cursor.moveToFirst()) {
            do {
                ids[counter] = cursor.getInt(0);
                type_ids[counter] = cursor.getInt(1);
                names[counter] = cursor.getString(2);
                prices[counter] = cursor.getInt(3);
                images[counter] = cursor.getInt(4);
                counter++;
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        btn.setOnClickListener(v -> {
            Intent intent = new Intent(btn.getContext(), ItemListActivity.class);
            intent.putExtra("TITLE", itemTypes.get(position).getName());
            intent.putExtra("IDS", ids);
            intent.putExtra("TYPE_IDS", type_ids);
            intent.putExtra("NAMES", names);
            intent.putExtra("PRICES", prices);
            intent.putExtra("IMAGES", images);
            btn.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return itemTypes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final FrameLayout layout;
        private final Button btn;

        public ViewHolder(@NonNull FrameLayout frameLayout) {
            super(frameLayout);
            layout = frameLayout;
            btn = (Button) frameLayout.getChildAt(0);
        }
    }
}
