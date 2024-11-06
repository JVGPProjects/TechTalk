package com.example.techtalk;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerificationActivity extends AppCompatActivity {

    private boolean isVerified = false; // This will track whether the verification is done
    private TextView countdownText;
    private Button resendButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private final int RESEND_TIME = 60000; // 1 minute in milliseconds
    private final int COUNTDOWN_INTERVAL = 1000; // 1 second in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        Button verifyButton = findViewById(R.id.verifyButton);
        countdownText = findViewById(R.id.countdownText);
        resendButton = findViewById(R.id.resendButton);

        countdownText.setPaintFlags(countdownText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // Start the countdown timer
        startCountdownTimer();

        // Check if the user's email is already verified
        checkIfEmailVerified();

        // Handle the click of the "Verify" button
        verifyButton.setOnClickListener(v -> {
            refreshUserVerificationStatus(); // Check the latest verification status
            if (isVerified) {
                showVerifiedDialog();
            } else {
                // Inform the user that they need to verify
                Toast.makeText(VerificationActivity.this, "Please click the verification link in your email.", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle the click of the "Resend Email" button
        resendButton.setOnClickListener(v -> {
            resendVerificationEmail(); // Implement this method to send the verification email again
            startCountdownTimer(); // Restart the countdown after resending
        });

        // Set up swipe-to-refresh functionality
        swipeRefreshLayout.setOnRefreshListener(() -> {
            refreshUserVerificationStatus();
            swipeRefreshLayout.setRefreshing(false); // Stop the refreshing animation
        });
    }

    // Check if the user's email is verified
    private void checkIfEmailVerified() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Check if the user email is verified
            isVerified = user.isEmailVerified();
            if (isVerified) {
                showVerifiedDialog();
            }
        }
    }

    // Show dialog when the email is verified
    private void showVerifiedDialog() {
        // Inflate the custom dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_email_verified, null);

        // Create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        // Find the button in the dialog layout
        Button continueButton = dialogView.findViewById(R.id.continueButton);

        // Create the dialog
        AlertDialog dialog = builder.create();

        // Set the button click listener
        continueButton.setOnClickListener(v -> {
            // Navigate to WelcomeSignUpActivity
            Intent nextIntent = new Intent(VerificationActivity.this, WelcomeSignUpActivity.class);
            startActivity(nextIntent);
            finish(); // Close the current activity
            dialog.dismiss(); // Dismiss the dialog
        });

        dialog.setCancelable(false); // Make it not cancelable outside the dialog
        dialog.show(); // Show the dialog
    }

    // Refresh the user's verification status
    private void refreshUserVerificationStatus() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.reload().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    isVerified = user.isEmailVerified();
                    if (isVerified) {
                        showVerifiedDialog();
                    }
                } else {
                    Toast.makeText(VerificationActivity.this, "Failed to refresh user data.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Method to resend verification email
    private void resendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Verification email resent!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to resend verification email.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Method to start the countdown timer
    private void startCountdownTimer() {
        resendButton.setEnabled(false); // Disable the resend button
        new CountDownTimer(RESEND_TIME, COUNTDOWN_INTERVAL) {
            int secondsRemaining = RESEND_TIME / 1000;

            @Override
            public void onTick(long millisUntilFinished) {
                secondsRemaining--;
                int minutes = secondsRemaining / 60;
                int seconds = secondsRemaining % 60;
                countdownText.setText(String.format("Resend verification email in: %d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                countdownText.setText("You can resend the verification email now.");
                resendButton.setEnabled(true); // Enable the resend button
            }
        }.start();
    }
}
