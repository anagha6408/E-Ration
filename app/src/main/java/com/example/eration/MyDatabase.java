package com.example.eration;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MyDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "login.db";
    private static final int DATABASE_VERSION = 8;

    // Users table
    public  static final String TABLE_USERS = "users";
    public  static final String COLUMN_USER_ID = "id";
    public  static final String COLUMN_USERNAME = "username";
    public  static final String COLUMN_PASSWORD = "password";
    public  static final String COLUMN_USERTYPE = "usertype"; // User type (admin, owner, user)
    public  static final String COLUMN_USERSTATUS = "userstatus"; // Status for users (APL/BPL/AY)

    // Table for user details (added by admin)
    public  static final String TABLE_USER_DETAILS = "user_details";
    public  static final String COLUMN_ID = "_id"; // Auto-increment ID column
    public  static final String COLUMN_USER_NAME = "user_name";
    public  static final String COLUMN_FAMILY_MEMBERS = "family_members";
    public  static final String COLUMN_AGE = "age";
    public  static final String COLUMN_OCCUPATION = "occupation";
    public  static final String COLUMN_INCOME = "income";
    public  static final String COLUMN_STATUS = "status"; // APL/BPL/AY

    public MyDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_USERTYPE + " TEXT, " +
                COLUMN_USERSTATUS + " TEXT)"); // Include the status column

        // Insert some default users for testing
        insertUser(db, "admin", "admin", "admin", null);
        insertUser(db, "user", "1234", "user", "APL");
        insertUser(db, "owner", "pass", "owner", null);

        db.execSQL("CREATE TABLE " + TABLE_USER_DETAILS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + // Auto-incremented ID
                COLUMN_USER_NAME + " TEXT, " +
                COLUMN_FAMILY_MEMBERS + " INTEGER, " +
                COLUMN_AGE + " INTEGER, " +
                COLUMN_OCCUPATION + " TEXT, " +
                COLUMN_INCOME + " TEXT, " +
                COLUMN_STATUS + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_DETAILS);
        onCreate(db);
    }

    // Method to add a new user for login purposes
    public boolean addUser(String username, String password,String usertype,String userstatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_USERTYPE, usertype); // Set user type as "user"
        values.put(COLUMN_USERSTATUS,userstatus);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1; // Return true if insertion was successful
    }

    // Method to validate login credentials
    public boolean checkUserCredentials(String username, String password, String usertype) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " +
                COLUMN_USERNAME + " = ? AND " +
                COLUMN_PASSWORD + " = ? AND " +
                COLUMN_USERTYPE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password, usertype});

        boolean isValid = cursor.moveToFirst(); // Check if a matching record exists
        cursor.close();
        return isValid;
    }

    // Method to add user details (admin adds ration card details)
    public boolean addUserDetails(String ownerName, int familyMembers, int age, String occupation, String income, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        Log.d("InsertUserDetails", "ownerName: " + ownerName + ", familyMembers: " + familyMembers +
                ", age: " + age + ", occupation: " + occupation + ", income: " + income + ", status: " + status);

        values.put(COLUMN_USER_NAME, ownerName);
        values.put(COLUMN_FAMILY_MEMBERS, familyMembers);
        values.put(COLUMN_AGE, age);
        values.put(COLUMN_OCCUPATION, occupation);
        values.put(COLUMN_INCOME, income);
        values.put(COLUMN_STATUS, status);


        long result = db.insert(TABLE_USER_DETAILS, null, values);
        return result != -1; // Return true if insertion was successful
    }


    public Cursor getUserDetails(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        // Check if the cursor is valid and contains data
        if (cursor != null && cursor.moveToFirst()) {
            return cursor;  // Return cursor only if data is found
        } else {
            Log.e("MyDatabase", "No user found with username: " + username);
            return null;  // Return null if no data found
        }
    }

    public Cursor getCustomerDetails(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USER_DETAILS + " WHERE " + COLUMN_USER_NAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        // Check if the cursor is valid and contains data
        if (cursor != null && cursor.moveToFirst()) {
            return cursor;  // Return cursor only if data is found
        } else {
            Log.e("MyDatabase", "No user found with username: " + username);
            return null;  // Return null if no data found
        }
    }

    private void insertUser(SQLiteDatabase db, String username, String password, String usertype, String userstatus) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_USERTYPE, usertype);
        values.put(COLUMN_USERSTATUS, userstatus);
        db.insert(TABLE_USERS, null, values);
    }
    // Check if the username already exists
    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ?", new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    public Cursor getUserDetailsByStatus(String status) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USER_DETAILS + " WHERE " + COLUMN_STATUS + " = ?", new String[]{status});
    }
    public Cursor getOwnerDetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT id,username, password FROM " + TABLE_USERS + " WHERE usertype = 'owner'", null);
    }
    public String getUser_type(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Correct query to fetch the user type from the status column
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_USERSTATUS + " FROM " + TABLE_USERS + " WHERE " +
                COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?", new String[]{username, password});

        if (cursor != null && cursor.moveToFirst()) {
            // Get the status (which represents the user type)
            String userStatus = cursor.getString(cursor.getColumnIndex(COLUMN_USERSTATUS));
            cursor.close();
            return userStatus;  // Return the user status as the user type
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;  // Return null if no matching user is found
    }

    public List<String> getAllOwnerIds() {
        SQLiteDatabase db = this.getReadableDatabase();  // Get the readable database
        List<String> ownerIds = new ArrayList<>();

        // Query to get all owner_ids from the 'users' table
        String query = "SELECT id FROM users WHERE usertype='owner' ";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            // Iterate through the cursor to get all owner_ids
            while (cursor.moveToNext()) {
                String ownerId = cursor.getString(cursor.getColumnIndex("id"));
                ownerIds.add(ownerId);
            }
            cursor.close();  // Close the cursor to avoid memory leaks
        }

        return ownerIds;
    }

}

