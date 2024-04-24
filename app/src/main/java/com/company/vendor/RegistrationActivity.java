package com.company.vendor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.concurrent.TimeUnit;

public class RegistrationActivity extends AppCompatActivity {

    private EditText editTextPhoneNumber, editTextName, editTextOTP;
    private Button buttonRegister, buttonVerifyOTP;
    private FirebaseAuth firebaseAuth;
    private String verificationId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        editTextName = findViewById(R.id.editTextName);
        buttonRegister = findViewById(R.id.buttonRegister);
        firebaseAuth = FirebaseAuth.getInstance();
        buttonVerifyOTP = findViewById(R.id.buttonVerifyOTP);
        editTextOTP = findViewById(R.id.editTextOTP);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = editTextPhoneNumber.getText().toString().trim();
                String fullName = editTextName.getText().toString().trim();

                if (phoneNumber.isEmpty() || fullName.isEmpty()) {
                    Toast.makeText(RegistrationActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    startPhoneNumberVerification(phoneNumber);
                    // You can proceed to verification or registration after sending verification code
                }
            }
        });

        // Click listener for the button to send OTP or verify it
        buttonVerifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = editTextPhoneNumber.getText().toString().trim();
                String enteredOTP = editTextOTP.getText().toString().trim();

                if (enteredOTP.isEmpty()) {
                    // If OTP field is empty, send OTP request
                    startPhoneNumberVerification(phoneNumber);
                } else {
                    // If OTP field is filled, verify the entered OTP
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, enteredOTP);
                    signInWithPhoneAuthCredential(credential); // Custom method to verify the credential
                }
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, handle UI accordingly
                            FirebaseUser user = task.getResult().getUser();
                            // User is authenticated, proceed with your app's logic
                            // For example, navigate to the main app screen
                            Toast.makeText(RegistrationActivity.this, "Authentication successed", Toast.LENGTH_SHORT).show();

                            // Get the user's name and phone number from EditText fields
                            String userName = editTextName.getText().toString().trim();
                            String phoneNumber = editTextPhoneNumber.getText().toString().trim();
                            // User is authenticated, start MainActivity and pass data via Intent
                            Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                            intent.putExtra("UserName", userName);
                            intent.putExtra("PhoneNumber", phoneNumber);
                            startActivity(intent);
                            finish(); // Optional: Finish the current activity to prevent going back to it on back press

                        } else {
                            // Sign in failed, display a message to the user.
                            Toast.makeText(RegistrationActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            // You might want to allow the user to retry or handle the failure scenario accordingly
                        }
                    }
                });
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        // This callback will be invoked in two situations:
                        // 1 - Instant verification. In some cases, where the phone number is instantly
                        //     verified without needing to send or enter a verification code.
                        // 2 - Auto-retrieval. On some devices, Google Play services can automatically
                        //     detect the incoming verification SMS and perform verification without
                        //     user action.

                        // You can automatically sign in the user or proceed to registration here
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        // This callback is invoked if an invalid request for verification is made,
                        // for instance if the phone number format is not valid.
                        Toast.makeText(RegistrationActivity.this, "Verification Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        // The SMS verification code has been sent to the provided phone number
                        verificationId = s;
                        // Proceed to enter the verification code sent to the user's phone
                        // Display a toast message indicating OTP has been sent
                        Toast.makeText(RegistrationActivity.this, "OTP has been sent", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
