package com.example.binusezyfood;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class TopUpActivity extends AppCompatActivity {

    private TextView balanceText;
    private int currentBalance;
    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);

        TextView nameText = findViewById(R.id.topupNameText);
        balanceText = findViewById(R.id.topupBalanceText);

        dbHelper = new DBHelper(this);
        db = getDb(dbHelper, db);
        Cursor cursor = db.query("USERS", new String[]{"NAME", "BALANCE"}, "_id = ?", new String[]{"1"}, null, null, null);

        if (cursor.moveToFirst()) {
            nameText.setText(cursor.getString(0));
            currentBalance = cursor.getInt(1);
        }

        cursor.close();
        db.close();

        balanceText.setText(String.valueOf(currentBalance));
    }

    public SQLiteDatabase getDb(SQLiteOpenHelper dbHelper, SQLiteDatabase db) {
        return dbHelper.getReadableDatabase();
    }

    private void updateBalance(int newBalance) {
        db = getDb(dbHelper, db);

        ContentValues contentValues = new ContentValues();
        contentValues.put("BALANCE", newBalance);
        db.update("USERS", contentValues,"_id = ?", new String[] {"1"});
        db.close();
    }

    public void topUp20(View view) {
        currentBalance += 20000;
        balanceText.setText(String.valueOf(currentBalance));
        updateBalance(currentBalance);
    }

    public void topUp50(View view) {
        currentBalance += 50000;
        balanceText.setText(String.valueOf(currentBalance));
        updateBalance(currentBalance);
    }

    public void topUp100(View view) {
        currentBalance += 100000;
        balanceText.setText(String.valueOf(currentBalance));
        updateBalance(currentBalance);
    }

}