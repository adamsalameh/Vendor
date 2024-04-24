package com.company.vendor;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.company.vendor.AddItemActivity;
import com.company.vendor.ShowItemsActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {

    private TextView textViewName;
    private Button buttonAdd, buttonShow;
    private static final int REQUEST_LOCATION_PERMISSION = 1001;
    private FusedLocationProviderClient fusedLocationClient;
    private String userName, phoneNumber;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonShow = findViewById(R.id.buttonShow);
        textViewName = findViewById(R.id.textViewName);

        // Check and request location permission if not granted
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            // Permission already granted, request location updates
            requestLocationUpdates();
        }


        // Retrieve data passed from RegistrationActivity
        Intent intent = getIntent();
        if (intent != null) {
            userName = intent.getStringExtra("UserName");
            phoneNumber = intent.getStringExtra("PhoneNumber");
            textViewName.setText(userName);
            // Set a click listener for the buttonAdd to navigate to the AddItemActivity
            buttonAdd.setOnClickListener(view -> {
                Intent addItemIntent = new Intent(MainActivity.this, AddItemActivity.class);
                addItemIntent.putExtra("UserName", userName);
                addItemIntent.putExtra("PhoneNumber", phoneNumber);
                addItemIntent.putExtra("latitude", latitude);
                addItemIntent.putExtra("longitude", longitude);
                startActivity(addItemIntent);
                // Optionally, you can finish the MainActivity to prevent going back to it on back press
                // finish();
            });
            buttonShow.setOnClickListener(view -> {
                Intent showItemsIntent = new Intent(MainActivity.this, ShowItemsActivity.class);
                showItemsIntent.putExtra("UserName", userName);
                showItemsIntent.putExtra("PhoneNumber", phoneNumber);
                showItemsIntent.putExtra("latitude", latitude);
                showItemsIntent.putExtra("longitude", longitude);
                startActivity(showItemsIntent);
            });

        }

    }
    // Request location updates
    private void requestLocationUpdates() {
        // Check if the app has permission to access the device's location
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, proceed to get the last known location
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            // Use latitude and longitude values here or pass them to another method
                            // for further processing related to displaying items within range, etc.
                            Toast.makeText(MainActivity.this, "Your latitude is: "+ latitude + "Your longitude is: "+ longitude, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(this, e -> {
                        // Handle failure to get location
                        Toast.makeText(MainActivity.this, "Error couldn't get the location", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Permission is not granted, request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    // Handle permission request response
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, request location updates
                requestLocationUpdates();
            } else {
                // Permission denied, handle accordingly (e.g., show a message)
                Toast.makeText(MainActivity.this, "Error Permission denied to get the location", Toast.LENGTH_SHORT).show();
            }
        }
    }
}