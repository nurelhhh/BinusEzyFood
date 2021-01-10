package com.example.binusezyfood;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "binus_ezy_food";
    private static final int DB_VERSION = 3;

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateDatabase(db, 0, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateDatabase(db, oldVersion, newVersion);
    }

    private void updateDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 1) {
            db.execSQL("CREATE TABLE ITEM_TYPES (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "NAME TEXT," +
                    "IMAGE INTEGER" +
                    ");");

            db.execSQL("CREATE TABLE ITEMS (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "NAME TEXT," +
                    "TYPE_ID TEXT," +
                    "PRICE INTEGER," +
                    "IMAGE INTEGER" +
                    ");");

            db.execSQL("CREATE TABLE RESTAURANTS (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "NAME TEXT," +
                    "ADDRESS TEXT," +
                    "LATITUDE TEXT," +
                    "LONGITUDE TEXT" +
                    ");");

            db.execSQL("CREATE TABLE USERS (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "NAME TEXT," +
                    "ADDRESS TEXT," +
                    "BALANCE INTEGER," +
                    "LATITUDE TEXT," +
                    "LONGITUDE TEXT" +
                    ");");

            db.execSQL("CREATE TABLE TRANSACTIONS (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "REST_ID INTEGER," +
                    "USER_ID INTEGER," +
                    "TOTAL_PRICE INTEGER," +
                    "DATE TEXT" +
                    ");");

            db.execSQL("CREATE TABLE ITEM_TRANSACTIONS (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "TRANSACTION_ID INTEGER," +
                    "ITEM_ID INTEGER," +
                    "QUANTITY INTEGER," +
                    "SUBTOTAL_PRICE INTEGER" +
                    ");");

            db.execSQL("CREATE TABLE ITEM_STOCKS (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "ITEM_ID INTEGER," +
                    "REST_ID INTEGER," +
                    "STOCK INTEGER" +
                    ");");

            insertDataToDatabase(db);

        }

        if (oldVersion < 2) {

            db.execSQL("CREATE TABLE CARTS (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "ITEM_ID INTEGER," +
                    "QUANTITY INTEGER," +
                    "SUBTOTAL_PRICE INTEGER" +
                    ");");

        }

        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE TRANSACTIONS ADD COLUMN RECEIVER_ADDRESS TEXT");
        }
    }

    private void insertDataToDatabase(SQLiteDatabase db) {
        insertItemType(db, "Drinks", R.drawable.ic_baseline_emoji_food_beverage_84);
        insertItemType(db, "Snacks", R.drawable.ic_baseline_fastfood_84);
        insertItemType(db, "Foods", R.drawable.ic_baseline_food_bank_84);

        insertItem(db, "Air Mineral", 2000, 1, R.drawable.air_mineral);
        insertItem(db, "Jus Mangga", 5000, 1, R.drawable.jus_mangga);
        insertItem(db, "Jus Jeruk", 4000, 1, R.drawable.jus_jeruk);
        insertItem(db, "Jus Alpukat", 5000, 1, R.drawable.jus_alpukat);
        insertItem(db, "Es Teh Manis", 3000, 1, R.drawable.es_teh_manis);
        insertItem(db, "Burger", 20000, 2, R.drawable.burger);
        insertItem(db, "Keripik Singkong", 10000, 2, R.drawable.keripik_singkong);
        insertItem(db, "Cokelat Batangan", 8000, 2, R.drawable.cokelat_batangan);
        insertItem(db, "Pizza", 45000, 3, R.drawable.pizza);
        insertItem(db, "Nasi Uduk", 14000, 3, R.drawable.nasi_uduk);
        insertItem(db, "Nasi Gundul", 18000, 3, R.drawable.nasi_gandul);
        insertItem(db, "Pecel Lele", 12000, 3, R.drawable.pecel_lele);

        insertRestaurant(db, "EzyFood Karawaci", "JL. Boulevard Diponegoro No.9999, Lippo Karawaci, Kota Tangerang", "-6.226117889216647", "106.61193738600512");
        insertRestaurant(db, "EzyFood BSD", "JL. BSD Raya Utama no.6666, BSD City, Tangerang Selatan", "-6.27676766969363", "106.63543190122913");
        insertRestaurant(db, "EzyFood Pondok Indah", "JL. Metro Pondok Indah no.1234, Jakarta Selatan", "-6.273497604514723", "106.7828208062674");

        insertUser(db, "Nurel Harsya", "JL. Vivaldi Raya no.7777, Summarecon Serpong, Kabupaten Tangerang", 12000, "-6.271219386241927", "106.6138377325065");

        insertItemStocks(db, 1, 1, 100);
        insertItemStocks(db, 2, 1, 0);
        insertItemStocks(db, 3, 1, 100);
        insertItemStocks(db, 4, 1, 100);
        insertItemStocks(db, 5, 1, 100);
        insertItemStocks(db, 6, 1, 100);
        insertItemStocks(db, 7, 1, 100);
        insertItemStocks(db, 8, 1, 100);
        insertItemStocks(db, 9, 1, 0);
        insertItemStocks(db, 10, 1, 100);
        insertItemStocks(db, 11, 1, 100);
        insertItemStocks(db, 12, 1, 100);

        insertItemStocks(db, 1, 2, 100);
        insertItemStocks(db, 2, 2, 100);
        insertItemStocks(db, 3, 2, 100);
        insertItemStocks(db, 4, 2, 100);
        insertItemStocks(db, 5, 2, 100);
        insertItemStocks(db, 6, 2, 0);
        insertItemStocks(db, 7, 2, 0);
        insertItemStocks(db, 8, 2, 0);
        insertItemStocks(db, 9, 2, 100);
        insertItemStocks(db, 10, 2, 100);
        insertItemStocks(db, 11, 2, 100);
        insertItemStocks(db, 12, 2, 0);

        insertItemStocks(db, 1, 3, 100);
        insertItemStocks(db, 2, 3, 0);
        insertItemStocks(db, 3, 3, 100);
        insertItemStocks(db, 4, 3, 0);
        insertItemStocks(db, 5, 3, 0);
        insertItemStocks(db, 6, 3, 100);
        insertItemStocks(db, 7, 3, 0);
        insertItemStocks(db, 8, 3, 100);
        insertItemStocks(db, 9, 3, 100);
        insertItemStocks(db, 10, 3, 0);
        insertItemStocks(db, 11, 3, 100);
        insertItemStocks(db, 12, 3, 0);
    }

    private void insertItemType(SQLiteDatabase db, String name, int image) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", name);
        contentValues.put("IMAGE", image);
        db.insert("ITEM_TYPES", null, contentValues);
    }

    private void insertItem(SQLiteDatabase db, String name, int price, int type_id, int image) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", name);
        contentValues.put("PRICE", price);
        contentValues.put("TYPE_ID", type_id);
        contentValues.put("IMAGE", image);
        db.insert("ITEMS", null, contentValues);
    }

    private void insertRestaurant(SQLiteDatabase db, String name, String address, String latitude, String longitude) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", name);
        contentValues.put("ADDRESS", address);
        contentValues.put("LONGITUDE", longitude);
        contentValues.put("LATITUDE", latitude);
        db.insert("RESTAURANTS", null, contentValues);
    }

    private void insertUser(SQLiteDatabase db, String name, String address, int balance, String latitude, String longitude) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", name);
        contentValues.put("ADDRESS", address);
        contentValues.put("BALANCE", balance);
        contentValues.put("LONGITUDE", longitude);
        contentValues.put("LATITUDE", latitude);
        db.insert("USERS", null, contentValues);
    }

    private void insertTransaction(SQLiteDatabase db, int rest_id, int user_id, int total_price, String date) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("REST_ID", rest_id);
        contentValues.put("USER_ID", user_id);
        contentValues.put("TOTAL_PRICE", total_price);
        contentValues.put("DATE", date);
        db.insert("TRANSACTIONS", null, contentValues);
    }

    private void insertItemTransaction(SQLiteDatabase db, int transaction_id, int item_id, int quantity, int subtotal_price) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("TRANSACTION_ID", transaction_id);
        contentValues.put("ITEM_ID", item_id);
        contentValues.put("QUANTITY", quantity);
        contentValues.put("SUBTOTAL_PRICE", subtotal_price);
        db.insert("ITEM_TRANSACTIONS", null, contentValues);
    }

    private void insertItemStocks(SQLiteDatabase db, int item_id, int rest_id, int stock) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("ITEM_ID", item_id);
        contentValues.put("REST_ID", rest_id);
        contentValues.put("STOCK", stock);
        db.insert("ITEM_STOCKS", null, contentValues);
    }
}
