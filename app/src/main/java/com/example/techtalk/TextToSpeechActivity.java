package com.example.techtalk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast; // Import Toast
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class TextToSpeechActivity extends AppCompatActivity {

    private EditText inputPraise;
    private TextToSpeech textToSpeech;

    private Button deleteButton;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_to_speech);

        inputPraise = findViewById(R.id.inputPraise);
        Button speakButton = findViewById(R.id.speakButton);
        deleteButton = findViewById(R.id.deleteButton);

        // Access shared preferences for selected voice
        preferences = getSharedPreferences("voice_prefs", MODE_PRIVATE);
        String selectedVoice = preferences.getString("selected_voice", "female"); // Default is female

        // Initialize TextToSpeech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.US);
                textToSpeech.setPitch(1.0f); // Normal pitch
                textToSpeech.setSpeechRate(1.0f); // Normal speech rate
            }
        });

        // Speak button action
        speakButton.setOnClickListener(v -> {
            String text = inputPraise.getText().toString();
            if (text.isEmpty()) {
                Toast.makeText(TextToSpeechActivity.this, "Input something!", Toast.LENGTH_SHORT).show();
            } else {
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        // Delete button action
        deleteButton.setOnClickListener(v -> {
            String text = inputPraise.getText().toString();
            if (text.isEmpty()) {
                Toast.makeText(TextToSpeechActivity.this, "Nothing to delete!", Toast.LENGTH_SHORT).show(); // Show Toast if empty
            } else {
                inputPraise.setText(""); // Clear input field
                Toast.makeText(TextToSpeechActivity.this, "Input deleted!", Toast.LENGTH_SHORT).show(); // Show Toast message
            }
        });

    }

    // Back button click handler
    public void onBackButtonClick(View view) {
        onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
