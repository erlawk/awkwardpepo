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

    // All Static variab
    // Database Version
    private static final int DATABASE_VERSION = 2;
    // Database Name
    private static final String DATABASE_NAME = "eventappdb";
    // Login table name
    private static final String TABLE_USER = "userinfo";
    // User preference table name
    private static final String TABLE_PREFERENCE = "userpreference";
    // Login Table Columns names
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "emailAddress";
    private static final String KEY_MOBILENUMBER = "mobileNumber";
    // User Preference Columns names
    private static final String KEY_MUSIC = "music";
    private static final String KEY_FOOD = "food";
    private static final String KEY_FESTIVAL = "festival";
    private static final String KEY_SOCIAL = "social";
    private static final String KEY_PARTY = "party";
    private static final String KEY_SPORTS = "sports";
    // Common columns names
    private static final String KEY_UID = "userID";
    private static final String KEY_ID = "id";
    private static final String KEY_ID1 = "id";

    // Table Create Statements
    private static final String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_UID + " INTEGER," + KEY_NAME + " TEXT,"
            + KEY_EMAIL + " TEXT UNIQUE,"
            + KEY_MOBILENUMBER + " TEXT" + ")";

    private static final String CREATE_PREFERENCE_TABLE = "CREATE TABLE " + TABLE_PREFERENCE + "(" + KEY_ID1 + " INTEGER PRIMARY KEY,"
            + KEY_EMAIL + " TEXT," + KEY_FOOD + " TEXT,"
            + KEY_FESTIVAL + " TEXT," + KEY_MUSIC + " TEXT,"
            + KEY_PARTY + " TEXT," + KEY_SOCIAL + " TEXT,"
            + KEY_SPORTS + " TEXT" + ")";


    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LOGIN_TABLE);
        db.execSQL(CREATE_PREFERENCE_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PREFERENCE);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(int userID, String name, String email, String mobileNumber) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_UID, userID); // Email
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_MOBILENUMBER, mobileNumber); // Created At

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }
    /**
     * Storing user preference details in database
     * */
    public void addPreference(String emailAddress, String music, String food, String festival, String social
                            , String party, String sports) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EMAIL, emailAddress);
        values.put(KEY_FOOD, food);
        values.put(KEY_MUSIC, music);
        values.put(KEY_FESTIVAL, festival);
        values.put(KEY_SOCIAL, social);
        values.put(KEY_PARTY, party);
        values.put(KEY_SPORTS, sports);
        // Inserting rows
        long id = db.insert(TABLE_PREFERENCE, null, values);
        db.close();

        Log.d(TAG, "New user preference inserted into sqlite: " + id);
    }
    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap< >();
        String selectQuery = "SELECT * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            //user.put("userID", cursor.getString(1));
            user.put("name", cursor.getString(1));
            user.put("emailAddress", cursor.getString(2));
            user.put("mobileNumber", cursor.getString(3));
            //user.put("userID", cursor.getString(4));

            //user.put("mobileNumber", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }
    public void deletePreference() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_PREFERENCE, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }


}