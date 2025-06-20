package com.example.eration;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class GoodsListData  extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ration_db";
    private static final int DATABASE_VERSION = 7;

    private static final String TABLE_GOODS  = "goods";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_OWNER_ID = "owner_id";
    private static final String COLUMN_ITEM_NAME = "item_name";
    private static final String COLUMN_TOTAL_QUANTITY = "total_quantity";
    private static final String COLUMN_CURRENT_QUANTITY = "current_quantity";
    private static final String COLUMN_DATE = "date";

    public static final String TABLE_ALLOCATIONS = "allocations";
    public static final String COLUMN_ALLOCATIONS_ID = "allocations_id";
    private static final String COLUMN_ALLO_OWNER_ID = "allo_owner_id";
    private static final String COLUMN_ALLOCATED_ITEM_NAME = "item_name";
    public static final String COLUMN_ALLOCATED_QUANTITY = "allocated_quantity";
    public static final String COLUMN_USER_TYPE = "user_types";

    public GoodsListData(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_GOODS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_OWNER_ID + " INTEGER, " +
                COLUMN_ITEM_NAME + " TEXT, " +
                COLUMN_TOTAL_QUANTITY + " INTEGER, " +
                COLUMN_CURRENT_QUANTITY + " INTEGER, " +
                COLUMN_DATE + " TEXT)";
        db.execSQL(createTableQuery); // Goods table creation

        String createAllocationsTable = "CREATE TABLE " + TABLE_ALLOCATIONS + " (" +
                COLUMN_ALLOCATIONS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ALLO_OWNER_ID + " INTEGER, " +
                COLUMN_ALLOCATED_ITEM_NAME + " TEXT, " +
                COLUMN_ALLOCATED_QUANTITY + " INTEGER, " +
                COLUMN_USER_TYPE + " TEXT," +
                "FOREIGN KEY (allo_owner_id) REFERENCES owners(owner_id)" +
                ")";
        db.execSQL(createAllocationsTable); // Allocations table creation

        Log.d("Database", "Tables created: " + TABLE_GOODS + ", " + TABLE_ALLOCATIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("Database", "onUpgrade called");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GOODS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALLOCATIONS);
        onCreate(db);

    }
    public void logTables() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        if (cursor.moveToFirst()) {
            do {
                Log.d("Database", "Table: " + cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public void addRationGoods(int owner_id,String itemName, int totalQuantity, int currentQuantity, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_OWNER_ID, owner_id);
        values.put(COLUMN_ITEM_NAME, itemName);
        values.put(COLUMN_TOTAL_QUANTITY, totalQuantity);
        values.put(COLUMN_CURRENT_QUANTITY, currentQuantity);
        values.put(COLUMN_DATE, date);
        Log.d("ownerGoods", "Item: " + itemName + ", Owner ID: " + owner_id + ", Total Quantity: " + totalQuantity + ", Date: " + date);

        // Insert into the goods table (corrected table name)
        db.insert(TABLE_GOODS, null, values);
        db.close();
    }



    public Cursor getAllRationGoods(int owner_id) {
        Log.e("Tag", "owner id is : " + owner_id);
        SQLiteDatabase db = this.getReadableDatabase();
        // Correctly pass the owner_id as part of selectionArgs
        return db.rawQuery("SELECT * FROM " + TABLE_GOODS + " WHERE owner_id = ?", new String[]{String.valueOf(owner_id)});
    }


    public boolean allocateGoods(int allo_owner_id, String itemName, int allocated_quantity, String userType) {
        if (allocated_quantity <= 0) {
            Log.e("AllocateGoods", "Invalid quantity: " + allocated_quantity);
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;

        try {
            // Check available quantity
            cursor = db.rawQuery(
                    "SELECT " + COLUMN_TOTAL_QUANTITY +
                            " FROM " + TABLE_GOODS +
                            " WHERE " + COLUMN_ITEM_NAME + " = ? AND " + COLUMN_OWNER_ID + " = ?",
                    new String[]{itemName, String.valueOf(allo_owner_id)}
            );

            Log.d("DatabaseQuery", "Rows returned: " + cursor.getCount());

            if (cursor.moveToFirst()) {
                int availableQuantity = cursor.getInt(0);
                if (availableQuantity < allocated_quantity) {
                    Log.w("AllocateGoods", "Insufficient quantity for item: " + itemName);
                    return false; // Insufficient quantity
                }

                db.beginTransaction(); // Start transaction

                try {
                    // Deduct allocated quantity from available quantity
                    ContentValues updateValues = new ContentValues();
                    updateValues.put(COLUMN_CURRENT_QUANTITY, availableQuantity - allocated_quantity);

                    int rowsUpdated = db.update(
                            TABLE_GOODS,
                            updateValues,
                            COLUMN_ITEM_NAME + " = ? AND owner_id = ?",
                            new String[]{itemName, String.valueOf(allo_owner_id)}
                    );

                    if (rowsUpdated == 0) {
                        Log.e("AllocateGoods", "Failed to update available quantity for item: " + itemName);
                        db.endTransaction(); // Rollback transaction
                        return false;
                    }

                    // Insert allocation into the allocations table
                    ContentValues allocationValues = new ContentValues();
                    allocationValues.put(COLUMN_ALLO_OWNER_ID, allo_owner_id);
                    allocationValues.put(COLUMN_ALLOCATED_ITEM_NAME, itemName);
                    allocationValues.put(COLUMN_ALLOCATED_QUANTITY, allocated_quantity);
                    allocationValues.put(COLUMN_USER_TYPE, userType);

                    long result = db.insert(TABLE_ALLOCATIONS, null, allocationValues);

                    if (result == -1) {
                        Log.e("AllocateGoods", "Failed to insert allocation record for item: " + itemName);
                        db.endTransaction(); // Rollback transaction
                        return false;
                    }

                    db.setTransactionSuccessful(); // Commit transaction
                    Log.d("AllocateGoods", "Allocation successful for item: " + itemName);
                    Log.d("AllocatedList"," Owner ID: " + allo_owner_id +
                            ", Item Name: " + itemName + ", Quantity: " + allocated_quantity +
                            ", User Type: " + userType);
                    return true;

                } finally {
                    db.endTransaction(); // Ensure transaction ends
                }
            } else {
                Log.w("AllocateGoods", "Item not found: " + itemName);
                return false; // Item not found
            }
        } catch (Exception e) {
            Log.e("AllocateGoods", "Error during allocation", e);
            return false;
        } finally {
            if (cursor != null) {
                cursor.close(); // Ensure cursor is closed
            }
        }
    }
    public Cursor getAllGoods(int owner_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT item_name, current_quantity FROM " + TABLE_GOODS + " WHERE owner_id = ? AND current_quantity > 0";
        return db.rawQuery(query, new String[]{String.valueOf(owner_id)});
    }

    public void updateQuantity(String itemName, int newQuantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CURRENT_QUANTITY, newQuantity);
        db.update(TABLE_GOODS, values, COLUMN_ITEM_NAME + " = ?", new String[]{itemName});
    }
    /*
    // Update the quantity of a specific item
    public void updateeeQuantity(String itemName, int newQuantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CURRENT_QUANTITY, newQuantity);


        // Check if the item exists before updating
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_GOODS + " WHERE " + COLUMN_ITEM_NAME + " = ?", new String[]{itemName});

            if (cursor != null && cursor.moveToFirst()) {
                int currentQuantity = cursor.getInt(cursor.getColumnIndex(COLUMN_CURRENT_QUANTITY));
                // Check if the current quantity is zero
                if (currentQuantity == 0) {
                    Log.e("updateQuantity", "Cannot update the quantity for " + itemName + " because the current quantity is zero.");
                    return; // Exit the method if the current quantity is zero
                }

                // Item exists, update the quantity
                int rowsAffected = db.update(TABLE_GOODS, values, COLUMN_ITEM_NAME + " = ?", new String[]{itemName});
                if (rowsAffected > 0) {
                    Log.d("updateQuantity", "Quantity updated for " + itemName + " to " + newQuantity);
                } else {
                    Log.e("updateQuantity", "Failed to update quantity for " + itemName);
                }
            } else {
                Log.e("updateQuantity", "Item " + itemName + " not found");
            }
        } catch (Exception e) {
            Log.e("updateQuantity", "Error updating quantity for " + itemName, e);
        } finally {
            if (cursor != null) {
                cursor.close();  // Always close the cursor to prevent memory leaks
            }
        }
    }
    */


    // Method to get the available quantity of a specific item
    public int getAvailableQuantity(String itemName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_CURRENT_QUANTITY + " FROM " + TABLE_GOODS + " WHERE " + COLUMN_ITEM_NAME + " = ?", new String[]{itemName});
        if (cursor != null && cursor.moveToFirst()) {
            int quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_CURRENT_QUANTITY));
            cursor.close();
            return quantity;
        }
        cursor.close();
        return 0; // Return 0 if item not found
    }
    public List<Allocation> getAllAllocations(int owner_id) {
        List<Allocation> allocationList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM allocations WHERE allo_owner_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(owner_id)});

        if (cursor.moveToFirst()) {
            do {
                String itemName = cursor.getString(cursor.getColumnIndexOrThrow("item_name"));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("allocated_quantity"));
                String userType = cursor.getString(cursor.getColumnIndexOrThrow("user_types"));

                Allocation allocation = new Allocation(itemName, quantity, userType);
                allocationList.add(allocation);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return allocationList;
    }
    public Cursor getGoodsByUserType(String userType) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Use fully qualified column names to avoid ambiguity
        String query = "SELECT g." + COLUMN_ITEM_NAME + " AS item_name, " +
                "g." + COLUMN_CURRENT_QUANTITY + " AS current_quantity, " +
                "g." + COLUMN_DATE + " AS date, " +
                "a." + COLUMN_ALLOCATED_QUANTITY + " AS allocated_quantity " +
                "FROM " + TABLE_GOODS + " g " +
                "JOIN " + TABLE_ALLOCATIONS + " a " +
                "ON g." + COLUMN_ITEM_NAME + " = a." + COLUMN_ALLOCATED_ITEM_NAME + " " +
                "WHERE a." + COLUMN_USER_TYPE + " = ?";

        // Log the query and parameter for debugging
        Log.d("getGoodsByUserType", "Executing query: " + query + " with UserType: " + userType);

        // Execute the query
        return db.rawQuery(query, new String[]{userType});
    }


    public int getCurrentQuantity(String itemName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int currentQuantity = 0; // Default to 0 if no data is found

        try {
            cursor = db.query(
                    TABLE_GOODS, // Use the actual table name
                    new String[]{COLUMN_CURRENT_QUANTITY}, // Query for the current_quantity column
                    COLUMN_ITEM_NAME + " = ?", // Condition to match item_name
                    new String[]{itemName}, // Item name passed as argument
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                currentQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CURRENT_QUANTITY));
            }
        } catch (Exception e) {
            Log.e("getCurrentQuantity", "Error fetching current quantity for " + itemName, e);
        } finally {
            if (cursor != null) {
                cursor.close(); // Always close the cursor
            }
        }

        return currentQuantity; // Return the current quantity or 0 if not found
    }

    public List<String> getAllOwnerIds() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> ownerIds = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT DISTINCT " + COLUMN_OWNER_ID + " FROM " + TABLE_GOODS, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                ownerIds.add(cursor.getString(cursor.getColumnIndexOrThrow("owner_id")));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return ownerIds;
    }

    public Cursor getGoodsByOwnerId(String ownerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Use fully qualified column names to avoid ambiguity
        String query = "SELECT g." + COLUMN_ITEM_NAME + " AS item_name, " +
                "g." + COLUMN_CURRENT_QUANTITY + " AS current_quantity, " +
                "g." + COLUMN_DATE + " AS date, " +
                "a." + COLUMN_ALLOCATED_QUANTITY + " AS allocated_quantity " +
                "FROM " + TABLE_GOODS + " g " +
                "JOIN " + TABLE_ALLOCATIONS + " a " +
                "ON g." + COLUMN_ITEM_NAME + " = a." + COLUMN_ALLOCATED_ITEM_NAME + " " +
                "WHERE g." + COLUMN_OWNER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{ownerId});

        return cursor; // Ensure the Cursor is returned
    }



}
