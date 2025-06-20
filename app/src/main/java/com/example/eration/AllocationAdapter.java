package com.example.eration;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class AllocationAdapter extends RecyclerView.Adapter<AllocationAdapter.ViewHolder> {
    private List<Allocation> allocationList;
    private GoodsListData db;
    private Context context;

    // Updated constructor to accept context
    public AllocationAdapter(Context context, List<Allocation> allocationList) {
        this.context = context;
        this.allocationList = allocationList;
        this.db = new GoodsListData(context);  // Initialize GoodsListData with context
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.allocation_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Allocation allocation = allocationList.get(position);
        holder.itemName.setText("Item Name :  " + allocation.getItemName());
        holder.quantity.setText("Item Allocated Quantity : " + String.valueOf(allocation.getQuantity()));
        Log.e("Current checking","Item name :"+ allocation.getItemName()+" Qty is : "+String.valueOf(allocation.getQuantity()));

        // Fetch current quantity from the goods table using the item name
        int currentQuantity = db.getCurrentQuantity(allocation.getItemName());
        Log.e("currentQuantity ---",String.valueOf(currentQuantity));
        holder.tvCurrentQuantity.setText("Item Current Quantity : " + String.valueOf(currentQuantity));
        holder.userType.setText("Category : " + allocation.getUserType());
    }

    @Override
    public int getItemCount() {
        return allocationList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, quantity, tvCurrentQuantity, userType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.tvItemName);
            quantity = itemView.findViewById(R.id.tvQuantity);  // Make sure this ID is correct
            tvCurrentQuantity = itemView.findViewById(R.id.tvCurrentQuantity);  // Ensure this matches the layout ID
            userType = itemView.findViewById(R.id.tvUserType);
        }
}
}
