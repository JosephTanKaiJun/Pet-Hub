package com.example.pethub.database;
import com.example.pethub.chat.Conversation;

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
    private static final int DATABASE_VERSION = 28;

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

    private static final String TABLE_COMMUNITY = "community";
    private static final String COLUMN_COMMUNITY_ID = "id";
    private static final String COLUMN_COMMUNITY_USERNAME = "username";
    private static final String COLUMN_COMMUNITY_COMMENT = "comment";

    // community rating

    // Ratings table
    private static final String TABLE_RATINGS = "ratings";
    private static final String COLUMN_RATING_ID = "id";
    private static final String COLUMN_RATING_USERNAME = "username";
    private static final String COLUMN_RATING_SITTER_NAME = "sitter_name";
    private static final String COLUMN_RATING_DETAILS = "details";
    private static final String COLUMN_RATING_STARS = "stars";


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

        // Replace existing messages table creation with:
        db.execSQL("CREATE TABLE " + TABLE_MESSAGES + "("
                + COLUMN_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "sender_id INTEGER NOT NULL,"
                + "receiver_id INTEGER NOT NULL,"
                + COLUMN_CONTENT + " TEXT,"
                + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY(sender_id) REFERENCES " + TABLE_USERS + "(user_id),"
                + "FOREIGN KEY(receiver_id) REFERENCES " + TABLE_USERS + "(user_id))");

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

        String CREATE_COMMUNITY_TABLE = "CREATE TABLE " + TABLE_COMMUNITY + "("
                + COLUMN_COMMUNITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_COMMUNITY_USERNAME + " TEXT NOT NULL,"
                + COLUMN_COMMUNITY_COMMENT + " TEXT NOT NULL)";
        db.execSQL(CREATE_COMMUNITY_TABLE);

        String CREATE_RATINGS_TABLE = "CREATE TABLE " + TABLE_RATINGS + "("
                + COLUMN_RATING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_RATING_USERNAME + " TEXT NOT NULL,"
                + COLUMN_RATING_SITTER_NAME + " TEXT NOT NULL,"
                + COLUMN_RATING_DETAILS + " TEXT NOT NULL,"
                + COLUMN_RATING_STARS + " INTEGER NOT NULL)";
        db.execSQL(CREATE_RATINGS_TABLE);

    }

    public boolean addOrUpdateRating(String username, String sitterName, String details, int stars) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_RATING_USERNAME, username);
        values.put(COLUMN_RATING_SITTER_NAME, sitterName);
        values.put(COLUMN_RATING_DETAILS, details);
        values.put(COLUMN_RATING_STARS, stars);

        long result = db.insert(TABLE_RATINGS, null, values);
        return result != -1;
    }
    public List<String> getAllRatingsWithAverageAndDetails() {
        List<String> resultList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Step 1: Get distinct sitter names
        Cursor sitterCursor = db.rawQuery("SELECT DISTINCT " + COLUMN_RATING_SITTER_NAME + " FROM " + TABLE_RATINGS, null);

        if (sitterCursor.moveToFirst()) {
            do {
                String sitter = sitterCursor.getString(0);

                // Step 2: Get average rating for this sitter
                Cursor avgCursor = db.rawQuery(
                        "SELECT AVG(" + COLUMN_RATING_STARS + ") FROM " + TABLE_RATINGS +
                                " WHERE " + COLUMN_RATING_SITTER_NAME + " = ?",
                        new String[]{sitter}
                );

                float avg = 0;
                if (avgCursor.moveToFirst()) {
                    avg = avgCursor.getFloat(0);
                }
                avgCursor.close();

                // Step 3: Get review details
                Cursor detailCursor = db.rawQuery(
                        "SELECT " + COLUMN_RATING_DETAILS + " FROM " + TABLE_RATINGS +
                                " WHERE " + COLUMN_RATING_SITTER_NAME + " = ?",
                        new String[]{sitter}
                );

                StringBuilder detailsBuilder = new StringBuilder();
                while (detailCursor.moveToNext()) {
                    String detail = detailCursor.getString(0);
                    detailsBuilder.append("• ").append(detail).append("\n");
                }
                detailCursor.close();

                String summary = sitter + ": ⭐" + String.format("%.1f", avg) + "\n" + detailsBuilder.toString();
                resultList.add(summary);

            } while (sitterCursor.moveToNext());
        }

        sitterCursor.close();
        return resultList;
    }


    // Add community comment
    public void addCommunityComment(String username, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("comment", comment);
        db.insert("community", null, values);
        db.close();
    }

    // Get all community comments
    public List<String> getAllCommunityComments() {
        List<String> comments = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT username, comment FROM community ORDER BY id DESC", null);

        if (cursor.moveToFirst()) {
            do {
                String username = cursor.getString(0);
                String comment = cursor.getString(1);
                comments.add(username + ": " + comment);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return comments;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop all tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HIRING_REQUESTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SITTERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OWNERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMUNITY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RATINGS);

        // Recreate tables
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
        return db.rawQuery(
                "SELECT u." + COLUMN_USER_ID + ", u." + COLUMN_USERNAME + ", s." + COLUMN_BIO +
                        ", s." + COLUMN_SKILLS + ", u." + COLUMN_EMAIL + ", u." + COLUMN_PHOTO_URI +
                        " FROM " + TABLE_SITTERS + " s " +
                        "JOIN " + TABLE_USERS + " u ON s." + COLUMN_USER_ID + " = u." + COLUMN_USER_ID,
                null
        );
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

    // Replace old method with:
    public void addMessage(int senderId, int receiverId, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sender_id", senderId);
        values.put("receiver_id", receiverId);
        values.put(COLUMN_CONTENT, content);
        db.insert(TABLE_MESSAGES, null, values);
        db.close();
    }
    private String getPhotoUri(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_PHOTO_URI},
                COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)}, null, null, null);
        String uri = "";
        if (cursor.moveToFirst()) {
            uri = cursor.getString(0);
        }
        cursor.close();
        return uri;
    }

    public List<Conversation> getConversations(int userId) {
        List<Conversation> conversations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT DISTINCT "
                + "CASE WHEN sender_id = ? THEN receiver_id ELSE sender_id END AS partner_id, "
                + "MAX(" + COLUMN_TIMESTAMP + ") AS latest_timestamp "
                + "FROM " + TABLE_MESSAGES
                + " WHERE sender_id = ? OR receiver_id = ? "
                + "GROUP BY partner_id";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(userId), String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                int partnerId = cursor.getInt(0);
                String timestamp = cursor.getString(1);
                String partnerName = getUsername(partnerId); // Use the new method
                String lastMessage = getLastMessage(userId, partnerId);
                String photoUri = getPhotoUri(partnerId);

                conversations.add(new Conversation(
                        partnerId,
                        partnerName, // Now using the username from Users table
                        lastMessage,
                        timestamp,
                        photoUri
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return conversations;
    }

    private String getLastMessage(int userA, int userB) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_CONTENT + " FROM " + TABLE_MESSAGES
                        + " WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?) "
                        + "ORDER BY " + COLUMN_TIMESTAMP + " DESC LIMIT 1",
                new String[]{String.valueOf(userA), String.valueOf(userB), String.valueOf(userB), String.valueOf(userA)}
        );

        String lastMessage = "";
        if (cursor.moveToFirst()) {
            lastMessage = cursor.getString(0);
        }
        cursor.close();
        return lastMessage;
    }
    public List<Message> getConversation(int userA, int userB) {
        List<Message> messages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MESSAGES +
                        " WHERE (sender_id = ? AND receiver_id = ?)" +
                        " OR (sender_id = ? AND receiver_id = ?)" +
                        " ORDER BY " + COLUMN_TIMESTAMP + " ASC",
                new String[]{
                        String.valueOf(userA),
                        String.valueOf(userB),
                        String.valueOf(userB),
                        String.valueOf(userA)
                });

        if (cursor.moveToFirst()) {
            do {
                Message message = new Message(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow("sender_id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("receiver_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)),
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
    public String getUsername(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_USERNAME}, COLUMN_USER_ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, null);
        String username = "";
        if (cursor.moveToFirst()) {
            username = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return username;
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