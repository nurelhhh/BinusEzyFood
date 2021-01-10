package com.example.binusezyfood;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.binusezyfood.DataClasses.ItemCart;
import com.google.android.material.snackbar.Snackbar;

import java.util.Vector;

public class CartActivity extends AppCompatActivity implements Communicators {

    private TextView balanceText;
    private int userBalance;
    private TextView totalPriceText;
    private int totalPrice = 0;
    private Vector<ItemCart> itemCarts;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        setTitle("Carts");

        balanceText = findViewById(R.id.cartBalanceText);
        totalPriceText = findViewById(R.id.cartTotalPriceText);

        db = Utils.getDb(this);

        Cursor cursor = db.query("CARTS", new String[]{"_id", "ITEM_ID", "QUANTITY", "SUBTOTAL_PRICE"}, null, null, null, null, null);

        itemCarts = new Vector<>();

        if (cursor.moveToFirst()) {
            do {
                itemCarts.add(new ItemCart(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getInt(2),
                        cursor.getInt(3)
                ));

                totalPrice += cursor.getInt(3);
            } while (cursor.moveToNext());
        }

        getUserBalance();

        cursor.close();
        
        totalPriceText.setText(Utils.toRupiah(totalPrice));

        RecyclerView recyclerView = findViewById(R.id.cartRecycler);
        recyclerView.setHasFixedSize(false);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter adapter = new CartAdapter(this, itemCarts);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserBalance();
    }

    private void getUserBalance() {
        Cursor cursor = db.query("USERS", new String[]{"_id", "BALANCE"}, "_id = ?", new String[]{"1"}, null, null, null);

        if (cursor.moveToFirst()) {
            userBalance = cursor.getInt(1);
            balanceText.setText(Utils.toRupiah(userBalance));
        }

        cursor.close();
    }

    @Override
    public void refreshTotalPrice(int subtractedPrice, int previousItemPosition) {
        totalPrice -= subtractedPrice;
        totalPriceText.setText(Utils.toRupiah(totalPrice));
    }

    public void onPay(View view) {
        
        if (userBalance < totalPrice) {
            Snackbar.make(view, "Your balance is less", Snackbar.LENGTH_SHORT)
                    .setAction("Top Up", v -> {
                        Intent intent = new Intent(this, TopUpActivity.class);
                        startActivity(intent);
                    }).show();
            return;
        }
        
        ContentValues transValues = new ContentValues();
        transValues.put("REST_ID", MainActivity.REST_ID);
        transValues.put("USER_ID", 1);
        transValues.put("TOTAL_PRICE", totalPrice);
        transValues.put("DATE", Utils.getTimeNow());
        transValues.put("RECEIVER_ADDRESS", MainActivity.RECEIVER_ADDRESS);
        db.insert("TRANSACTIONS", null, transValues);

        int transId = -1;
        Cursor cursor = db.rawQuery("SELECT * FROM TRANSACTIONS ORDER BY _id  DESC LIMIT 1", null);
        if (cursor.moveToFirst()) {
            transId = cursor.getInt(0);
        }
        cursor.close();

        ContentValues itemTransactionValues = new ContentValues();
        for (ItemCart item: itemCarts) {
            itemTransactionValues.clear();
            itemTransactionValues.put("TRANSACTION_ID", transId);
            itemTransactionValues.put("ITEM_ID", item.getItem_id());
            itemTransactionValues.put("QUANTITY", item.getQuantity());
            itemTransactionValues.put("SUBTOTAL_PRICE", item.getSubtotal_price());
            db.insert("ITEM_TRANSACTIONS", null, itemTransactionValues);
        }

        db.delete("CARTS", null, null);

        int newBalance = userBalance - totalPrice;
        itemTransactionValues.clear();
        itemTransactionValues.put("BALANCE", newBalance);
        db.update("USERS", itemTransactionValues, "_id = ?", new String[] {"1"});

        Intent intent = new Intent(this, TransactionActivity.class);
        startActivity(intent);
        finish();
    }
}