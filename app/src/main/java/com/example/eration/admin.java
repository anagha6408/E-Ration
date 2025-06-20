package com.example.eration;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class admin extends AppCompatActivity {

    Button _btnAddUser, _btnAddOwner, _btnDisUser, _btnDisOwner,_btnDisGoods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Toast.makeText(getApplicationContext(),"Welcome Admin!!!",Toast.LENGTH_SHORT).show();

        // Initialize buttons
        _btnAddUser = findViewById(R.id.btnAddUser);
        _btnAddOwner = findViewById(R.id.btnAddOwner);
        _btnDisUser = findViewById(R.id.btnDisUser);
        _btnDisOwner = findViewById(R.id.btnDisOwner);
        _btnDisGoods = findViewById(R.id.btnDisGoods);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String password = intent.getStringExtra("password");

        // Set click listeners for each button
        _btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(admin.this, AddUserActivity.class);
                startActivity(intent);
            }
        });

        _btnAddOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(admin.this, AddOwnerActivity.class); // Make sure this activity exists
                startActivity(intent);
            }
        });

        _btnDisUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(admin.this, DisplayUsersActivity.class);
                startActivity(intent);
            }
        });

        _btnDisOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(admin.this, DisplayOwnersActivity.class); // Make sure this activity exists
                startActivity(intent);
            }
        });
        _btnDisGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(admin.this, AdminGoodsDisplay.class); // Make sure this activity exists
                intent.putExtra("username", username);
                intent.putExtra("password", password);
                String user_types = intent.getStringExtra("user_types");
                startActivity(intent);
            }
        });
    }
}