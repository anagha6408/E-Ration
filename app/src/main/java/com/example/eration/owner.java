package com.example.eration;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class owner extends AppCompatActivity {

    Button goods, allocateGoods;
    private MyDatabase myDatabase;
    private int ownerId;  // Declare outside so it's accessible within OnClickListener

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner);
        goods = findViewById(R.id.goods);
        myDatabase = new MyDatabase(this);

        // Get the username from the Intent
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        Log.e("TAG", "Username is : " + username);

        // Fetch user details from the database
        if (username != null) {
            Log.e("TAG", "HAII");
            Cursor cursor = myDatabase.getUserDetails(username);
            if (cursor != null && cursor.moveToFirst()) {
                try {
                    // Retrieve details before closing the cursor
                    ownerId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String userType = cursor.getString(cursor.getColumnIndexOrThrow("usertype"));

                    Toast.makeText(this, "Welcome, " + username + " to the Owner page", Toast.LENGTH_LONG).show();

                    Log.d("OwnerActivity", "Fetched user details: Username: " + username + ", ID: " + ownerId + ", Type: " + userType);
                } catch (Exception e) {
                    Log.e("TAG", "Error retrieving user details", e);
                } finally {
                    cursor.close(); // Always close the cursor in a finally block to avoid resource leaks
                }
            } else {
                Log.e("TAG", "No user details found for username: " + username);
            }

            // Set up the goods button click listener
            goods.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(owner.this, ownerGoods.class);
                    i.putExtra("owner_id", ownerId);  // Pass the owner id
                    startActivity(i);
                }
            });

            // Set up the allocateGoods button click listener
            allocateGoods = findViewById(R.id.btnAllocateGoods);
            allocateGoods.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i2 = new Intent(owner.this, AllocateGoodsActivity.class);
                    i2.putExtra("owner_id", ownerId);  // Pass the owner id
                    startActivity(i2);
                }
            });
        }
    }
}