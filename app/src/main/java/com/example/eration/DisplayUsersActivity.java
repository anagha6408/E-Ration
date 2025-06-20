package com.example.eration;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class DisplayUsersActivity extends AppCompatActivity {

    private ListView listViewAPLUsers;
    private ListView listViewBPLUsers;
    private ListView listViewAYUsers;
    private MyDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_users);

        // Initialize ListViews
        listViewAPLUsers = findViewById(R.id.listViewAPLUsers);
        listViewBPLUsers = findViewById(R.id.listViewBPLUsers);
        listViewAYUsers = findViewById(R.id.listViewAYUsers);

        db = new MyDatabase(this);

        // Load user data from the database
        loadUserData();
    }

    // Retrieve user details and display them in the ListViews based on status
    private void loadUserData() {
        loadUserDataByStatus("APL", listViewAPLUsers);
        loadUserDataByStatus("BPL", listViewBPLUsers);
        loadUserDataByStatus("AY", listViewAYUsers);
    }

    // Load user data by status and set it to the respective ListView
    private void loadUserDataByStatus(String status, ListView listView) {
        Cursor cursor = db.getUserDetailsByStatus(status); // Implement this method in MyDatabase
        if (cursor != null && cursor.getCount() > 0) {
            String[] from = {
                    "user_name", "family_members", "age", "occupation", "income", "status"
            };
            int[] to = {
                    R.id.tvOwnerName, R.id.tvFamilyMembers, R.id.tvAge, R.id.tvOccupation, R.id.tvIncome, R.id.tvStatus
            };
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                    this,
                    R.layout.user_list_item, // Define the layout for each item
                    cursor,
                    from,
                    to,
                    0
            );
            listView.setAdapter(adapter);
        } else {
            Toast.makeText(this, "No " + status + " users found", Toast.LENGTH_SHORT).show();
            // Optionally clear the adapter if no data is found
            listView.setAdapter(null);
        }
    }
}
