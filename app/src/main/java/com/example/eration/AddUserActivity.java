package com.example.eration;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;;
import android.widget.Toast;

public class AddUserActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etOwnerName, etFamilyMembers, etAge, etOccupation, etIncome, etStatus;
    private Button btnSaveUser;
    private RadioGroup radioGroupStatus; // Declare RadioGroup for status
    private MyDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        // Initialize UI elements
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etOwnerName = findViewById(R.id.etOwnerName);
        etFamilyMembers = findViewById(R.id.etFamilyMembers);
        etAge = findViewById(R.id.etAge);
        etOccupation = findViewById(R.id.etOccupation);
        etIncome = findViewById(R.id.etIncome);
        radioGroupStatus = findViewById(R.id.radioGroupStatus);
        btnSaveUser = findViewById(R.id.btnSaveUser);


        db = new MyDatabase(this);

        // Set up the save button listener
        btnSaveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Get input values
                    String username = etUsername.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();
                    String ownerName = etOwnerName.getText().toString().trim();
                    String familyMembersStr = etFamilyMembers.getText().toString().trim();
                    String ageStr = etAge.getText().toString().trim();
                    String occupation = etOccupation.getText().toString().trim();
                    String income = etIncome.getText().toString().trim();

                    // Get selected status from RadioGroup
                    int selectedStatusId = radioGroupStatus.getCheckedRadioButtonId();
                    if (selectedStatusId == -1) {
                        Toast.makeText(AddUserActivity.this, "Please select a status", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    RadioButton selectedStatusButton = findViewById(selectedStatusId);
                    String status = selectedStatusButton.getText().toString();

                    // Validate input fields
                    if (username.isEmpty() || password.isEmpty() || ownerName.isEmpty() || familyMembersStr.isEmpty() ||
                            ageStr.isEmpty() || occupation.isEmpty() || income.isEmpty()) {
                        Toast.makeText(AddUserActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Convert family members and age to integers
                    int familyMembers;
                    int age;
                    try {
                        familyMembers = Integer.parseInt(familyMembersStr);
                        age = Integer.parseInt(ageStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(AddUserActivity.this, "Please enter valid numbers for family members and age", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Add the user to the database
                    boolean isUserAdded = db.addUser(username, password,"user",status);
                    boolean isUserDetailsAdded = db.addUserDetails(ownerName, familyMembers, age, occupation, income, status);

                    if (isUserAdded && isUserDetailsAdded) {
                        Toast.makeText(AddUserActivity.this, "User added successfully", Toast.LENGTH_SHORT).show();
                        clearFields();
                    } else {
                        Toast.makeText(AddUserActivity.this, "Failed to add user", Toast.LENGTH_SHORT).show();
                    }
                } catch (NullPointerException e) {
                    Toast.makeText(AddUserActivity.this, "An unexpected error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Clear all input fields
    private void clearFields() {
        etUsername.setText("");
        etPassword.setText("");
        etOwnerName.setText("");
        etFamilyMembers.setText("");
        etAge.setText("");
        etOccupation.setText("");
        etIncome.setText("");
        radioGroupStatus.clearCheck(); // Clear selected radio button in RadioGroup
    }
}