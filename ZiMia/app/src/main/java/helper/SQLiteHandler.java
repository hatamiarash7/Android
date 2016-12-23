package helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {
    private static final String TAG = SQLiteHandler.class.getSimpleName();
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "android_api";
    // Login table name
    private static final String TABLE_LOGIN = "login";
    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_UID = "uid";
    private static final String KEY_CREATED_AT = "created_at";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_ADDRESS + " TEXT," + KEY_PHONE + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_UID + " TEXT,"
                + KEY_CREATED_AT + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);
        Log.d(TAG, "Database tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
        // Create tables again
        onCreate(db);
    }

    public void addUser(String name, String email, String address, String phone, String uid, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();
        name = "'" + name + "'";
        email = "'" + email + "'";
        address = "'" + address + "'";
        phone = "'" + phone + "'";
        created_at = "'" + created_at + "'";
        String query = "INSERT OR REPLACE INTO " + TABLE_LOGIN + "(" + KEY_NAME + ", " + KEY_ADDRESS + ", "
                + KEY_PHONE + ", " + KEY_EMAIL + ", " + KEY_UID + ", "
                + KEY_CREATED_AT + ") VALUES(" + name + ", " + address
                + ", " + phone + ", " + email + ", " + uid + ", " + created_at + ")";
        db.execSQL(query);
        db.close(); // Closing database connection
        Log.d(TAG, "New user inserted into sqlite");
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        Log.d(TAG, "Sqlite: " + String.valueOf(cursor.getCount()));
        if (cursor.getCount() > 0) {
            Log.d(TAG, "Sqlite2: " + cursor.getString(1));
            user.put("name", cursor.getString(1));
            user.put("address", cursor.getString(2));
            user.put("phone", cursor.getString(3));
            user.put("email", cursor.getString(4));
            user.put("uid", cursor.getString(5));
            user.put("created_at", cursor.getString(6));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());
        return user;
    }

    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();
        // return row count
        return rowCount;
    }

    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_LOGIN, null, null);
        db.close();
        Log.d(TAG, "Deleted all user info from sqlite");
    }
}