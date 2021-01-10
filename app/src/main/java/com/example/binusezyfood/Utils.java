package com.example.binusezyfood;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

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

}
