package com.example.eration;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ownerGoods extends AppCompatActivity {

    private GoodsListData db;
    private EditText itemNameEditText, quantityEditText, dateEditText;
    private RecyclerView goodsRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_goods);
        db = new GoodsListData(this);
        itemNameEditText = findViewById(R.id.item_name);
        quantityEditText = findViewById(R.id.quantity);
        dateEditText = findViewById(R.id.date);

        goodsRecyclerView = findViewById(R.id.goods_recycler_view);
        goodsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button addGoodsButton = findViewById(R.id.add_goods_button);
        addGoodsButton.setOnClickListener(v -> addGoods());

        // Show date picker on clicking the date field
        dateEditText.setOnClickListener(v -> showDatePicker());

        // Load existing goods on startup
        loadRationGoods();

    }
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String dateStr = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    dateEditText.setText(dateStr);
                },
                year, month, day);
        datePickerDialog.show();
    }


    private void addGoods() {
        String itemName = itemNameEditText.getText().toString().trim();
        String quantityStr = quantityEditText.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();

        if (itemName.isEmpty() || quantityStr.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Intent intent = getIntent();
            // Correct usage of getIntExtra()
            int owner_id = intent.getIntExtra("owner_id", -1);
            Log.e("TAG", "owner_id is : "+owner_id);
            int quantity = Integer.parseInt(quantityStr);
            // Assuming totalQuantity and currentQuantity are the same initially
            int currentQuantity = quantity;

            // Add the item to the database
            Log.e("TAG", "owner_id is : "+owner_id+" itemName "+itemName+" quantity "+quantity+" currentQuantity "+currentQuantity+" date "+date);
            db.addRationGoods(owner_id,itemName, quantity, currentQuantity, date);
            loadRationGoods();

            // Clear the input fields
            itemNameEditText.setText("");
            quantityEditText.setText("");
            dateEditText.setText("");

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Quantity must be a number.", Toast.LENGTH_SHORT).show();
        }
    }


    private void loadRationGoods() {
        List<Object> items = new ArrayList<>();
        Intent intent = getIntent();
        int owner_id = intent.getIntExtra("owner_id", -1);
        Cursor cursor = db.getAllRationGoods(owner_id);

        try {
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Get column index by column name
                int itemNameColumnIndex = cursor.getColumnIndex("item_name");
                int totalQuantityColumnIndex = cursor.getColumnIndex("total_quantity");
                int currentQuantityColumnIndex = cursor.getColumnIndex("current_quantity");
                int dateColumnIndex = cursor.getColumnIndex("date");

                // Check if any column index is invalid
                if (itemNameColumnIndex == -1 || totalQuantityColumnIndex == -1 || currentQuantityColumnIndex == -1 || dateColumnIndex == -1) {
                    Log.e("ownerGoods", "One or more column indexes are invalid. Check your database schema.");
                    return; // Stop execution if columns are not found
                }

                String itemName = cursor.getString(itemNameColumnIndex);
                int totalQuantity = cursor.getInt(totalQuantityColumnIndex);
                int currentQuantity = cursor.getInt(currentQuantityColumnIndex); // Fetch current_quantity
                String date = cursor.getString(dateColumnIndex);

                items.add(new GoodsItem(itemName, totalQuantity, currentQuantity, date)); // Pass current_quantity
            } while (cursor.moveToNext());
        } else {
            Log.e("ownerGoods", "Cursor is empty or null.");
        }
        }catch (Exception e) {
        Log.e("ownerGoods", "Error loading goods.", e);
    } finally {
        if (cursor != null) {
            cursor.close();
        }
    }

        GoodsAdapter adapter = new GoodsAdapter(items);
        goodsRecyclerView.setAdapter(adapter);
    }


}