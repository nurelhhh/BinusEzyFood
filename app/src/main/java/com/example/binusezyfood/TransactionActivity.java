package com.example.binusezyfood;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.widget.TextView;

public class TransactionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        TextView titleText = findViewById(R.id.transaction_title_text);
        titleText.setText("Your transaction history");

        SQLiteOpenHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("" +
                "SELECT _id, REST_ID, TOTAL_PRICE, DATE, RECEIVER_ADDRESS " +
                "FROM TRANSACTIONS " +
                "WHERE USER_ID = 1 " +
                "ORDER BY _id DESC;", null);

        int len = cursor.getCount();
        int[] ids = new int[len] ;
        int[] rest_ids = new int[len];
        int[] total_prices = new int[len];
        String[] dates = new String[len];
        String[] receiver_addresses = new String[len];

        int counter = 0;
        if (cursor.moveToFirst()) {
            do {
                ids[counter] = cursor.getInt(0);
                rest_ids[counter] = cursor.getInt(1);
                total_prices[counter] = cursor.getInt(2);
                dates[counter] = cursor.getString(3);
                receiver_addresses[counter] = cursor.getString(4);
                counter++;
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        RecyclerView recyclerView = findViewById(R.id.transaction_recycler);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter adapter = new TransactionAdapter(ids, rest_ids, total_prices, dates, receiver_addresses);
        recyclerView.setAdapter(adapter);
    }
}