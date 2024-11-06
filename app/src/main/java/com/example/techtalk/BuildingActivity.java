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

public class BuildingActivity extends AppCompatActivity {

    private GridLayout buildingGrid;
    private TextToSpeech textToSpeech; // TextToSpeech instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building); // Set the corresponding layout

        buildingGrid = findViewById(R.id.buildingGrid); // Assuming you have a GridLayout for buildings

        // Initialize TextToSpeech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US); // Set language to US English
            } else {
                Toast.makeText(this, "TextToSpeech initialization failed", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up click listeners for each building in the grid
        for (int i = 0; i < buildingGrid.getChildCount(); i++) {
            View child = buildingGrid.getChildAt(i);
            if (child instanceof ImageView) {
                child.setClickable(true); // Ensure the ImageView is clickable
                child.setOnClickListener(v -> {
                    String tag = (String) v.getTag();
                    if (tag != null) {
                        showBuildingGifDialog(tag); // Pass the tag (building name) to the dialog
                    } else {
                        Toast.makeText(BuildingActivity.this, "Tag not found!", Toast.LENGTH_SHORT).show();
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

    // Show popup dialog with the GIF when a building is clicked
    private void showBuildingGifDialog(String buildingName) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_building_gif); // Use the updated dialog layout

        GifImageView gifImageView = dialog.findViewById(R.id.buildingGifView);
        ImageButton speakImageButton = dialog.findViewById(R.id.speakImageButton); // Reference the speak button

        // Fetch the corresponding GIF resource based on the building tag
        int gifResId = getResources().getIdentifier(buildingName.toLowerCase() + "_gif", "drawable", getPackageName());

        // Check if the GIF resource exists, and set the image in the GifImageView
        if (gifResId != 0) {
            gifImageView.setImageResource(gifResId); // Set the building GIF
        } else {
            Toast.makeText(this, "GIF not found for " + buildingName, Toast.LENGTH_SHORT).show();
            gifImageView.setImageResource(R.drawable.default_gif); // Set a default GIF if not found
        }

        // Set up the speak button to read out the animal name
        speakImageButton.setOnClickListener(v -> {
            if (textToSpeech != null) {
                textToSpeech.speak(buildingName, TextToSpeech.QUEUE_FLUSH, null, null); // Speak the animal's name
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
