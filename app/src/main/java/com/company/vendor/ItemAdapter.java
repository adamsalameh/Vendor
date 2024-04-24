package com.company.vendor;
import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;



public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private List<Map<String, String>> itemList;

    public ItemAdapter(List<Map<String, String>> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Map<String, String> currentItem = itemList.get(position);

        // Bind data to views in the item layout
        holder.itemNameTextView.setText(currentItem.get("itemName"));
        holder.itemPriceTextView.setText("Price: $" + currentItem.get("price"));
        holder.itemQuantityTextView.setText("Quantity: " + currentItem.get("quantity"));

        // Load image using Picasso (replace "image_url_key" with the actual key)
        String imageUrl = currentItem.get("imageUrl");
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(holder.itemImageView);
        }

        // Set onClickListener for the "Call Seller" button
        holder.callSellerButton.setOnClickListener(view -> {
            String phoneNumber = currentItem.get("userPhone");
            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameTextView, itemPriceTextView, itemQuantityTextView;
        ImageView itemImageView;
        Button callSellerButton; // Button to call the seller

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.item_name_text_view);
            itemPriceTextView = itemView.findViewById(R.id.item_price_text_view);
            itemQuantityTextView = itemView.findViewById(R.id.item_quantity_text_view);
            itemImageView = itemView.findViewById(R.id.item_image_view);
            callSellerButton = itemView.findViewById(R.id.buttonCallSeller); // Initialize the button
        }
    }
}