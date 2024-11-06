package com.example.techtalk;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ChangeAccountPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_account_password);

        mAuth = FirebaseAuth.getInstance();

        EditText currentPasswordEditText = findViewById(R.id.currentPasswordEditText);
        EditText newPasswordEditText = findViewById(R.id.newPasswordEditText);
        EditText confirmNewPasswordEditText = findViewById(R.id.confirmNewPasswordEditText);
        Button changePasswordButton = findViewById(R.id.changePasswordButton);

        changePasswordButton.setOnClickListener(v -> {
            String currentPassword = currentPasswordEditText.getText().toString();
            String newPassword = newPasswordEditText.getText().toString();
            String confirmNewPassword = confirmNewPasswordEditText.getText().toString();

            if (TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmNewPassword)) {
                Toast.makeText(ChangeAccountPasswordActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            } else if (!newPassword.equals(confirmNewPassword)) {
                Toast.makeText(ChangeAccountPasswordActivity.this, "New passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                changePassword(currentPassword, newPassword);
            }
        });
    }

    // Back button click handler
    public void onBackButtonClick(View view) {
        // Close the current activity and return to the previous one
        onBackPressed();
    }

    private void changePassword(String currentPassword, String newPassword) {
        // Re-authenticate the user with the current password
        String email = mAuth.getCurrentUser().getEmail();
        mAuth.signInWithEmailAndPassword(email, currentPassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // If re-authentication is successful, update the password
                mAuth.getCurrentUser().updatePassword(newPassword).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Toast.makeText(ChangeAccountPasswordActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity
                    } else {
                        Toast.makeText(ChangeAccountPasswordActivity.this, "Failed to change password: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(ChangeAccountPasswordActivity.this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
