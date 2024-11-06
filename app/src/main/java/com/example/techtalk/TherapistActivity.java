package com.example.techtalk;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TherapistActivity extends AppCompatActivity {

    private static final String TAG = "TherapistActivity";
    private static final String PARENT_ROLE = "parent";
    private static final int MIN_REPORT_LENGTH = 10;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private EditText reportEditText;
    private BottomNavigationView bottomNavigationView;
    private LinearLayout parentButtonContainer; // Container for parent buttons
    private List<String> parentIds; // To hold the parent IDs
    private List<String> parentEmails; // To hold the parent emails
    private List<String> parentProfileUrls; // To hold the parent profile image URLs
    private EditText searchEditText;
    private Set<String> uniqueEmails; // To track unique parent emails

    private String currentUserRole; // To track the role of the current user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_therapist);
        FirebaseApp.initializeApp(this);

        parentButtonContainer = findViewById(R.id.parentButtonContainer);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        searchEditText = findViewById(R.id.searchEditText);

        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize uniqueEmails to track already added emails
        uniqueEmails = new HashSet<>();

        // Check the current user's role and display the appropriate UI
        checkUserRole();

        // Setup search functionality
        setupSearchFunctionality();

        bottomNavigationView.setSelectedItemId(R.id.navigation_report);

        // BottomNavigationView item selection
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.navigation_report) {
                return true;
            } else if (id == R.id.navigation_home) {
                startActivity(new Intent(TherapistActivity.this, WelcomeActivity.class));
                return true;
            } else if (id == R.id.navigation_settings) {
                startActivity(new Intent(TherapistActivity.this, SettingsActivity.class));
                return true;
            } else {
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.navigation_report); // Ensure 'report' is always selected
    }

    private void checkUserRole() {
        // Get the current user
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String role = task.getResult().getString("role");
                        currentUserRole = role;

                        // Based on the role, show the appropriate UI
                        if ("therapist".equals(role)) {
                            fetchParents(); // If therapist, show the parent list
                        } else if ("parent".equals(role)) {
                            fetchParents(); // If parent, show only their email
                        }
                    } else {
                        Log.e(TAG, "Error fetching user role: " + task.getException().getMessage());
                    }
                });
    }
    // Back button click handler
    public void onBackButtonClick(View view) {
        onBackPressed();
    }


    private void fetchParents() {
        db.collection("users")
                .whereEqualTo("role", PARENT_ROLE)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        parentIds = new ArrayList<>();
                        parentEmails = new ArrayList<>();
                        parentProfileUrls = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String email = document.getString("email");
                            String userId = document.getId();
                            String profileImageUrl = document.getString("profileImageUrl");

                            Log.d(TAG, "Email: " + email + ", UserID: " + userId + ", Profile URL: " + profileImageUrl);

                            if (email != null && !uniqueEmails.contains(email)) {
                                uniqueEmails.add(email);
                                createParentButton(email, profileImageUrl, userId);
                                parentIds.add(userId);
                                parentEmails.add(email);
                                parentProfileUrls.add(profileImageUrl);
                            } else {
                                Log.e(TAG, "Email is null or already exists for document ID: " + userId);
                            }
                        }
                    } else {
                        Log.e(TAG, "Error fetching parents: " + task.getException().getMessage());
                    }
                });
    }

    private void createParentButton(String email, String profileImageUrl, String parentId) {
        // Create the button for the parent
        Button parentButton = new Button(this);
        parentButton.setText(email);

        // Set button layout parameters with no margin
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        parentButton.setBackgroundColor(getResources().getColor(R.color.bg));
        parentButton.setBackgroundResource(R.drawable.buttonstate);

        layoutParams.setMargins(0, 0, 0, 0);
        parentButton.setLayoutParams(layoutParams);

        parentButton.setPadding(40, 20, 0, 20);
        parentButton.setElevation(40);

        // Set an OnClickListener for the button
        parentButton.setOnClickListener(v -> {
            Intent intent = new Intent(TherapistActivity.this, ChatActivity.class);
            intent.putExtra("parentId", parentId); // Pass the selected parent ID
            startActivity(intent);
        });

        // Check if profileImageUrl is null or empty, use default image if true
        String imageUrlToUse = (profileImageUrl == null || profileImageUrl.isEmpty())
                ? "android.resource://com.example.techtalk/" + R.drawable.pfp1 // Default image
                : profileImageUrl;

        // Load the profile image using Glide
        Glide.with(this)
                .load(imageUrlToUse)
                .placeholder(R.drawable.pfp1) // This will be the placeholder image
                .circleCrop() // Ensures the image is circular
                .into(new CustomViewTarget<Button, Drawable>(parentButton) {
                    @Override
                    protected void onResourceCleared(Drawable placeholder) {
                        parentButton.setCompoundDrawablesWithIntrinsicBounds(scaleDrawable(placeholder, 50, 50), null, null, null);
                    }

                    @Override
                    public void onLoadFailed(Drawable errorDrawable) {
                        parentButton.setCompoundDrawablesWithIntrinsicBounds(scaleDrawable(errorDrawable, 50, 50), null, null, null);
                    }

                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        parentButton.setCompoundDrawablesWithIntrinsicBounds(scaleDrawable(resource, 150, 150), null, null, null);
                    }
                });

        // Adjust the drawable size to fit the button properly
        parentButton.setCompoundDrawablePadding(20);
        parentButton.setTextColor(ContextCompat.getColor(this, R.color.black));
        parentButton.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        parentButtonContainer.addView(parentButton);
    }

    private Drawable scaleDrawable(Drawable drawable, int width, int height) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        return new BitmapDrawable(getResources(), scaledBitmap);
    }

    private void setupSearchFunctionality() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String query = searchEditText.getText().toString().toLowerCase();
                filterParents(query);
            }

            @Override
            public void afterTextChanged(Editable editable) {}

        });
    }

    private void filterParents(String query) {
        // Filter the parent buttons based on the search query
        parentButtonContainer.removeAllViews();
        for (int i = 0; i < parentEmails.size(); i++) {
            if (parentEmails.get(i).toLowerCase().contains(query)) {
                createParentButton(parentEmails.get(i), parentProfileUrls.get(i), parentIds.get(i));
            }
        }
    }
}
