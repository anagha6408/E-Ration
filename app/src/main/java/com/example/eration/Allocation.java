package com.example.eration;

import android.util.Log;

public class Allocation {
    private String itemName;
    private int allocatedQuantity;
    private String userType;

    public Allocation( String itemName, int allocatedQuantity, String userType) {
        this.itemName = itemName;
        this.allocatedQuantity = allocatedQuantity;
        this.userType = userType;
    }

    // Getters
    public String getItemName() { return itemName; }
    public int getQuantity() {
        Log.d("TTT","allocatedQuantity in Allocation list "+allocatedQuantity);
        return allocatedQuantity; }
    public String getUserType() { return userType; }
}
