package com.zybooks.cs360projectthree;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "myAppDatabase.db";
    // Table Names
    private static final String TABLE_USERS = "users";
    // Common column names
    private static final String KEY_ID = "id";
    // USERS Table - column names
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    // Table Create Statements
    // USERS table create statement
    private static final String CREATE_TABLE_USERS = "CREATE TABLE "
            + TABLE_USERS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_USERNAME
            + " TEXT," + KEY_PASSWORD + " TEXT" + ")";
    // DATA table create statement
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_USERS);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        // create new tables
        onCreate(db);
    }
    // Method to add a new user
    public void addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, username);
        values.put(KEY_PASSWORD, password);
        // insert row
        db.close(); // Closing database connection
    }
    // Method to authenticate a user
    public boolean authenticateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_USERS + " WHERE "
                + KEY_USERNAME + " = '" + username + "' AND " + KEY_PASSWORD + " = '" + password + "'";
        try (Cursor c = db.rawQuery(selectQuery, null)) {
            // Check if cursor has any data
            //if cursor has data then in user database there is a user associated with this given username so return true
            return c.moveToFirst() && c.getCount() > 0;
        }
        //if username and password does not match any of the entries in the database return false
    }
}