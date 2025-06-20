package com.example.eration;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class DisplayOwnersActivity extends AppCompatActivity {
    private MyDatabase myDatabase;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_owners); // Your layout XML file

        myDatabase = new MyDatabase(this);
        listView = findViewById(R.id.listViewOwners); // ListView in your layout

        // Fetch owner details from the database
        Cursor cursor = myDatabase.getOwnerDetails();

        // Check if cursor is not null and contains data
        if (cursor != null && cursor.getCount() > 0) {
            ArrayList<String> ownerList = new ArrayList<>();

            // Loop through the cursor and add owner names to the list
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                Log.e("ID HERE IS ", String.valueOf(id));
                String username = cursor.getString(cursor.getColumnIndex("username"));
                String password = cursor.getString(cursor.getColumnIndex("password"));
                String ownerDetails = "Name: " + username + ", Password: " + password; // Format as needed
                ownerList.add(ownerDetails); // Add username to the list
            }
            cursor.close(); // Close cursor after use

            // Set up the adapter to bind the data to the ListView
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1, // Default layout for each item
                    ownerList // Data source
            );

            listView.setAdapter(adapter);
        } else {
            Toast.makeText(this, "No owners found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myDatabase != null) {
            myDatabase.close(); // Close database connection when activity is destroyed
        }
    }
}