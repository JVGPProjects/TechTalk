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

public class TherapistListActivity extends AppCompatActivity {

    private static final String TAG = "TherapistListActivity";
    private static final String THERAPIST_ROLE = "therapist";

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private LinearLayout therapistButtonContainer; // Container for therapist buttons
    private List<String> therapistIds; // To hold the therapist IDs
    private List<String> therapistEmails; // To hold the therapist emails
    private List<String> therapistProfileUrls; // To hold the therapist profile image URLs
    private EditText searchEditText;
    private Set<String> uniqueEmails; // To track unique therapist emails

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_therapist_list); // Change to the new layout for therapist list
        FirebaseApp.initializeApp(this);

        therapistButtonContainer = findViewById(R.id.therapistButtonContainer); // Container for therapist buttons
        searchEditText = findViewById(R.id.searchEditText); // EditText for searching therapists

        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize uniqueEmails to track already added emails
        uniqueEmails = new HashSet<>();

        // Fetch therapists from Firestore
        fetchTherapists();

        // Setup search functionality
        setupSearchFunctionality();
    }

    private void fetchTherapists() {
        db.collection("users")
                .whereEqualTo("role", THERAPIST_ROLE)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        therapistIds = new ArrayList<>();
                        therapistEmails = new ArrayList<>();
                        therapistProfileUrls = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String email = document.getString("email");
                            String userId = document.getId();
                            String profileImageUrl = document.getString("profileImageUrl");

                            Log.d(TAG, "Email: " + email + ", UserID: " + userId + ", Profile URL: " + profileImageUrl);

                            if (email != null && !uniqueEmails.contains(email)) {
                                uniqueEmails.add(email);
                                createTherapistButton(email, profileImageUrl, userId);
                                therapistIds.add(userId);
                                therapistEmails.add(email);
                                therapistProfileUrls.add(profileImageUrl);
                            } else {
                                Log.e(TAG, "Email is null or already exists for document ID: " + userId);
                            }
                        }
                    } else {
                        Log.e(TAG, "Error fetching therapists: " + task.getException().getMessage());
                    }
                });
    }

    private void createTherapistButton(String email, String profileImageUrl, String therapistId) {
        // Create the button for the therapist
        Button therapistButton = new Button(this);
        therapistButton.setText(email);

        // Set button layout parameters with no margin
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        therapistButton.setBackgroundColor(getResources().getColor(R.color.bg));
        therapistButton.setBackgroundResource(R.drawable.buttonstate);

        // Set margins to 0 to remove the space between buttons
        layoutParams.setMargins(0, 0, 0, 0);
        therapistButton.setLayoutParams(layoutParams);

        // Set any additional padding to 0, if needed
        therapistButton.setPadding(40, 20, 0, 20);
        therapistButton.setElevation(40);

        // Set an OnClickListener for the button
        therapistButton.setOnClickListener(v -> {
            Intent intent = new Intent(TherapistListActivity.this, TherapistProfileActivity.class);
            intent.putExtra("therapistId", therapistId); // Pass the selected therapist ID
            startActivity(intent);
        });

        // Load the profile image using Glide
        Glide.with(this)
                .load(profileImageUrl)
                .placeholder(R.drawable.pfp1) // Placeholder image if URL is null
                .circleCrop() // Make the profile image circular
                .into(new CustomViewTarget<Button, Drawable>(therapistButton) {
                    @Override
                    protected void onResourceCleared(Drawable placeholder) {
                        therapistButton.setCompoundDrawablesWithIntrinsicBounds(scaleDrawable(placeholder, 50, 50), null, null, null); // Scale the placeholder
                    }

                    @Override
                    public void onLoadFailed(Drawable errorDrawable) {
                        therapistButton.setCompoundDrawablesWithIntrinsicBounds(scaleDrawable(errorDrawable, 50, 50), null, null, null); // Scale the error drawable
                    }

                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        therapistButton.setCompoundDrawablesWithIntrinsicBounds(scaleDrawable(resource, 150, 150), null, null, null); // Scale the loaded image
                    }
                });

        // Adjust the drawable size to fit the button properly
        therapistButton.setCompoundDrawablePadding(20); // Add some padding between text and image
        therapistButton.setTextColor(ContextCompat.getColor(this, R.color.black));
        therapistButton.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START); // Align text to the start

        // Add the button to the therapist button container
        therapistButtonContainer.addView(therapistButton);
    }

    // Helper method to scale the drawable
    private Drawable scaleDrawable(Drawable drawable, int width, int height) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        return new BitmapDrawable(getResources(), scaledBitmap);
    }

    private void setupSearchFunctionality() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTherapistButtons(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed after text changes
            }
        });
    }

    private void filterTherapistButtons(String query) {
        therapistButtonContainer.removeAllViews(); // Clear existing buttons
        for (int i = 0; i < therapistEmails.size(); i++) {
            String therapistEmail = therapistEmails.get(i);
            if (therapistEmail.toLowerCase().contains(query.toLowerCase())) {
                createTherapistButton(therapistEmail, therapistProfileUrls.get(i), therapistIds.get(i)); // Recreate the button with filtered email and profile image
            }
        }
    }

    // Back button click handler
    public void onBackButtonClick(View view) {
        onBackPressed();
    }
}
