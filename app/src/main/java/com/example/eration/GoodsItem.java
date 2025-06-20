package com.example.eration;
public class GoodsItem {
    private String itemName;
    private int total_quantity;
    private int curret_quantity;

    private String date; // Assuming date is stored as a String

    public GoodsItem(String itemName, int total_quantity, int current_quantity, String date) {
        this.itemName = itemName;
        this.total_quantity = total_quantity;
        this.curret_quantity = current_quantity; // Correct spelling if needed
        this.date = date;
    }


    public String getItemName() {
        return itemName;
    }


    public int getTotal_quantity() {
        return total_quantity;
    }

    public int getCurrent_quantity() {
        return curret_quantity;
    }

    public String getDate() {
        return date;
    }
}

