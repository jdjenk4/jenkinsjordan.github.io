package com.example.androidappenhancement.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 2;
    // Database Name
    private static final String DATABASE_NAME = "myAppDatabase.db";
    // Table Names
    private static final String TABLE_USERS = "users";
    // Common column names
    private static final String KEY_ID = "id";
    // USERS Table - column names
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD_HASH = "password_hash";
    private static final String KEY_SALT = "salt";
    // Table Create Statements
    // USERS table create statement
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_USERNAME + " TEXT UNIQUE,"
            + KEY_PASSWORD_HASH + " TEXT,"
            + KEY_SALT + " TEXT"
            + ")";
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
    public boolean addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Generate a random salt for the new user
        String salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);

        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, username);
        values.put(KEY_PASSWORD_HASH, hashedPassword);
        values.put(KEY_SALT, salt);

        try {
            long result = db.insertOrThrow(TABLE_USERS, null, values);
            return result != -1; //true if inserted successfully
        } catch (SQLException e) {
            // throw an exception
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }
    // Method to authenticate a user
    public boolean authenticateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT " + KEY_PASSWORD_HASH + ", " + KEY_SALT +
                " FROM " + TABLE_USERS + " WHERE " + KEY_USERNAME + " = ?";

        try (Cursor c = db.rawQuery(selectQuery, new String[]{username})) {
            if (c.moveToFirst()) {
                String storedHash = c.getString(c.getColumnIndexOrThrow(KEY_PASSWORD_HASH));
                String salt = c.getString(c.getColumnIndexOrThrow(KEY_SALT));

                // Hash the entered password using the stored salt
                String enteredHash = hashPassword(password, salt);
                return storedHash.equals(enteredHash);
            } else {
                return false; // user not found
            }
        } finally {
            db.close();
        }
    }
    // Generates a random secure salt
    private static String generateSalt() {
        byte[] saltBytes = new byte[16];
        new SecureRandom().nextBytes(saltBytes);
        return Base64.encodeToString(saltBytes, Base64.NO_WRAP);
    }
    // Hashes the password with salt utilizing SHA-256
    private static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] hashed = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeToString(hashed, Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}