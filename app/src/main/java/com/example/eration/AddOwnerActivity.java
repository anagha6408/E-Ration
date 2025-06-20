package com.example.eration;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddOwnerActivity extends AppCompatActivity {

    private EditText etOwnerName, etUsername, etPassword;
    private Button btnSaveOwner;
    private MyDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_owner);

        // Initialize UI elements
        etOwnerName = findViewById(R.id.etOwnerName);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnSaveOwner = findViewById(R.id.btnSaveOwner);

        db = new MyDatabase(this);

        // Set up the save button listener
        btnSaveOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ownerName = etOwnerName.getText().toString().trim();
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // Validate input fields
                if (ownerName.isEmpty() || username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(AddOwnerActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if username already exists
                if (db.isUsernameExists(username)) {
                    Toast.makeText(AddOwnerActivity.this, "Username already exists. Please choose another.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Add owner to the database
                boolean isSuccess = db.addUser(username, password, "owner", null); // Add owner with user type

                if (isSuccess) {
                    Toast.makeText(AddOwnerActivity.this, "Owner added successfully", Toast.LENGTH_SHORT).show();
                    clearFields();
                } else {
                    Toast.makeText(AddOwnerActivity.this, "Failed to add owner", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Clear all input fields
    private void clearFields() {
        etOwnerName.setText("");
        etUsername.setText("");
        etPassword.setText("");
    }
}