package com.example.techtalk;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangeFourDigitPasswordActivity extends AppCompatActivity {
    private EditText currentFourDigitPasswordEditText, newFourDigitPasswordEditText;
    private Button saveButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_four_digit_password);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        currentFourDigitPasswordEditText = findViewById(R.id.currentFourDigitPasswordEditText);
        newFourDigitPasswordEditText = findViewById(R.id.newFourDigitPasswordEditText);
        saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(v -> changePassword());
    }

    // Back button click handler
    public void onBackButtonClick(View view) {
        // Close the current activity and return to the previous one
        onBackPressed();
    }

    private void changePassword() {
        String currentFourDigitPassword = currentFourDigitPasswordEditText.getText().toString();
        String newFourDigitPassword = newFourDigitPasswordEditText.getText().toString();

        if (TextUtils.isEmpty(currentFourDigitPassword) || TextUtils.isEmpty(newFourDigitPassword)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        // Fetch the saved password from Firestore
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String savedFourDigitPassword = documentSnapshot.getString("password");

                        if (!currentFourDigitPassword.equals(savedFourDigitPassword)) {
                            Toast.makeText(this, "Current 4-digit password is incorrect", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Update the new password in Firestore
                        updatePasswordInFirebase(newFourDigitPassword);
                    } else {
                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updatePasswordInFirebase(String newPassword) {
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("users").document(userId)
                .update("password", newPassword)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "4-Digit Password changed successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error updating password: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
