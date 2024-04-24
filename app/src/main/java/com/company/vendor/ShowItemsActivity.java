package com.company.vendor;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowItemsActivity extends AppCompatActivity {

    private TextView textViewName;
    private DatabaseReference itemsReference;
    private String userName, phoneNumber;
    private double userLatitude, userLongitude;
    private static final double MAX_DISTANCE = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_items);
        textViewName = findViewById(R.id.textViewName);
        // Retrieve data passed from RegistrationActivity
        Intent intent = getIntent();
        if (intent != null) {
            userName = intent.getStringExtra("UserName");
            phoneNumber = intent.getStringExtra("PhoneNumber");
            userLatitude = intent.getDoubleExtra("latitude", 0.0);
            userLongitude = intent.getDoubleExtra("longitude", 0.0);
            textViewName.setText(userName);
            Toast.makeText(this, "The latitude is: " + userLongitude, Toast.LENGTH_SHORT).show();
        }

        // Initialize Firebase Database reference
        itemsReference = FirebaseDatabase.getInstance().getReference().child("items");

        // Retrieve items from Firebase and populate ListView
        retrieveItemsFromFirebase();
    }

    private void retrieveItemsFromFirebase() {
        itemsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Map<String, String>> itemList = new ArrayList<>();



                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String itemName = snapshot.child("itemName").getValue(String.class);
                    String itemPrice = snapshot.child("price").getValue(String.class);
                    String itemUserName = snapshot.child("userName").getValue(String.class);
                    String itemQuantity = snapshot.child("quantity").getValue(String.class);
                    String itemUserPhone = snapshot.child("userPhone").getValue(String.class);
                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);
                    double itemLatitude = snapshot.child("latitude").getValue(Double.class);
                    double itemLongitude = snapshot.child("longitude").getValue(Double.class);

                    // User's coordinates
                    final Location userLocation = new Location("");
                    userLocation.setLatitude(userLatitude);
                    userLocation.setLongitude(userLongitude);

                    // Item's coordinates
                    Location itemLocation = new Location("");
                    itemLocation.setLatitude(itemLatitude);
                    itemLocation.setLongitude(itemLongitude);

                    // Distance between user and item
                    float distance = userLocation.distanceTo(itemLocation);

                    // Create a map to hold item details
                    Map<String, String> itemDetails = new HashMap<>();
                    itemDetails.put("itemName", itemName);
                    itemDetails.put("price", itemPrice);
                    itemDetails.put("userName", itemUserName);
                    itemDetails.put("quantity", itemQuantity);
                    itemDetails.put("userPhone", itemUserPhone);
                    itemDetails.put("imageUrl", imageUrl);

                    // Add the item details to the list
                    if (distance <= MAX_DISTANCE) {
                        itemList.add(itemDetails);
                    }
                }

                // Assuming itemList contains the data retrieved from Firebase
                RecyclerView recyclerView = findViewById(R.id.recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(ShowItemsActivity.this));
                ItemAdapter adapter = new ItemAdapter(itemList);
                recyclerView.setAdapter(adapter);

            }
                @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors or cancelled events
                    if (databaseError != null) {
                        Log.e("FirebaseError", "Database Error: " + databaseError.getMessage());
                    } else {
                        Log.e("FirebaseError", "Data retrieval cancelled.");
                    }
                    // You can also notify the user about the error or log it for debugging purposes
                    Toast.makeText(ShowItemsActivity.this, "Error retrieving data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                }
        });
    }
}
