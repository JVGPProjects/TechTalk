package com.example.techtalk;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private Switch notificationSwitch;
    private ImageButton backButton;
    private boolean isChanged = false;  // Track if any settings are changed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        notificationSwitch = findViewById(R.id.notificationsSwitch);
        ImageButton backButton = findViewById(R.id.backButton);

        // Load the saved preferences
        boolean notificationsEnabled = getSharedPreferences("app_preferences", MODE_PRIVATE)
                .getBoolean("notifications_enabled", true);
        notificationSwitch.setChecked(notificationsEnabled);

        // Set a listener on the switch to save the preference when changed
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isChanged = true;  // Mark as changed
                getSharedPreferences("app_preferences", MODE_PRIVATE)
                        .edit()
                        .putBoolean("notifications_enabled", isChecked)
                        .apply();
                Toast.makeText(SettingsActivity.this, "Notifications " + (isChecked ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
            }
        });

        // Set the OnClickListener for the back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // Handle button clicks for changing passwords and other activities
        Button accountButton = findViewById(R.id.accountButton);
        Button changeAccountPasswordButton = findViewById(R.id.changeAccountPasswordButton);
        Button changeFourDigitPasswordButton = findViewById(R.id.changeFourDigitPasswordButton);
        Button textToSpeechVoiceChangerButton = findViewById(R.id.textToSpeechVoiceChangerButton);
        Button aboutSectionButton = findViewById(R.id.aboutSectionButton);

        accountButton.setOnClickListener(v -> {
            isChanged = true;  // Mark as changed
            startActivity(new Intent(SettingsActivity.this, AccountActivity.class));
        });

        changeAccountPasswordButton.setOnClickListener(v -> {
            isChanged = true;  // Mark as changed
            startActivity(new Intent(SettingsActivity.this, ChangeAccountPasswordActivity.class));
        });

        changeFourDigitPasswordButton.setOnClickListener(v -> {
            isChanged = true;  // Mark as changed
            startActivity(new Intent(SettingsActivity.this, ChangeFourDigitPasswordActivity.class));
        });

        textToSpeechVoiceChangerButton.setOnClickListener(v -> {
            isChanged = true;  // Mark as changed
            startActivity(new Intent(SettingsActivity.this, Tutorial.class));
        });

        aboutSectionButton.setOnClickListener(v -> {
            startActivity(new Intent(SettingsActivity.this, AboutActivity.class));
        });
    }


    // Back button click handler
    public void onBackButtonClick(View view) {
        onBackPressed(); // Calls the overridden onBackPressed() method
    }

}
