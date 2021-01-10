package com.example.binusezyfood;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
    public static String getTimeNow() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd 'at' HH:mm:ss");
        String currentDateAndTime = sdf.format(new Date());

        return currentDateAndTime;
    }

    public static void hideKeyboard(AppCompatActivity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private static final NumberFormat ID_FORMAT = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));

    public static String toRupiah(int number) {
        ID_FORMAT.setMaximumFractionDigits(0);
        return ID_FORMAT.format(number);
    }

    public static SQLiteDatabase getDb(Context context) {
        return new DBHelper(context).getReadableDatabase();
    }
}
