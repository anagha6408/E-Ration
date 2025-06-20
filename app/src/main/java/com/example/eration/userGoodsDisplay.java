package com.example.eration;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class userGoodsDisplay extends AppCompatActivity {

    ListView goodsListView;
    private GoodsListData db2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_goods_display);
        goodsListView = findViewById(R.id.goods_list_view);
        db2 = new GoodsListData(this);
        Intent intent = getIntent();
        String owner_id = intent.getStringExtra("owner_id");
        Log.d("userGoodsDisplay", "Received owner_id: " + owner_id);
        String loggedInUsertypes = intent.getStringExtra("loggedInUsertypes");
        Log.d("userGoodsDisplay", "Received loggedInUsertypes: " + loggedInUsertypes);

        Cursor cursor = db2.getGoodsByUserType(loggedInUsertypes);
        ArrayList<String> goodsList = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            try {
                // Iterate through the cursor
                do {
                    // Safely access the cursor columns
                    String itemName = cursor.getString(cursor.getColumnIndexOrThrow("item_name"));
                    int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("current_quantity"));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                    int allocatedQuantity = cursor.getInt(cursor.getColumnIndexOrThrow("allocated_quantity"));

                    String itemDetails = "Item: " + itemName + " \nAllocated Quantity for this month: " + allocatedQuantity +
                            "\nDate of goods arrival: " + date +
                            "\nRemaining Quantity: " + quantity;
                    goodsList.add(itemDetails); // Add to list

                    // Log or use the data
                    Log.d("UserActivity", "Item: " + itemName +
                            ", Date: " + date +
                            ", Allocated Quantity: " + allocatedQuantity +
                            ", Remaining Quantity: " + quantity);
                } while (cursor.moveToNext()); // Move to the next row
            } catch (Exception e) {
                Log.e("UserActivity", "Error accessing cursor data", e);
            } finally {
                cursor.close();
            }

            // Set the adapter for ListView
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, goodsList);
            goodsListView.setAdapter(adapter);
        } else {
            Log.e("UserActivity", "Cursor is empty or null");
        }

    }
}