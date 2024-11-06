package com.example.techtalk;

import android.app.Dialog;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import pl.droidsonroids.gif.GifImageView;

public class ColorsActivity extends AppCompatActivity {

    private GridLayout colorsGrid;
    private TextToSpeech textToSpeech; // TextToSpeech instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colors); // Set your corresponding layout

        colorsGrid = findViewById(R.id.colorsGrid); // Assuming you renamed the GridLayout

        // Initialize TextToSpeech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US); // Set language to US English
            } else {
                Toast.makeText(this, "TextToSpeech initialization failed", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up click listeners for each color button in the grid
        for (int i = 0; i < colorsGrid.getChildCount(); i++) {
            View child = colorsGrid.getChildAt(i);
            if (child instanceof ImageButton) {
                child.setClickable(true); // Ensure the ImageButton is clickable
                child.setOnClickListener(v -> {
                    String tag = (String) v.getTag();
                    if (tag != null) {
                        showColorGifDialog(tag); // Pass the tag (color name) to the dialog
                    } else {
                        Toast.makeText(ColorsActivity.this, "Tag not found!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    // Back button click handler
    public void onBackButtonClick(View view) {
        // Close the current activity and return to the previous one
        onBackPressed();
    }

    // Show popup dialog with the GIF when a color button is clicked
    private void showColorGifDialog(String colorName) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_color_gif); // Use the updated dialog layout

        GifImageView gifImageView = dialog.findViewById(R.id.colorGifView);
        ImageButton speakImageButton = dialog.findViewById(R.id.speakImageButton); // Reference the speak button

        // Fetch the corresponding GIF resource based on the color tag
        int gifResId = getResources().getIdentifier(colorName.toLowerCase() + "_gif", "drawable", getPackageName());

        // Check if the GIF resource exists, and set the image in the GifImageView
        if (gifResId != 0) {
            gifImageView.setImageResource(gifResId); // Set the color GIF
        } else {
            Toast.makeText(this, "GIF not found for " + colorName, Toast.LENGTH_SHORT).show();
            gifImageView.setImageResource(R.drawable.default_gif); // Set a default GIF if not found
        }

        // Set up the speak button to read out the animal name
        speakImageButton.setOnClickListener(v -> {
            if (textToSpeech != null) {
                textToSpeech.speak(colorName, TextToSpeech.QUEUE_FLUSH, null, null); // Speak the animal's name
            }
        });

        // Show the dialog
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        // Shutdown TextToSpeech when activity is destroyed
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
