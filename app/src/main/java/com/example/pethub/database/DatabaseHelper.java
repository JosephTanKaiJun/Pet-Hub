package com.example.pethub.database;
import com.example.pethub.chat.Booking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;
import com.example.pethub.chat.Message;
import com.example.pethub.chat.Sitter;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "pethub.db";
    private static final int DATABASE_VERSION = 4;

    // Sitters table
    private static final String TABLE_SITTERS = "sitters";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_BIO = "bio";
    private static final String COLUMN_SKILLS = "skills";
    private static final String COLUMN_EMAIL = "email";

    // Messages table
    private static final String TABLE_MESSAGES = "messages";
    private static final String COLUMN_MESSAGE_ID = "message_id";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_SITTER_ID = "sitter_id";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_IS_USER = "is_user";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    // Hiring requests table
    private static final String TABLE_HIRING_REQUESTS = "hiring_requests";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_SITTERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_BIO + " TEXT,"
                + COLUMN_SKILLS + " TEXT,"
                + COLUMN_EMAIL + " TEXT,"
                + "PHOTO_RES_ID INTEGER)");

        db.execSQL("CREATE TABLE " + TABLE_MESSAGES + "("
                + COLUMN_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_ID + " INTEGER,"
                + COLUMN_SITTER_ID + " INTEGER,"
                + COLUMN_CONTENT + " TEXT,"
                + COLUMN_IS_USER + " INTEGER,"
                + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP)");

        db.execSQL("CREATE TABLE " + TABLE_HIRING_REQUESTS + "("
                + "request_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "user_id INTEGER,"
                + "sitter_id INTEGER,"
                + "date TEXT,"
                + "pet_type TEXT,"
                + "species TEXT,"
                + "remarks TEXT,"
                + "status TEXT DEFAULT 'pending',"
                + "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            db.execSQL("CREATE TABLE " + TABLE_HIRING_REQUESTS + "("
                    + "request_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "user_id INTEGER,"
                    + "sitter_id INTEGER,"
                    + "date TEXT,"
                    + "pet_type TEXT,"
                    + "species TEXT,"
                    + "remarks TEXT,"
                    + "status TEXT DEFAULT 'pending',"
                    + "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)");
        } else {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SITTERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_HIRING_REQUESTS);
            onCreate(db);
        }
    }

    public void addSitter(String name, String bio, String skills, String email, int photoResId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("NAME", name);
        values.put("BIO", bio);
        values.put("SKILLS", skills);
        values.put("EMAIL", email);
        values.put("PHOTO_RES_ID", photoResId);
        db.insert("SITTERS", null, values);
        db.close();
    }

    public Cursor getAllSitters() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_SITTERS, null);
    }

    public Sitter getSitterById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SITTERS, null, COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Sitter sitter = new Sitter(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BIO)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SKILLS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    cursor.getInt(cursor.getColumnIndexOrThrow("PHOTO_RES_ID"))
            );
            cursor.close();
            return sitter;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    public void addMessage(int userId, int sitterId, String content, boolean isUser) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_SITTER_ID, sitterId);
        values.put(COLUMN_CONTENT, content);
        values.put(COLUMN_IS_USER, isUser ? 1 : 0);
        db.insert(TABLE_MESSAGES, null, values);
        db.close();
    }

    public int getSittersCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_SITTERS, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public List<Message> getMessages(int userId, int sitterId) {
        List<Message> messages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MESSAGES +
                        " WHERE " + COLUMN_USER_ID + " = ? AND " + COLUMN_SITTER_ID + " = ?" +
                        " ORDER BY " + COLUMN_TIMESTAMP + " ASC",
                new String[]{String.valueOf(userId), String.valueOf(sitterId)});

        if (cursor.moveToFirst()) {
            do {
                Message message = new Message(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SITTER_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_USER)) == 1,
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP))
                );
                messages.add(message);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return messages;
    }

    public void addHiringRequest(int userId, int sitterId, String date, String petType, String species, String remarks) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("sitter_id", sitterId);
        values.put("date", date);
        values.put("pet_type", petType);
        values.put("species", species);
        values.put("remarks", remarks);
        db.insert(TABLE_HIRING_REQUESTS, null, values);
        db.close();
    }

    public List<Booking> getBookingsForUser(int userId) {
        List<Booking> bookings = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // Update query to include PHOTO_RES_ID
        Cursor cursor = db.rawQuery("SELECT hr.*, s.name as sitter_name, s.PHOTO_RES_ID as photoResId FROM " + TABLE_HIRING_REQUESTS + " hr " +
                        "JOIN " + TABLE_SITTERS + " s ON hr.sitter_id = s.id " +
                        "WHERE hr.user_id = ? " +
                        "ORDER BY hr.date ASC",
                new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                // Extract photoResId from cursor
                int photoResId = cursor.getInt(cursor.getColumnIndexOrThrow("photoResId"));
                // Pass photoResId to Booking constructor
                Booking booking = new Booking(
                        cursor.getInt(cursor.getColumnIndexOrThrow("request_id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("sitter_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("sitter_name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("date")),
                        cursor.getString(cursor.getColumnIndexOrThrow("pet_type")),
                        cursor.getString(cursor.getColumnIndexOrThrow("species")),
                        cursor.getString(cursor.getColumnIndexOrThrow("remarks")),
                        cursor.getString(cursor.getColumnIndexOrThrow("status")),
                        cursor.getString(cursor.getColumnIndexOrThrow("timestamp")),
                        photoResId // Add this line
                );
                bookings.add(booking);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return bookings;
    }

    public boolean hasBookingOnDate(int userId, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_HIRING_REQUESTS +
                        " WHERE user_id = ? AND date = ?",
                new String[]{String.valueOf(userId), date});
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count > 0;
    }
}