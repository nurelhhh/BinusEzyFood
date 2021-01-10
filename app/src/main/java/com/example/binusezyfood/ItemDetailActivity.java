package com.example.binusezyfood;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ItemDetailActivity extends AppCompatActivity {

    private int currentQty = 0;
    private TextView quantityText;
    private int itemId;
    private int itemStock = -1;
    private int itemPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        Intent intent = getIntent();

        setTitle(intent.getStringExtra("NAME") + " Detail");

        itemId = intent.getIntExtra("ID", -1);

        ImageView imageView = findViewById(R.id.imageDetail);
        TextView nameText = findViewById(R.id.nameDetailText);
        TextView stockText = findViewById(R.id.stockDetailText);
        TextView priceText = findViewById(R.id.priceDetailText);
        quantityText = findViewById(R.id.quantityDetailText);

        imageView.setImageResource(intent.getIntExtra("IMAGE", -1));
        nameText.setText(intent.getStringExtra("NAME"));

        Cursor cursor = Utils.getDb(this).query("ITEM_STOCKS", new String[]{"ITEM_ID", "STOCK"}, "ITEM_ID = ? AND REST_ID = ?", new String[]{String.valueOf(itemId), String.valueOf(MainActivity.REST_ID)}, null, null, null, null);

        if (cursor.moveToFirst()) {
            itemStock = cursor.getInt(1);
        }

        cursor.close();

        stockText.setText("Stock: " + itemStock);

        itemPrice = intent.getIntExtra("PRICE", -1);
        priceText.setText(Utils.toRupiah(itemPrice));
        quantityText.setText(String.valueOf(currentQty));
    }

    public void decQty(View view) {
        if (currentQty == 1 || currentQty == 0) {
            Toast.makeText(this, "Cannot decrease more", Toast.LENGTH_SHORT).show();
            return;
        }
        currentQty--;
        quantityText.setText(String.valueOf(currentQty));
    }

    public void incQty(View view) {
        if (currentQty == itemStock) {
            Toast.makeText(this, "Cannot buy more than available stock", Toast.LENGTH_SHORT).show();
            return;
        }
        currentQty++;
        quantityText.setText(String.valueOf(currentQty));
    }

    public void addToCart(View view) {


        if (currentQty == 0) {
            Toast.makeText(this, "This item stock is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteOpenHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("ITEM_ID", itemId);
        contentValues.put("QUANTITY", currentQty);
        contentValues.put("SUBTOTAL_PRICE", currentQty * itemPrice);

        db.insert("CARTS", null, contentValues);
        db.close();

        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }
}