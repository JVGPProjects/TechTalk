package com.example.techtalk;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class TextToSpeechVoiceChangerActivity extends AppCompatActivity {

    private Button maleVoiceButton, femaleVoiceButton;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_to_speech_voice_changer);

        maleVoiceButton = findViewById(R.id.maleVoiceButton);
        femaleVoiceButton = findViewById(R.id.femaleVoiceButton);

        // Initialize SharedPreferences to store selected voice
        preferences = getSharedPreferences("voice_prefs", MODE_PRIVATE);
        editor = preferences.edit();

        // Set male voice button
        maleVoiceButton.setOnClickListener(v -> {
            editor.putString("selected_voice", "male");
            editor.apply();
            Toast.makeText(this, "Voice changed to male", Toast.LENGTH_SHORT).show();
        });

        // Set female voice button
        femaleVoiceButton.setOnClickListener(v -> {
            editor.putString("selected_voice", "female");
            editor.apply();
            Toast.makeText(this, "Voice changed to female", Toast.LENGTH_SHORT).show();
        });
    }
}
