/*
 * Copyright (c) 2017 - All Rights Reserved - Arash Hatami
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
    private static final String KEY_PHONE = "phone";
    private static final String KEY_UID = "uid";
    private static final String KEY_TYPE = "type";
    private static final String KEY_AGE = "age";
    private static final String KEY_SEX = "sex";

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
                + KEY_PHONE + " TEXT, "
                + KEY_EMAIL + " TEXT UNIQUE, "
                + KEY_TYPE + " TEXT, "
                + KEY_AGE + " TEXT, "
                + KEY_SEX + " TEXT"
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
                + KEY_PHONE + " TEXT, "
                + KEY_EMAIL + " TEXT UNIQUE, "
                + KEY_TYPE + " TEXT, "
                + KEY_AGE + " TEXT, "
                + KEY_SEX + " TEXT"
                + ")";
        db.execSQL(Query);
        db.close();
        Log.d(TAG, "Database table created - Manual");
    }

    // add user data to database
    public void addUser(String uid, String name, String email, String phone, String type, String age, String sex) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_UID, uid);
        values.put(KEY_NAME, name);
        values.put(KEY_EMAIL, email);
        values.put(KEY_PHONE, phone);
        values.put(KEY_TYPE, type);
        values.put(KEY_AGE, age);
        values.put(KEY_SEX, sex);
        // Inserting Row
        long id = db.insert(TABLE_LOGIN, null, values);
        //db.execSQL(query);
        db.close();
        Log.d(TAG, "New user inserted into sqlite");
    }

    // get user details from database and send them
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();
        String Query = "SELECT * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(Query, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("uid", cursor.getString(1));
            user.put("name", cursor.getString(2));
            user.put("email", cursor.getString(3));
            user.put("phone", cursor.getString(4));
            user.put("type", cursor.getString(5));
            user.put("age", cursor.getString(6));
            user.put("sex", cursor.getString(7));
        }
        cursor.close();
        db.close();
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());
        return user;
    }

    public void updateUser(String name, String email, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        name = "'" + name + "'";
        email = "'" + email + "'";
        phone = "'" + phone + "'";
        String Query = "UPDATE " + TABLE_LOGIN + " SET "
                + KEY_NAME + "=" + name + ", "
                + KEY_PHONE + "=" + phone + " WHERE "
                + KEY_EMAIL + "=" + email;
        db.execSQL(Query);
        db.close();
        Log.d(TAG, "Row Updated");
    }

    // count all users
    public int getRowCount() {
        String Query = "SELECT  * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(Query, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();
        return rowCount;
    }

    // delete all users
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOGIN, null, null);
        db.close();
        Log.d(TAG, "Deleted all user info from sqlite");
    }
}