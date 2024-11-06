package com.example.techtalk;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LockActivity extends AppCompatActivity {

    private static final String TAG = "LockActivity"; // Tag for logging
    private String userRole; // Variable to store the user role
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        EditText box1 = findViewById(R.id.box1);
        EditText box2 = findViewById(R.id.box2);
        EditText box3 = findViewById(R.id.box3);
        EditText box4 = findViewById(R.id.box4);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        bottomNavigationView.setSelectedItemId(R.id.navigation_lock);

        // Add TextWatchers to each box
        setBoxWatcher(box1, box2);
        setBoxWatcher(box2, box3);
        setBoxWatcher(box3, box4);

        // Set a TextWatcher for the last box to handle password verification
        box4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    String enteredPassword = box1.getText().toString() +
                            box2.getText().toString() +
                            box3.getText().toString() +
                            box4.getText().toString();
                    Log.d(TAG, "Entered Password: " + enteredPassword);

                    boolean isNewUser = (mAuth.getCurrentUser() != null) &&
                            mAuth.getCurrentUser().getMetadata().getCreationTimestamp() ==
                                    mAuth.getCurrentUser().getMetadata().getLastSignInTimestamp();

                    if (isNewUser) {
                        handleNewUserPassword(enteredPassword);
                    } else {
                        verifyExistingPassword(enteredPassword);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // BottomNavigationView item selection
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_account) {
                startActivity(new Intent(LockActivity.this, AccountNavActivity.class));
                return true;
            } else if (id == R.id.navigation_home) {
                startActivity(new Intent(LockActivity.this, WelcomeActivity.class));
                return true;
            } else if (id == R.id.navigation_lock) {
                return true;
            } else {
                return false;
            }
        });
    }

    private void setBoxWatcher(EditText currentBox, EditText nextBox) {
        currentBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    nextBox.requestFocus();  // Move focus to the next box
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void handleNewUserPassword(String enteredPassword) {
        if (enteredPassword.length() == 4 && enteredPassword.matches("\\d+")) { // Check if it's numeric
            savePasswordToFirebase(enteredPassword); // Save to Firebase
        } else {
            Toast.makeText(LockActivity.this, "Password must be a 4-digit number", Toast.LENGTH_SHORT).show();
        }
    }

    private void savePasswordToFirebase(String password) {
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("users").document(userId)
                .update("password", password)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Password saved to Firebase.");
                    Toast.makeText(LockActivity.this, "Password set successfully", Toast.LENGTH_SHORT).show();
                    navigateToNextActivity(true);  // Pass true for new users
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "Error saving password: " + e.getMessage());
                    Toast.makeText(LockActivity.this, "Error saving password", Toast.LENGTH_SHORT).show();
                });
    }

    private void verifyExistingPassword(String enteredPassword) {
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String savedPassword = documentSnapshot.getString("password");
                        if (enteredPassword.equals(savedPassword)) {
                            Toast.makeText(LockActivity.this, "Password correct", Toast.LENGTH_SHORT).show();
                            navigateToNextActivity(false);  // Pass false for existing users
                        } else {
                            Toast.makeText(LockActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "Error fetching password: " + e.getMessage());
                    Toast.makeText(LockActivity.this, "Error verifying password", Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateToNextActivity(boolean isNewUser) {
        Log.d(TAG, "Navigating to next activity by checking role in Firebase.");

        String userId = mAuth.getCurrentUser().getUid();  // Get the current user ID

        // Fetch the user document from Firestore
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Get the role from the Firestore document
                        userRole = documentSnapshot.getString("role");

                        if (userRole == null) {
                            Log.d(TAG, "User role not found in Firestore.");
                            Toast.makeText(this, "User role not found in database. Please try again.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Log.d(TAG, "User role from Firestore: " + userRole);
                        Intent intent;

                        if (isNewUser) {
                            if ("therapist".equals(userRole)) {
                                intent = new Intent(LockActivity.this, TherapistLockTutorialActivity.class); // Tutorial for therapists
                            } else if ("parent".equals(userRole)) {
                                intent = new Intent(LockActivity.this, LockTutorialActivity.class); // Tutorial for parents
                            } else {
                                Log.d(TAG, "User role not recognized, staying in LockActivity.");
                                Toast.makeText(this, "User role not recognized.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } else {
                            if ("therapist".equals(userRole)) {
                                intent = new Intent(LockActivity.this, TherapistHomeActivity.class);
                            } else if ("parent".equals(userRole)) {
                                intent = new Intent(LockActivity.this, ParentHomeActivity.class);
                            } else {
                                Log.d(TAG, "User role not recognized, staying in LockActivity.");
                                Toast.makeText(this, "User role not recognized.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        startActivity(intent);
                        finish();  // Finish the current activity after starting the new one

                    } else {
                        Log.d(TAG, "User document does not exist in Firestore.");
                        Toast.makeText(this, "User not found in database.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "Error fetching user role from Firestore: " + e.getMessage());
                    Toast.makeText(this, "Error checking user role.", Toast.LENGTH_SHORT).show();
                });
    }
}
