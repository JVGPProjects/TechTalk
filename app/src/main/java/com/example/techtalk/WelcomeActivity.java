package com.example.techtalk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.TooltipCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class WelcomeActivity extends AppCompatActivity {

    private TextView welcomeMessage;
    private ImageButton logoButton;
    private ImageButton ttsButton;
    private ImageButton conversationButton;
    private ImageButton moodButton;
    private ImageButton signLanguageButton;
    private ImageButton familyButton;
    private View animalButton;
    private View shapeButton;
    private View colorButton;
    private View buildingButton;
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        welcomeMessage = findViewById(R.id.welcomeText);
        logoButton = findViewById(R.id.logoButton);
        ttsButton = findViewById(R.id.ttsButton);
        conversationButton = findViewById(R.id.conversationButton);
        moodButton = findViewById(R.id.moodButton);
        signLanguageButton = findViewById(R.id.signLanguageButton);
        familyButton = findViewById(R.id.familyButton);
        animalButton = findViewById(R.id.animalButton);
        shapeButton = findViewById(R.id.shapeButton);
        colorButton = findViewById(R.id.colorButton);
        buildingButton = findViewById(R.id.buildingButton);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance(); // Initialize Firestore

        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        // Get the username from the intent
        String username = getIntent().getStringExtra("username");

        // Fetch user data from Firestore
        fetchUserData();

        // Set click listeners for buttons7
        logoButton.setOnClickListener(v -> {
            // Create and show the tooltip
            Toast.makeText(WelcomeActivity.this, "TechTalk", Toast.LENGTH_SHORT).show();
        });


        ttsButton.setOnClickListener(v -> startActivity(new Intent(WelcomeActivity.this, TextToSpeechActivity.class)));
        conversationButton.setOnClickListener(v -> startActivity(new Intent(WelcomeActivity.this, ConversationActivity.class)));
        moodButton.setOnClickListener(v -> startActivity(new Intent(WelcomeActivity.this, MoodActivity.class)));
        signLanguageButton.setOnClickListener(v -> startActivity(new Intent(WelcomeActivity.this, SignLanguageActivity.class)));
        familyButton.setOnClickListener(v -> startActivity(new Intent(WelcomeActivity.this, FamilyActivity.class)));
        animalButton.setOnClickListener(v -> startActivity(new Intent(WelcomeActivity.this, AnimalsActivity.class)));
        shapeButton.setOnClickListener(v -> startActivity(new Intent(WelcomeActivity.this, ShapesActivity.class)));
        colorButton.setOnClickListener(v -> startActivity(new Intent(WelcomeActivity.this, ColorsActivity.class)));
        buildingButton.setOnClickListener(v -> startActivity(new Intent(WelcomeActivity.this, BuildingActivity.class)));

        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        // BottomNavigationView item selection
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.navigation_account) {
                startActivity(new Intent(WelcomeActivity.this, AccountNavActivity.class));
                return true;
            } else if (id == R.id.navigation_home) {
                return true; // Already on the home page
            } else if (id == R.id.navigation_lock) {
                Intent intentUserProfile = new Intent(WelcomeActivity.this, LockActivity.class);
                intentUserProfile.putExtra("username", username);
                startActivity(intentUserProfile);
                return true;
            } else {
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.navigation_home); // Ensure 'report' is always selected
    }

    private void fetchUserData() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    String username = document.getString("username");
                    welcomeMessage.setText("Welcome, " + username + "!");
                } else {
                    Toast.makeText(WelcomeActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(WelcomeActivity.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
