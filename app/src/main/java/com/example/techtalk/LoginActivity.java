package com.example.techtalk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "LoginActivity";

    private EditText passwordEditText;
    private boolean isPasswordVisible = false; // Track password visibility state
    public static final String SHARED_PREFS = "sharedPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG, "Activity created");

        mAuth = FirebaseAuth.getInstance();

        EditText usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password); // Reference to password EditText
        Button loginButton = findViewById(R.id.loginButton);
        TextView signUpTextView = findViewById(R.id.signUpTextView);
        TextView forgotPassTextView = findViewById(R.id.forgotPassTextView);
        ImageButton togglePasswordVisibilityButton = findViewById(R.id.togglePasswordVisibility); // Reference to the toggle button

        // Toggle password visibility on ImageButton click
        togglePasswordVisibilityButton.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                passwordEditText.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePasswordVisibilityButton.setImageResource(R.drawable.open); // Update icon to indicate visibility
            } else {
                passwordEditText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePasswordVisibilityButton.setImageResource(R.drawable.close); // Update icon to indicate hidden
            }
            passwordEditText.setSelection(passwordEditText.getText().length()); // Move cursor to the end
        });

        loginButton.setOnClickListener(v -> {
            String email = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(LoginActivity.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(email, password);
            }
        });

        forgotPassTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        signUpTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        checkBox();

    }

    private void checkBox() {
        SharedPreferences sharedPreferences = getSharedPreferences (SHARED_PREFS, MODE_PRIVATE);
        String check = sharedPreferences.getString("name","");
        if(check.equals("true")){
            Toast.makeText(LoginActivity.this,"Login Succesfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            String userId = user.getUid();

                            // Retrieve user role from Firestore
                            db.collection("users").document(userId).get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            DocumentSnapshot document = task1.getResult();
                                            if (document != null && document.exists()) {
                                                String role = document.getString("role"); // Assuming you store the role in Firestore
                                                String username = document.getString("username");

                                                // Navigate based on role
                                                Intent intent;
                                                if ("admin".equals(role)) {
                                                    intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                                                } else {
                                                    SharedPreferences sharedPreferences = getSharedPreferences (SHARED_PREFS, MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.putString("name","true");
                                                    editor.apply();

                                                    intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                                                }
                                                intent.putExtra("username", username); // Pass the retrieved username
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Log.e(TAG, "User document does not exist");
                                                Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Log.e(TAG, "Failed to retrieve user role: ", task1.getException());
                                            Toast.makeText(LoginActivity.this, "Error retrieving user role", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Log.e(TAG, "Authentication failed: " + task.getException().getMessage());
                        Toast.makeText(LoginActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



}
