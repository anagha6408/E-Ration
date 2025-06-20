package com.example.eration;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity2 extends AppCompatActivity {

    EditText _txtUser, _txtPass;
    Button _btnLogin;
    Spinner _spinner;
    MyDatabase MyDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        // Initialize the views
        _txtPass = findViewById(R.id.txtPass);
        _txtUser = findViewById(R.id.txtUser);
        _btnLogin = findViewById(R.id.btnLogin);
        _spinner = findViewById(R.id.spinner);
        MyDatabase = new MyDatabase(this);

        // Set up the spinner adapter to display user types
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.usertype,
                android.R.layout.simple_spinner_item // Changed to standard layout for spinner items
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _spinner.setAdapter(adapter);

        // Set up the login button click listener
        _btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected item from the spinner
                String userType = _spinner.getSelectedItem().toString();
                String username = _txtUser.getText().toString();
                String password = _txtPass.getText().toString();

                // Check for valid credentials using the database
                if (MyDatabase.checkUserCredentials(username, password, userType)) {
                    Intent intent;

                    // Determine which activity to launch based on user type
                    if (userType.equals("admin")) {
                        intent = new Intent(MainActivity2.this, admin.class);
                        startActivity(intent);
                    } else if (userType.equals("user")) {
                        intent = new Intent(MainActivity2.this, user.class);
                        intent.putExtra("username", username);
                        intent.putExtra("password", password);
                        Log.d("Intent to pass>>",username);
                        Log.d("Intent to pass>>",password);
                        startActivity(intent);
                    } else if (userType.equals("owner"))
                    {
                        Intent intent2 = new Intent(MainActivity2.this, owner.class);
                        intent2.putExtra("username", username);
                        Log.e("TAG", username);// Replace 'username' with the actual value
                        //intent = new Intent(MainActivity2.this, owner.class);
                        startActivity(intent2);
                    } else {
                        Toast.makeText(getApplicationContext(), "Error: Invalid user type", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Clear inputs before navigating
                    _txtUser.setText("");
                    _txtPass.setText("");
                    _spinner.setSelection(0);
                } else {
                    Toast.makeText(getApplicationContext(), "Error: Invalid credentials", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
