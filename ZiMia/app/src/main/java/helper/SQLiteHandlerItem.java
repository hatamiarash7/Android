package helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SQLiteHandlerItem extends SQLiteOpenHelper {
    private static final String TAG = SQLiteHandlerItem.class.getSimpleName();
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "android_api";
    // Login table name
    private static final String TABLE_CARD = "card";
    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PRICE = "price";
    private static final String KEY_COUNT = "count";

    public SQLiteHandlerItem(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_CARD + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_PRICE + " TEXT," + KEY_COUNT + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);
        Log.d(TAG, "Database table created-onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARD);
        // Create tables again
        onCreate(db);
    }

    public void CreateTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARD);
        String CREATE_CARD_TABLE = "CREATE TABLE " + TABLE_CARD + "("
                + KEY_ID + " INTEGER PRIMARY KEY, " + KEY_NAME + " TEXT, "
                + KEY_PRICE + " TEXT, " + KEY_COUNT + " TEXT" + ")";
        db.execSQL(CREATE_CARD_TABLE);
        db.close();
        Log.d(TAG, "Database table created-Manual");
    }

    public void addItem(String name, String price, String count) {
        SQLiteDatabase db = this.getWritableDatabase();
        name = "'" + name + "'";
        price = "'" + price + "'";
        count = "'" + count + "'";
        String query = "INSERT OR REPLACE INTO " + TABLE_CARD + "("
                + KEY_NAME + ", " + KEY_PRICE + ", " + KEY_COUNT
                + ") VALUES(" + name + ", " + price + ", " + count + ")";
        db.execSQL(query);
        db.close(); // Closing database connection
        Log.d(TAG, name + " inserted into database");
    }

    public void updateItem(String name, String price, String count) {
        SQLiteDatabase db = this.getWritableDatabase();
        name = "'" + name + "'";
        price = "'" + price + "'";
        count = "'" + count + "'";
        String query = "UPDATE " + TABLE_CARD + " SET "
                + KEY_NAME + "=" + name + ", " + KEY_PRICE + "=" + price
                + ", " + KEY_COUNT + "=" + count + " WHERE " + KEY_NAME + "=" + name;
        db.execSQL(query);
        db.close(); // Closing database connection
        Log.d(TAG, name + " updated");
    }

    public List<String> getItemDetails() {
        List<String> item2 = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CARD;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        Log.d(TAG, "database : " + String.valueOf(cursor.getCount()));
        if (cursor.getCount() > 0) {
            while (!cursor.isAfterLast()) {
                item2.add(cursor.getString(0));
                item2.add(cursor.getString(1));
                item2.add(cursor.getString(2));
                item2.add(cursor.getString(3));
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching item from Sqlite: " + item2.toString());
        return item2;
    }

    public int TotalPrice() {
        int total = 0;
        String selectQuery = "SELECT * FROM " + TABLE_CARD;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        Log.d(TAG, "Sqlite: " + String.valueOf(cursor.getCount()));
        if (cursor.getCount() > 0) {
            while (!cursor.isAfterLast()) {
                Log.d(TAG, "Sqlite2: " + cursor.getString(1));
                total += Integer.parseInt(cursor.getString(2));
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Total Price : " + String.valueOf(total));
        return total;
    }

    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CARD;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();
        // return row count
        return rowCount;
    }

    public void deleteItem(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete Item's Row
        name = "'" + name + "'";
        db.delete(TABLE_CARD, KEY_NAME + "=" + name, null);
        db.close();
        Log.d(TAG, "Deleted " + name + " from sqlite");
    }

    public void deleteItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_CARD, null, null);
        db.close();
        CreateTable();
        Log.d(TAG, "Deleted all item info from sqlite");
    }
}