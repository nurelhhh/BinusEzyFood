package com.example.binusezyfood;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Arrays;

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

        TextView title = findViewById(R.id.itemListTitleText);
        title.setText(Arrays.toString(names));

        RecyclerView recyclerView = findViewById(R.id.item_recycler);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter adapter = new ItemsAdapter(ids, type_ids, names, prices, images);
        recyclerView.setAdapter(adapter);

    }
}