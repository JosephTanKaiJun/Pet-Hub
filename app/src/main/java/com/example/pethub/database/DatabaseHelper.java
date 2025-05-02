package com.example.pethub.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PetHub.db";
    private static final int DATABASE_VERSION = 12; // Incremented due to schema change

    // Users table
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_STUDENT_ID = "student_id";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PHONE_NUMBER = "phone_number";
    private static final String COLUMN_IS_SITTER = "is_sitter";

    // Sitters table
    private static final String TABLE_SITTERS = "sitters";
    private static final String COLUMN_BIO = "bio";
    private static final String COLUMN_CARE_PETS = "care_pets";
    private static final String COLUMN_CARE_PLANTS = "care_plants";
    private static final String COLUMN_PET_RATE = "pet_rate";
    private static final String COLUMN_PLANT_RATE = "plant_rate";
    private static final String COLUMN_INCOME = "income";

    // Owners table
    private static final String TABLE_OWNERS = "owners";
    private static final String COLUMN_OWNER_BIO = "bio";
    private static final String COLUMN_OWNER_CARE_PETS = "care_pets";
    private static final String COLUMN_OWNER_CARE_PLANTS = "care_plants";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_STUDENT_ID + " TEXT,"
                + COLUMN_EMAIL + " TEXT,"
                + COLUMN_PHONE_NUMBER + " TEXT,"
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
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";
        db.execSQL(CREATE_SITTERS_TABLE);

        String CREATE_OWNERS_TABLE = "CREATE TABLE " + TABLE_OWNERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_OWNER_BIO + " TEXT,"
                + COLUMN_OWNER_CARE_PETS + " INTEGER,"
                + COLUMN_OWNER_CARE_PLANTS + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";
        db.execSQL(CREATE_OWNERS_TABLE);

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, 1);
        values.put(COLUMN_NAME, "Test User");
        values.put(COLUMN_STUDENT_ID, "2105034");
        values.put(COLUMN_EMAIL, "test@example.com");
        values.put(COLUMN_PHONE_NUMBER, "0123456789");
        values.put(COLUMN_IS_SITTER, 0);
        db.insert(TABLE_USERS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SITTERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OWNERS);
        onCreate(db);
    }

    // ------------------- SITTER METHODS -------------------

    public void addSitter(int userId, String bio, boolean carePets, boolean carePlants, double petRate, double plantRate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_BIO, bio);
        values.put(COLUMN_CARE_PETS, carePets ? 1 : 0);
        values.put(COLUMN_CARE_PLANTS, carePlants ? 1 : 0);
        values.put(COLUMN_PET_RATE, petRate);
        values.put(COLUMN_PLANT_RATE, plantRate);
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
                "SELECT u." + COLUMN_NAME + ", u." + COLUMN_STUDENT_ID + ", u." + COLUMN_EMAIL + ", u." + COLUMN_PHONE_NUMBER +
                        ", s." + COLUMN_BIO + ", s." + COLUMN_CARE_PETS + ", s." + COLUMN_CARE_PLANTS +
                        ", s." + COLUMN_PET_RATE + ", s." + COLUMN_PLANT_RATE + ", s." + COLUMN_INCOME +
                        " FROM " + TABLE_USERS + " u" +
                        " JOIN " + TABLE_SITTERS + " s ON u." + COLUMN_USER_ID + " = s." + COLUMN_USER_ID +
                        " WHERE u." + COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)});
    }

    public void updateSitterDetails(int userId, String name, String studentId, String email, String phoneNumber,
                                    String bio, boolean carePets, boolean carePlants, double petRate, double plantRate) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues userValues = new ContentValues();
        userValues.put(COLUMN_NAME, name);
        userValues.put(COLUMN_STUDENT_ID, studentId);
        userValues.put(COLUMN_EMAIL, email);
        userValues.put(COLUMN_PHONE_NUMBER, phoneNumber);
        db.update(TABLE_USERS, userValues, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});

        ContentValues sitterValues = new ContentValues();
        sitterValues.put(COLUMN_BIO, bio);
        sitterValues.put(COLUMN_CARE_PETS, carePets ? 1 : 0);
        sitterValues.put(COLUMN_CARE_PLANTS, carePlants ? 1 : 0);
        sitterValues.put(COLUMN_PET_RATE, petRate);
        sitterValues.put(COLUMN_PLANT_RATE, plantRate);
        db.update(TABLE_SITTERS, sitterValues, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});

        db.close();
    }

    // ------------------- OWNER METHODS -------------------

    public void addOwner(int userId, String bio, boolean carePets, boolean carePlants) {
        SQLiteDatabase db = this.getWritableDatabase();
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
                "SELECT u." + COLUMN_NAME + ", u." + COLUMN_STUDENT_ID + ", u." + COLUMN_EMAIL + ", u." + COLUMN_PHONE_NUMBER +
                        ", o." + COLUMN_OWNER_BIO + ", o." + COLUMN_OWNER_CARE_PETS + ", o." + COLUMN_OWNER_CARE_PLANTS +
                        " FROM " + TABLE_USERS + " u" +
                        " JOIN " + TABLE_OWNERS + " o ON u." + COLUMN_USER_ID + " = o." + COLUMN_USER_ID +
                        " WHERE u." + COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)});
    }

    public void updateOwnerDetails(int userId, String name, String studentId, String email, String phoneNumber,
                                   String bio, boolean carePets, boolean carePlants) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues userValues = new ContentValues();
        userValues.put(COLUMN_NAME, name);
        userValues.put(COLUMN_STUDENT_ID, studentId);
        userValues.put(COLUMN_EMAIL, email);
        userValues.put(COLUMN_PHONE_NUMBER, phoneNumber);
        db.update(TABLE_USERS, userValues, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});

        ContentValues ownerValues = new ContentValues();
        ownerValues.put(COLUMN_OWNER_BIO, bio);
        ownerValues.put(COLUMN_OWNER_CARE_PETS, carePets ? 1 : 0);
        ownerValues.put(COLUMN_OWNER_CARE_PLANTS, carePlants ? 1 : 0);
        db.update(TABLE_OWNERS, ownerValues, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});

        db.close();
    }
}
