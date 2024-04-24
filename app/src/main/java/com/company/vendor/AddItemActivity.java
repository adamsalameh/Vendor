package com.company.vendor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AddItemActivity extends AppCompatActivity {
    private DatabaseReference itemsReference;
    private TextView textViewName;
    private EditText editTextItemName;
    private EditText editTextQuantity;
    private EditText editTextPrice;
    private Button buttonSave, buttonSelectFromGallery;
    private ImageView imageViewItem;
    private String userName, phoneNumber;
    private double latitude, longitude;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    // Firebase Storage
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        itemsReference = database.getReference("items");

        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        textViewName = findViewById(R.id.textViewName);
        editTextItemName = findViewById(R.id.editTextItemName);
        editTextQuantity = findViewById(R.id.editTextQuantity);
        editTextPrice = findViewById(R.id.editTextPrice);
        buttonSave = findViewById(R.id.buttonSave);
        buttonSelectFromGallery = findViewById(R.id.buttonSelectFromGallery);
        imageViewItem = findViewById(R.id.imageViewItem);

        buttonSelectFromGallery.setOnClickListener(view -> selectImageFromGallery());

        // Retrieve data passed from RegistrationActivity
        Intent intent = getIntent();
        if (intent != null) {
            userName = intent.getStringExtra("UserName");
            phoneNumber = intent.getStringExtra("PhoneNumber");
            latitude = intent.getDoubleExtra("latitude", 0.0);
            longitude = intent.getDoubleExtra("longitude", 0.0);
            Toast.makeText(this, "The latitude is: " + latitude, Toast.LENGTH_SHORT).show();

            textViewName.setText(userName);
        }

        // Click listener for the save button
        buttonSave.setOnClickListener(view -> saveItem());
    }

    private void saveItem() {
        String itemName = editTextItemName.getText().toString().trim();
        String quantity = editTextQuantity.getText().toString().trim();
        String price = editTextPrice.getText().toString().trim();

        // Generate a unique key for the new item
        String newItemKey = itemsReference.push().getKey();

        // Prepare data for the new item
        Map<String, Object> newItem = new HashMap<>();
        newItem.put("userName", userName);
        newItem.put("userPhone", phoneNumber);
        newItem.put("itemName", itemName);
        newItem.put("quantity", quantity);
        newItem.put("price", price);
        newItem.put("latitude", latitude);
        newItem.put("longitude", longitude);

        if (imageUri != null) {
            // Upload the image to Firebase Storage
            StorageReference imageRef = storageReference.child("item_images/" + newItemKey);
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get the download URL of the uploaded image
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            // Add the image URL to the item data
                            newItem.put("imageUrl", imageUrl);

                            // Save item details to the database
                            itemsReference.child(newItemKey).setValue(newItem)
                                    .addOnSuccessListener(aVoid -> {
                                        // Item added successfully
                                        Toast.makeText(getApplicationContext(), "Item added successfully", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Item addition failed
                                        Toast.makeText(getApplicationContext(), "Failed to add item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Image upload failed
                        Toast.makeText(getApplicationContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            // If no image is selected, save other item details without the image URL
            itemsReference.child(newItemKey).setValue(newItem)
                    .addOnSuccessListener(aVoid -> {
                        // Item added successfully
                        Toast.makeText(getApplicationContext(), "Item added successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Item addition failed
                        Toast.makeText(getApplicationContext(), "Failed to add item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageViewItem.setImageURI(imageUri);
        }
    }
}
