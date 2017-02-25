/*
 * Copyright (c) 2017 - Arash Hatami - All Rights Reserved
 */

package helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {
    private static final String TAG = SQLiteHandler.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;             // Database Version
    private static final String DATABASE_NAME = "android_api"; // Database Name
    private static final String TABLE_LOGIN = "login";         // Login table name
    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";
    private static final String KEY_TYPE = "type";
    private static final String KEY_USERNAME = "username";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // create tables on call
    @Override
    public void onCreate(SQLiteDatabase db) {
        String Query = "CREATE TABLE " + TABLE_LOGIN + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_UID + " TEXT, "
                + KEY_NAME + " TEXT, "
                + KEY_EMAIL + " TEXT, "
                + KEY_USERNAME + " TEXT,"
                + KEY_TYPE + " TEXT"
                + ")";
        db.execSQL(Query);
        Log.d(TAG, "Database table created - onCreate");
    }

    // drop and recreate table
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
        onCreate(db);
    }

    // create table if onCreate can't do that
    public void CreateTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
        String Query = "CREATE TABLE " + TABLE_LOGIN + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_UID + " TEXT, "
                + KEY_NAME + " TEXT, "
                + KEY_EMAIL + " TEXT, "
                + KEY_USERNAME + " TEXT,"
                + KEY_TYPE + " TEXT"
                + ")";
        db.execSQL(Query);
        db.close();
        Log.d(TAG, "Database table created - Manual");
    }

    // add user data to database
    public void addUser(String uid, String name, String email, String username, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_UID, uid);
        values.put(KEY_NAME, name);
        values.put(KEY_EMAIL, email);
        values.put(KEY_USERNAME, username);
        values.put(KEY_TYPE, type);
        // Inserting Row
        long id = db.insert(TABLE_LOGIN, null, values);
        //db.execSQL(query);
        db.close();
        Log.d(TAG, "New user inserted into sqlite " + username);
    }

    // get user details from database and send them
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();
        String Query = "SELECT * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(Query, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            Log.d(TAG, "Sqlite: " + cursor.getString(1) + " " + cursor.getString(2));
            user.put("uid", cursor.getString(1));
            user.put("name", cursor.getString(2));
            user.put("email", cursor.getString(3));
            user.put("username", cursor.getString(4));
            user.put("type", cursor.getString(5));
        }
        cursor.close();
        db.close();
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());
        return user;
    }

    public void updateUser(String name, String email, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        name = "'" + name + "'";
        email = "'" + email + "'";
        String Query = "UPDATE " + TABLE_LOGIN + " SET "
                + KEY_NAME + "=" + name + ", "
                + KEY_EMAIL + "=" + email + " WHERE "
                + KEY_USERNAME + "=" + username;
        db.execSQL(Query);
        db.close();
        Log.d(TAG, "Row Updated");
    }

    // delete all users
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOGIN, null, null);
        db.close();
        Log.d(TAG, "Deleted all user info from sqlite");
    }

    public int GetCID(String type) {
        switch (type) {
            case "فرهنگی":
                return 1;
            case "بسیج دانشجویی":
                return 2;
            case "آموزشی":
                return 3;
            case "علمی پژوهشی":
                return 4;
            case "تغذیه":
                return 5;
            case "خوبگاه ها":
                return 6;
            case "حراست":
                return 7;
            case "نهاد رهبری":
                return 8;
            default:
                return 0;
        }
    }
}