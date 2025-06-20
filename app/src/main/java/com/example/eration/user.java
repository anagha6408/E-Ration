package com.example.eration;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class user extends AppCompatActivity {

    TextView userNameTextView, userFamilyMembersTextView, userAgeTextView, userOccupationTextView;
    TextView userIncomeTextView, userStatusTextView;
    private String loggedInUsername, loggedInUsertypes;
    private MyDatabase db;
    private GoodsListData db2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        userNameTextView = findViewById(R.id.user_name_text_view);
        userFamilyMembersTextView = findViewById(R.id.user_family_members_text_view);
        userAgeTextView = findViewById(R.id.user_age_text_view);
        userOccupationTextView = findViewById(R.id.user_occupation_text_view);
        userIncomeTextView = findViewById(R.id.user_income_text_view);
        userStatusTextView = findViewById(R.id.user_status_text_view);

        db = new MyDatabase(this);
        db2 = new GoodsListData(this);
        // Retrieve username and password from the intent
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String password = intent.getStringExtra("password");

        Toast.makeText(getApplicationContext(),"Welcome  "+username+"  !!!",Toast.LENGTH_SHORT).show();
        // Log the values to check if they are being received
        Log.d("UserActivity", "Received username: " + username);
        Log.d("UserActivity", "Received password: " + password);


        if (username != null && password != null) {
            loggedInUsername = username;

            // Fetch user type from database
            loggedInUsertypes = db.getUser_type(username, password);
            Log.d("UserType", "Logged in user type: " + loggedInUsertypes);
            if (loggedInUsertypes != null) {
                // Proceed to fetch user details and check user types
                getCustomerDetails();
                checkUsertypes(loggedInUsertypes);

            } else {
                Toast.makeText(this, "Error: User type not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Error: Missing username or password", Toast.LENGTH_SHORT).show();
        }

    }

    private void getCustomerDetails() {
        // Fetch user details from the database
        Cursor cursor = db.getCustomerDetails(loggedInUsername);
        if (cursor != null && cursor.moveToFirst()) {
            Log.d("CursorColumns", Arrays.toString(cursor.getColumnNames()));
            String userName = cursor.getString(cursor.getColumnIndex("user_name"));
            String familyMembers = cursor.getString(cursor.getColumnIndex("family_members"));
            String age = cursor.getString(cursor.getColumnIndex("age"));
            String occupation = cursor.getString(cursor.getColumnIndex("occupation"));
            String income = cursor.getString(cursor.getColumnIndex("income"));
            String status = cursor.getString(cursor.getColumnIndex("status"));

            userNameTextView.setText(userName);
            userFamilyMembersTextView.setText(familyMembers);
            userAgeTextView.setText(age);
            userOccupationTextView.setText(occupation);
            userIncomeTextView.setText(income);
            userStatusTextView.setText(status);
        } else {
            Toast.makeText(this, "No user details found", Toast.LENGTH_SHORT).show();
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    public void checkUsertypes(String userType) {
        Cursor cursor = db2.getGoodsByUserType(userType);
        if (cursor != null && cursor.moveToFirst()) {
            try {
                LinearLayout buttonContainer = findViewById(R.id.buttonContainer);
                if (buttonContainer == null) {
                    Log.e("UserActivity", "Button container not found in layout");
                    return;
                }

                buttonContainer.removeAllViews(); // Clear any previous buttons

                List<String> ownerIds = db.getAllOwnerIds(); // Fetch all owner IDs
                if (ownerIds != null && !ownerIds.isEmpty()) {
                    for (String ownerId : ownerIds) {
                        Log.d("Owner ID", ownerId);

                        // Create a new button for each owner
                        Button ownerButton = new Button(this);
                        ownerButton.setText("Owner ID: " + ownerId);
                        ownerButton.setTextSize(18); // Set text size in SP
                        ownerButton.setTextColor(getResources().getColor(R.color.yellow)); // Assuming #ffde59 is defined in colors.xml
                        ownerButton.setBackground(getResources().getDrawable(R.drawable.button_style)); // Set the drawable background

                        ownerButton.setTag(ownerId); // Set the ownerId as the button's tag
                        // Set the click listener for the button
                        ownerButton.setOnClickListener(v -> {
                            String clickedOwnerId = (String) v.getTag(); // Retrieve the ownerId from the button's tag
                            Log.d("UserActivity", "Button clicked for Owner ID: " + clickedOwnerId);

                            // Toast message to confirm button click
                            Toast.makeText(user.this, "Fetching goods for Owner ID: " + clickedOwnerId, Toast.LENGTH_SHORT).show();

                            // Create an Intent to navigate to the userGoodsDisplay activity
                            Intent intent = new Intent(user.this, userGoodsDisplay.class);
                            intent.putExtra("owner_id", clickedOwnerId);
                            intent.putExtra("loggedInUsertypes", loggedInUsertypes);
                            startActivity(intent); // Start the userGoodsDisplay activity
                        });

                        // Set layout parameters for the button
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        params.setMargins(0, 10, 0, 10); // Add margin between buttons
                        ownerButton.setLayoutParams(params);

                        // Make sure the button is visible and clickable
                        ownerButton.setVisibility(View.VISIBLE);
                        ownerButton.setClickable(true);

                        // Add the button to the container
                        buttonContainer.addView(ownerButton);
                        Log.d("UserActivity", "Button added for Owner ID: " + ownerId);
                    }
                } else {
                    Toast.makeText(this, "No owners found", Toast.LENGTH_SHORT).show();
                }

                // Extract item details for logging or usage
                String itemName = cursor.getString(cursor.getColumnIndexOrThrow("item_name"));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("current_quantity"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                int allocatedQuantity = cursor.getInt(cursor.getColumnIndexOrThrow("allocated_quantity"));

                Log.d("UserActivity", "Item: " + itemName + ", Date: " + date + ", Allocated Quantity: " + allocatedQuantity + ", Remaining Quantity: " + quantity);
            } catch (Exception e) {
                Log.e("UserActivity", "Error accessing cursor data", e);
            } finally {
                cursor.close();
            }
        } else {
            Log.e("UserActivity", "Cursor is empty or null");
        }
    }


}

