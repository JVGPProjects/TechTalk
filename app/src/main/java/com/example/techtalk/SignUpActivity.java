package com.example.techtalk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private boolean isPasswordVisible = false; // Track password visibility state

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        EditText usernameEditText = findViewById(R.id.signUpUsername);
        EditText emailEditText = findViewById(R.id.signUpEmail);
        passwordEditText = findViewById(R.id.signUpPassword); // Initialize here
        confirmPasswordEditText = findViewById(R.id.signUpConfirmPassword); // Initialize here
        Button signUpButton = findViewById(R.id.signUpButton);
        TextView loginTextView = findViewById(R.id.loginTextView);
        RadioGroup roleRadioGroup = findViewById(R.id.roleRadioGroup);

        LinearLayout therapistQuestionLayout = findViewById(R.id.therapistQuestionLayout);
        EditText therapistAnswerEditText = findViewById(R.id.therapistAnswer);

        roleRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.therapistRadioButton) {
                therapistQuestionLayout.setVisibility(View.VISIBLE); // Show question
            } else {
                therapistQuestionLayout.setVisibility(View.GONE); // Hide question
                therapistAnswerEditText.setText(""); // Clear answer if user switches roles
            }
        });



        // ImageButton references for both password and confirm password
        ImageButton togglePasswordVisibilityButton = findViewById(R.id.toggleSignUpPasswordVisibility);
        ImageButton toggleConfirmPasswordVisibilityButton = findViewById(R.id.toggleSignUpConfirmPasswordVisibility);

        // Toggle password visibility for the password field
        togglePasswordVisibilityButton.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                passwordEditText.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePasswordVisibilityButton.setImageResource(R.drawable.open); // Update icon
            } else {
                passwordEditText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePasswordVisibilityButton.setImageResource(R.drawable.close); // Update icon
            }
            passwordEditText.setSelection(passwordEditText.getText().length()); // Move cursor to the end
        });

        // Toggle password visibility for the confirm password field
        toggleConfirmPasswordVisibilityButton.setOnClickListener(v -> {
            boolean isConfirmPasswordVisible = !(confirmPasswordEditText.getInputType() == android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            if (isConfirmPasswordVisible) {
                confirmPasswordEditText.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                toggleConfirmPasswordVisibilityButton.setImageResource(R.drawable.open); // Update icon
            } else {
                confirmPasswordEditText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                toggleConfirmPasswordVisibilityButton.setImageResource(R.drawable.close); // Update icon
            }
            confirmPasswordEditText.setSelection(confirmPasswordEditText.getText().length()); // Move cursor to the end
        });

        signUpButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();

            // Validate input fields
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidPassword(password)) {
                Toast.makeText(SignUpActivity.this, "Password must contain at least 6 characters, including uppercase, lowercase, and a number.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }



            int selectedRoleId = roleRadioGroup.getCheckedRadioButtonId();
            if (selectedRoleId == -1) { // No role selected
                Toast.makeText(SignUpActivity.this, "Please select a role", Toast.LENGTH_SHORT).show();
                return;
            }

            String role = selectedRoleId == R.id.parentRadioButton ? "parent" : "therapist";

            // Validate therapist answer if selected
            if ("therapist".equals(role)) {
                String answer = therapistAnswerEditText.getText().toString().trim();
                if (!"PasswordTherapist".equalsIgnoreCase(answer)) { // Example correct answer
                    Toast.makeText(SignUpActivity.this, "Incorrect! Please provide the correct answer.", Toast.LENGTH_SHORT).show();
                    return; // Prevent signup
                }
            }

            // Create a new user with Firebase Authentication
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                user.sendEmailVerification()
                                        .addOnCompleteListener(verificationTask -> {
                                            if (verificationTask.isSuccessful()) {
                                                saveUserInfo(username, email, role); // Call this method to save user info
                                                Toast.makeText(SignUpActivity.this, "Verification email sent. Please check your inbox.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(SignUpActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(SignUpActivity.this, "This email is already registered or invalid.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        loginTextView.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    // Password validation method
    private boolean isValidPassword(String password) {
        // Check for at least 6 characters, at least one uppercase, one lowercase, and one number
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}$";
        return Pattern.compile(passwordPattern).matcher(password).matches();
    }

    private void saveUserInfo(String username, String email, String role) {
        String userId = mAuth.getCurrentUser().getUid();

        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("email", email);
        user.put("role", role); // Save the user's role

        db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("role", role); // Store the user role
                    editor.apply();

                    Intent intent = new Intent(SignUpActivity.this, VerificationActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("role", role); // Pass the role to the next activity
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(SignUpActivity.this, "Failed to save user data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
