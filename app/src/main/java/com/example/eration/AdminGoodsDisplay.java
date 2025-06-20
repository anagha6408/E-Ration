package com.example.eration;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AdminGoodsDisplay extends AppCompatActivity {

    ListView goodsListView;
    private MyDatabase db;
    private GoodsListData db2;
    private String loggedInUsertypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_goods_display);

        db = new MyDatabase(this);
        db2 = new GoodsListData(this);
        // Display all goods
        setupGoodsButtons();
    }

    private void setupGoodsButtons() {
        LinearLayout buttonContainer = findViewById(R.id.buttonContainer);

        if (buttonContainer == null) {
            Log.e("AdminGoodsDisplay", "Button container not found in layout");
            return;


        }

        buttonContainer.removeAllViews(); // Clear previous buttons

        // Fetch all owner IDs
        List<String> ownerIds = db.getAllOwnerIds(); // This method should return a list of all owner IDs
        if (ownerIds == null || ownerIds.isEmpty()) {
            Log.e("AdminGoodsDisplay", "No owner IDs found");
            Toast.makeText(this, "No owners available", Toast.LENGTH_SHORT).show();
            return;
        }
        // Create a button for each owner ID
        for (String ownerId : ownerIds) {
            Button ownerButton = new Button(this);
            ownerButton.setText("Owner ID: " + ownerId);
            ownerButton.setTextSize(18);
            ownerButton.setTextColor(getResources().getColor(R.color.white));
            ownerButton.setBackground(getResources().getDrawable(R.drawable.button_style));
            ownerButton.setTag(ownerId);

            // Set onClickListener for the owner button
            ownerButton.setOnClickListener(v -> {
                String clickedOwnerId = (String) v.getTag(); // Retrieve owner ID from the button's tag
                Log.d("AdminGoodsDisplay", "Fetching goods for Owner ID: " + clickedOwnerId);

                // Fetch goods for the clicked owner
                Cursor cursor = db2.getGoodsByOwnerId(clickedOwnerId); // Method to fetch goods for a specific owner ID
                if (cursor != null && cursor.moveToFirst()) {
                    try {
                        List<String> goodsDetailsList = new ArrayList<>();
                        do {
                            // Retrieve goods details
                            String itemName = cursor.getString(cursor.getColumnIndexOrThrow("item_name"));
                            int currentQuantity = cursor.getInt(cursor.getColumnIndexOrThrow("current_quantity"));
                            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                            int allocatedQuantity = cursor.getInt(cursor.getColumnIndexOrThrow("allocated_quantity"));

                            // Combine details into a single string
                            String goodsDetails = "Item: " + itemName +
                                    "\nCurrent Quantity: " + currentQuantity +
                                    "\nDate: " + date +
                                    "\nAllocated Quantity: " + allocatedQuantity;
                            goodsDetailsList.add(goodsDetails);
                        } while (cursor.moveToNext());

                        // EDIT: Display goods details in a ListView inside a dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Goods for Owner ID: " + clickedOwnerId);
                        // Inflate custom layout for ListView
                        LayoutInflater inflater = LayoutInflater.from(this);
                        View dialogView = inflater.inflate(R.layout.activity_admin_goods_display, null);

                        // Initialize and populate the ListView
                        ListView listView = dialogView.findViewById(R.id.goods_list_view);
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, goodsDetailsList);
                        listView.setAdapter(adapter);

                        // Set the custom view in the dialog
                        builder.setView(dialogView);
                        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                        builder.show();

                    } catch (Exception e) {
                        Log.e("AdminGoodsDisplay", "Error processing goods data for owner ID: " + clickedOwnerId, e);
                    } finally {
                        cursor.close();
                    }
                } else {
                    Log.e("AdminGoodsDisplay", "No goods found for Owner ID: " + clickedOwnerId);
                    Toast.makeText(this, "No goods available for this owner", Toast.LENGTH_SHORT).show();
                }
            });

            // Set layout parameters for the button
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 10, 0, 10); // Add margin
            ownerButton.setLayoutParams(params);

            // Add the button to the container
            buttonContainer.addView(ownerButton);
        }
    }
}