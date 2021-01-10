package com.example.binusezyfood;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

public class ItemListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Intent intent = getIntent();

        int[] ids = intent.getIntArrayExtra("IDS");
        int[] type_ids = intent.getIntArrayExtra("TYPE_IDS");
        String[] names = intent.getStringArrayExtra("NAMES");
        int[] prices = intent.getIntArrayExtra("PRICES");
        int[] images = intent.getIntArrayExtra("IMAGES");

        Cursor cursor = Utils.getDb(this).query("ITEM_TYPES", new String[]{"NAME"}, "_id = ?", new String[]{String.valueOf(type_ids[0])}, null, null, null);

        if (cursor.moveToFirst()) {
            setTitle(cursor.getString(0));
        }

        cursor.close();

        RecyclerView recyclerView = findViewById(R.id.item_recycler);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter adapter = new ItemsAdapter(ids, type_ids, names, prices, images);
        recyclerView.setAdapter(adapter);

    }
}