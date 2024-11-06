package com.example.techtalk;

import android.app.Dialog;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;
import pl.droidsonroids.gif.GifImageView;

public class AnimalsActivity extends AppCompatActivity {

    private GridLayout animalsGrid;
    private TextToSpeech textToSpeech; // TextToSpeech instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animals); // Set your corresponding layout

        animalsGrid = findViewById(R.id.animalsGrid); // Assuming you renamed the GridLayout

        // Initialize TextToSpeech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US); // Set language to US English
            } else {
                Toast.makeText(this, "TextToSpeech initialization failed", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up click listeners for each image in the grid
        for (int i = 0; i < animalsGrid.getChildCount(); i++) {
            View child = animalsGrid.getChildAt(i);
            if (child instanceof ImageView) {
                child.setClickable(true); // Ensure the ImageView is clickable
                child.setOnClickListener(v -> {
                    String tag = (String) v.getTag();
                    if (tag != null) {
                        showAnimalGifDialog(tag); // Pass the tag (animal name) to the dialog
                    } else {
                        Toast.makeText(AnimalsActivity.this, "Tag not found!", Toast.LENGTH_SHORT).show();
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

    // Show popup dialog with the GIF when an animal is clicked
    private void showAnimalGifDialog(String animalName) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_animal_gif); // Use the updated dialog layout

        GifImageView gifImageView = dialog.findViewById(R.id.animalGifView);
        ImageButton speakImageButton = dialog.findViewById(R.id.speakImageButton); // Reference the speak button

        // Fetch the corresponding GIF resource based on the animal tag
        int gifResId = getResources().getIdentifier(animalName.toLowerCase() + "_gif", "drawable", getPackageName());

        // Check if the GIF resource exists, and set the image in the GifImageView
        if (gifResId != 0) {
            gifImageView.setImageResource(gifResId); // Set the animal GIF
        } else {
            Toast.makeText(this, "GIF not found for " + animalName, Toast.LENGTH_SHORT).show();
            gifImageView.setImageResource(R.drawable.default_gif); // Set a default GIF if not found
        }

        // Set up the speak button to read out the animal name
        speakImageButton.setOnClickListener(v -> {
            if (textToSpeech != null) {
                textToSpeech.speak(animalName, TextToSpeech.QUEUE_FLUSH, null, null); // Speak the animal's name
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