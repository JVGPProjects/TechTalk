package com.example.techtalk;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.view.View;

public class AccountNavActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private TextView emailTextView;
    private TextView usernameTextView;
    private TextView profileUsernameTextView;
    private BottomNavigationView bottomNavigationView;
    private ImageView profileImageButton;
    private String previousProfileImageUrl; // Store the previous profile image URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_nav);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance(); // Initialize Firebase Storage
        storageRef = storage.getReference(); // Get a reference to the storage

        profileImageButton = findViewById(R.id.profileImageButton);
        emailTextView = findViewById(R.id.emailTextView);
        usernameTextView = findViewById(R.id.usernameTextView);
        profileUsernameTextView = findViewById(R.id.profileUsernameTextView);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setSelectedItemId(R.id.navigation_account);

        fetchUserData(); // Load user data on activity creation

        // BottomNavigationView item selection
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.navigation_account) {
                return true;
            } else if (id == R.id.navigation_home) {
                startActivity(new Intent(AccountNavActivity.this, WelcomeActivity.class));
                return true; // Already on the home page
            } else if (id == R.id.navigation_lock) {
                startActivity(new Intent(AccountNavActivity.this, LockActivity.class));
                return true;
            } else {
                return false;
            }
        });
    }

    private void fetchUserData() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    emailTextView.setText(mAuth.getCurrentUser().getEmail());
                    usernameTextView.setText(documentSnapshot.getString("username"));
                    profileUsernameTextView.setText(documentSnapshot.getString("username"));
                    previousProfileImageUrl = documentSnapshot.getString("profileImageUrl");

                    // Load the latest profile image
                    if (previousProfileImageUrl != null && !previousProfileImageUrl.isEmpty()) {
                        Glide.with(this)
                                .load(previousProfileImageUrl)
                                .circleCrop()
                                .into(profileImageButton);
                    } else {
                        profileImageButton.setImageResource(R.drawable.pfp1); // Set a default image if none exists
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(AccountNavActivity.this, "Failed to fetch user data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

}
