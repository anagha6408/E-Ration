package com.example.eration;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.database.Cursor;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class AllocateGoodsActivity extends AppCompatActivity {
        private Spinner spinnerItemName, spinnerUser;
        private EditText editQuantity;
        private Button btnAllocate;
        private GoodsListData db;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_allocate_goods);
            spinnerItemName = findViewById(R.id.spinnerItemName);
            editQuantity = findViewById(R.id.editQuantity);
            btnAllocate = findViewById(R.id.btnAllocate);
            spinnerUser = findViewById(R.id.spinnerUser);
            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            Intent intent = getIntent();
            int owner_id = intent.getIntExtra("owner_id", -1);
            Log.e("TAG allo", "owner_id is : "+owner_id);

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    this,
                    R.array.user_types, // Reference to the string-array in strings.xml
                    android.R.layout.simple_spinner_item // Default layout for spinner items
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Layout for dropdown items
            spinnerUser.setAdapter(adapter); // Set the adapter to the spinner

            db = new GoodsListData(this);

            List<Allocation> allocationList = db.getAllAllocations(owner_id);
            Log.d("AllocationAdapter", "Size of allocation list: " + allocationList.size());

            AllocationAdapter allocationAdapter = new AllocationAdapter(this,allocationList);
            recyclerView.setAdapter(allocationAdapter);

            // Populate Spinners
            populateGoodsSpinner();

            db.logTables();


            // Set onClick listener for allocation button
            btnAllocate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    allocateGoods();
                }
            });
        }

        private void populateGoodsSpinner() {
            ArrayList<String> goodsList = new ArrayList<>();
            Intent intent = getIntent();
            int owner_id = intent.getIntExtra("owner_id", -1);
            Cursor cursor = db.getAllGoods(owner_id);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int quantity = cursor.getInt(cursor.getColumnIndex("current_quantity"));
                    if (quantity > 0) {
                        goodsList.add(cursor.getString(cursor.getColumnIndex("item_name")));
                    }
                    //goodsList.add(cursor.getString(cursor.getColumnIndex("item_name"))); // Replace with your column name
                }
                cursor.close();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, goodsList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerItemName.setAdapter(adapter);
        }

    private void allocateGoods() {
        Intent intent = getIntent();
        int owner_id = intent.getIntExtra("owner_id", -1);
        Log.e("TAG", "owner_id is : "+owner_id);
        String itemName = spinnerItemName.getSelectedItem().toString();
        Log.e("TAG>>>", "itemName is : "+itemName);
        String selectedUser = spinnerUser.getSelectedItem().toString();
        Log.e("TAG>>>", "selectedUser is : "+selectedUser);
        int quantity;

        try {
            quantity = Integer.parseInt(editQuantity.getText().toString()); // Parse quantity from EditText
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get available quantity for the selected item
        int availableQuantity = db.getAvailableQuantity(itemName);
        Log.e("TAG","availableQuantity = "+availableQuantity);

        if (quantity > availableQuantity) {
            Toast.makeText(this, "Insufficient quantity available.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Since the spinner already provides the correct user type directly (e.g., "BPL", "APL", "AY")
        String userType = selectedUser;
        // Allocate goods to the user type
        boolean success = db.allocateGoods(owner_id,itemName, quantity, userType);


        if (success) {
            Log.e("Update if success","success");
            // Update the available quantity in the goods table
            int availableQuantityAfterAllocation = availableQuantity - quantity;
            Log.e("*****","success"+availableQuantityAfterAllocation);
            db.updateQuantity(itemName, availableQuantityAfterAllocation);


            Toast.makeText(this, "Goods allocated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error in allocation.", Toast.LENGTH_SHORT).show();
        }
    }

}
