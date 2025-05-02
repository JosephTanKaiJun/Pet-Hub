package com.example.pethub.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.pethub.chat.Booking;
import com.example.pethub.chat.Message;
import com.example.pethub.chat.Sitter;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PetHub.db";
    private static final int DATABASE_VERSION = 16; // Incremented from 15

    // Users table
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_STUDENT_ID = "student_id";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_IS_SITTER = "is_sitter";
    public static final String COLUMN_PHONE_NUMBER = "phone_number";
    public static final String COLUMN_PHOTO_URI = "PHOTO_URI";

    // Sitters table
    private static final String TABLE_SITTERS = "sitters";
    public static final String COLUMN_BIO = "bio";
    public static final String COLUMN_CARE_PETS = "care_pets";
    public static final String COLUMN_CARE_PLANTS = "care_plants";
    public static final String COLUMN_PET_RATE = "pet_rate";
    public static final String COLUMN_PLANT_RATE = "plant_rate";
    public static final String COLUMN_INCOME = "income";
    public static final String COLUMN_SKILLS = "skills";

    // Messages table
    private static final String TABLE_MESSAGES = "messages";
    private static final String COLUMN_MESSAGE_ID = "message_id";
    private static final String COLUMN_SITTER_ID = "sitter_id";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_IS_USER = "is_user";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    // Owners table
    public static final String TABLE_OWNERS = "owners";
    public static final String COLUMN_OWNER_BIO = "bio";
    public static final String COLUMN_OWNER_CARE_PETS = "care_pets";
    public static final String COLUMN_OWNER_CARE_PLANTS = "care_plants";

    // Hiring requests table
    private static final String TABLE_HIRING_REQUESTS = "hiring_requests";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT UNIQUE,"
                + COLUMN_STUDENT_ID + " TEXT,"
                + COLUMN_EMAIL + " TEXT UNIQUE,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_PHONE_NUMBER + " TEXT,"
                + COLUMN_PHOTO_URI + " TEXT,"
                + COLUMN_IS_SITTER + " INTEGER DEFAULT 0" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_SITTERS_TABLE = "CREATE TABLE " + TABLE_SITTERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_BIO + " TEXT,"
                + COLUMN_CARE_PETS + " INTEGER,"
                + COLUMN_CARE_PLANTS + " INTEGER,"
                + COLUMN_PET_RATE + " REAL,"
                + COLUMN_PLANT_RATE + " REAL,"
                + COLUMN_INCOME + " REAL DEFAULT 0,"
                + COLUMN_SKILLS + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";
        db.execSQL(CREATE_SITTERS_TABLE);

        String CREATE_OWNERS_TABLE = "CREATE TABLE " + TABLE_OWNERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_OWNER_BIO + " TEXT,"
                + COLUMN_OWNER_CARE_PETS + " INTEGER,"
                + COLUMN_OWNER_CARE_PLANTS + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";
        db.execSQL(CREATE_OWNERS_TABLE);

        db.execSQL("CREATE TABLE " + TABLE_MESSAGES + "("
                + COLUMN_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_ID + " INTEGER,"
                + COLUMN_SITTER_ID + " INTEGER,"
                + COLUMN_CONTENT + " TEXT,"
                + COLUMN_IS_USER + " INTEGER,"
                + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))");

        db.execSQL("CREATE TABLE " + TABLE_HIRING_REQUESTS + "("
                + "request_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_ID + " INTEGER,"
                + "sitter_id INTEGER,"
                + "date TEXT,"
                + "pet_type TEXT,"
                + "species TEXT,"
                + "remarks TEXT,"
                + "status TEXT DEFAULT 'pending',"
                + "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))");

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, 1);
        values.put(COLUMN_USERNAME, "Test User");
        values.put(COLUMN_STUDENT_ID, "2105034");
        values.put(COLUMN_EMAIL, "test@example.com");
        values.put(COLUMN_PHONE_NUMBER, "0123456789");
        values.put(COLUMN_IS_SITTER, 0);
        db.insert(TABLE_USERS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop all tables and recreate them
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HIRING_REQUESTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SITTERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OWNERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public void addSitter(int userId, String bio, boolean carePets, boolean carePlants, double petRate, double plantRate, String skills) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_BIO, bio);
        values.put(COLUMN_CARE_PETS, carePets ? 1 : 0);
        values.put(COLUMN_CARE_PLANTS, carePlants ? 1 : 0);
        values.put(COLUMN_PET_RATE, petRate);
        values.put(COLUMN_PLANT_RATE, plantRate);
        values.put(COLUMN_SKILLS, skills);
        values.put(COLUMN_INCOME, 0.0);

        Cursor cursor = db.query(TABLE_SITTERS, new String[]{COLUMN_USER_ID}, COLUMN_USER_ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, null);
        if (cursor.moveToFirst()) {
            db.update(TABLE_SITTERS, values, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});
        } else {
            db.insert(TABLE_SITTERS, null, values);
        }
        cursor.close();
        db.close();
    }

    public Cursor getAllSitters() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_SITTERS, null);
    }

    public boolean isUserSitter(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_IS_SITTER}, COLUMN_USER_ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, null);
        if (cursor.moveToFirst()) {
            int isSitter = cursor.getInt(0);
            cursor.close();
            return isSitter == 1;
        }
        cursor.close();
        return false;
    }

    public void setUserAsSitter(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_SITTER, 1);
        db.update(TABLE_USERS, values, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});
        db.close();
    }

    public Cursor getSitterDetails(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT u." + COLUMN_USERNAME + ", u." + COLUMN_STUDENT_ID + ", u." + COLUMN_EMAIL + ", u." + COLUMN_PHONE_NUMBER + ", u." + COLUMN_PHOTO_URI + ", " +
                        "s." + COLUMN_BIO + ", s." + COLUMN_CARE_PETS + ", s." + COLUMN_CARE_PLANTS + ", s." + COLUMN_PET_RATE + ", s." + COLUMN_PLANT_RATE +
                        ", s." + COLUMN_INCOME + ", s." + COLUMN_SKILLS +
                        " FROM " + TABLE_USERS + " u" +
                        " JOIN " + TABLE_SITTERS + " s ON u." + COLUMN_USER_ID + " = s." + COLUMN_USER_ID +
                        " WHERE u." + COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}
        );
    }

    public Sitter getSitterById(int sitterId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT u." + COLUMN_USERNAME + ", s." + COLUMN_BIO + ", s." + COLUMN_SKILLS + ", u." + COLUMN_EMAIL + ", u." + COLUMN_PHOTO_URI + " " +
                        "FROM " + TABLE_USERS + " u " +
                        "JOIN " + TABLE_SITTERS + " s ON u." + COLUMN_USER_ID + " = s." + COLUMN_USER_ID + " " +
                        "WHERE u." + COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(sitterId)}
        );

        Sitter sitter = null;
        if (cursor.moveToFirst()) {
            sitter = new Sitter(
                    sitterId,
                    cursor.getString(0),  // name
                    cursor.getString(1),  // bio
                    cursor.getString(2),  // skills
                    cursor.getString(3),  // email
                    cursor.getString(4)   // photoUri
            );
        }
        cursor.close();
        db.close();
        return sitter;
    }

    public void updateSitterDetails(int userId, String username, String studentId, String email, String phoneNumber,
                                    String photoUri, String bio, boolean carePets, boolean carePlants,
                                    double petRate, double plantRate, String skills) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues userValues = new ContentValues();
        userValues.put(COLUMN_USERNAME, username);
        userValues.put(COLUMN_STUDENT_ID, studentId);
        userValues.put(COLUMN_EMAIL, email);
        userValues.put(COLUMN_PHONE_NUMBER, phoneNumber);
        userValues.put(COLUMN_PHOTO_URI, photoUri);
        db.update(TABLE_USERS, userValues, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});

        ContentValues sitterValues = new ContentValues();
        sitterValues.put(COLUMN_BIO, bio);
        sitterValues.put(COLUMN_CARE_PETS, carePets ? 1 : 0);
        sitterValues.put(COLUMN_CARE_PLANTS, carePlants ? 1 : 0);
        sitterValues.put(COLUMN_PET_RATE, petRate);
        sitterValues.put(COLUMN_PLANT_RATE, plantRate);
        sitterValues.put(COLUMN_SKILLS, skills);
        db.update(TABLE_SITTERS, sitterValues, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});

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
        Cursor cursor = db.rawQuery(
                "SELECT hr.*, u.username as sitter_name, u." + COLUMN_PHOTO_URI + " as photoUri FROM " + TABLE_HIRING_REQUESTS + " hr " +
                        "JOIN " + TABLE_USERS + " u ON hr.sitter_id = u.user_id " +
                        "WHERE hr.user_id = ? " +
                        "ORDER BY hr.date ASC",
                new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
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
                        cursor.getString(cursor.getColumnIndexOrThrow("photoUri"))
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

    ///Owner functions
    public void addOwner(int userId, String bio, boolean carePets, boolean carePlants, String photoUri) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Update user's photo URI if provided
        if (photoUri != null) {
            ContentValues userValues = new ContentValues();
            userValues.put(COLUMN_PHOTO_URI, photoUri);
            db.update(TABLE_USERS, userValues, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_OWNER_BIO, bio);
        values.put(COLUMN_OWNER_CARE_PETS, carePets ? 1 : 0);
        values.put(COLUMN_OWNER_CARE_PLANTS, carePlants ? 1 : 0);

        Cursor cursor = db.query(TABLE_OWNERS, new String[]{COLUMN_USER_ID}, COLUMN_USER_ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, null);
        if (cursor.moveToFirst()) {
            db.update(TABLE_OWNERS, values, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});
        } else {
            db.insert(TABLE_OWNERS, null, values);
        }
        cursor.close();
        db.close();
    }

    public Cursor getOwnerDetails(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT u." + COLUMN_USERNAME + ", u." + COLUMN_STUDENT_ID + ", u." + COLUMN_EMAIL +
                        ", u." + COLUMN_PHONE_NUMBER + ", u." + COLUMN_PHOTO_URI +
                        ", o." + COLUMN_OWNER_BIO + ", o." + COLUMN_OWNER_CARE_PETS +
                        ", o." + COLUMN_OWNER_CARE_PLANTS +
                        " FROM " + TABLE_USERS + " u" +
                        " LEFT JOIN " + TABLE_OWNERS + " o ON u." + COLUMN_USER_ID + " = o." + COLUMN_USER_ID +
                        " WHERE u." + COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}
        );
    }


    public boolean updateOwnerDetails(int userId, String username, String studentId, String email,
                                      String phoneNumber, String photoUri, String bio,
                                      boolean carePets, boolean carePlants) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        boolean success = false;

        try {
            // Update user table
            ContentValues userValues = new ContentValues();
            userValues.put(COLUMN_USERNAME, username);
            userValues.put(COLUMN_STUDENT_ID, studentId);
            userValues.put(COLUMN_EMAIL, email);
            userValues.put(COLUMN_PHONE_NUMBER, phoneNumber);
            if (photoUri != null) {
                userValues.put(COLUMN_PHOTO_URI, photoUri);
            }

            int userRows = db.update(TABLE_USERS, userValues,
                    COLUMN_USER_ID + "=?",
                    new String[]{String.valueOf(userId)});

            if (userRows <= 0) {
                throw new Exception("Failed to update user record");
            }

            // Update owner table
            ContentValues ownerValues = new ContentValues();
            ownerValues.put(COLUMN_OWNER_BIO, bio);
            ownerValues.put(COLUMN_OWNER_CARE_PETS, carePets ? 1 : 0);
            ownerValues.put(COLUMN_OWNER_CARE_PLANTS, carePlants ? 1 : 0);

            // Check if owner record exists
            Cursor cursor = db.query(TABLE_OWNERS,
                    new String[]{COLUMN_USER_ID},
                    COLUMN_USER_ID + "=?",
                    new String[]{String.valueOf(userId)}, null, null, null);

            if (cursor.moveToFirst()) {
                // Update existing
                int ownerRows = db.update(TABLE_OWNERS, ownerValues,
                        COLUMN_USER_ID + "=?",
                        new String[]{String.valueOf(userId)});
                if (ownerRows <= 0) {
                    throw new Exception("Failed to update owner record");
                }
            } else {
                // Insert new
                ownerValues.put(COLUMN_USER_ID, userId);
                long result = db.insert(TABLE_OWNERS, null, ownerValues);
                if (result == -1) {
                    throw new Exception("Failed to insert owner record");
                }
            }
            cursor.close();

            db.setTransactionSuccessful();
            success = true;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error updating owner details", e);
        } finally {
            db.endTransaction();
            db.close();
        }
        return success;
    }

    public boolean insertUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public boolean checkCredentials(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE username = ? AND password = ?",
                new String[]{username, password});
        boolean valid = cursor.getCount() > 0;
        cursor.close();
        return valid;
    }

    public boolean checkUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE username = ?",
                new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public int getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT user_id FROM " + TABLE_USERS + " WHERE username = ?",
                new String[]{username});
        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0);
        }
        cursor.close();
        return userId;
    }
}