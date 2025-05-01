package com.example.pethub.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.example.pethub.chat.Message;
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "pethub.db";
    private static final int DATABASE_VERSION = 2;

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

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Sitters table
        db.execSQL("CREATE TABLE " + TABLE_SITTERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_BIO + " TEXT,"
                + COLUMN_SKILLS + " TEXT,"
                + COLUMN_EMAIL + " TEXT)");

        // Messages table
        db.execSQL("CREATE TABLE " + TABLE_MESSAGES + "("
                + COLUMN_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_ID + " INTEGER,"
                + COLUMN_SITTER_ID + " INTEGER,"
                + COLUMN_CONTENT + " TEXT,"
                + COLUMN_IS_USER + " INTEGER,"
                + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SITTERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        onCreate(db);
    }

    // Sitter methods
    public void addSitter(String name, String bio, String skills, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_BIO, bio);
        values.put(COLUMN_SKILLS, skills);
        values.put(COLUMN_EMAIL, email);
        db.insert(TABLE_SITTERS, null, values);
        db.close();
    }

    public Cursor getAllSitters() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_SITTERS, null);
    }

    // Message methods
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

    public List<com.example.pethub.chat.Message> getMessages(int userId, int sitterId) {
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
}