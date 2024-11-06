package com.example.techtalk;

import android.app.Dialog;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import pl.droidsonroids.gif.GifImageView;

public class ShapesActivity extends AppCompatActivity {

    private GridLayout shapesGrid;
    private TextToSpeech textToSpeech; // TextToSpeech instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shapes);

        shapesGrid = findViewById(R.id.shapesGrid);

        // Initialize TextToSpeech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US); // Set language to US English
            } else {
                Toast.makeText(this, "TextToSpeech initialization failed", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up click listeners for each shape image in the grid
        for (int i = 0; i < shapesGrid.getChildCount(); i++) {
            View child = shapesGrid.getChildAt(i);
            if (child instanceof ImageView) {
                child.setClickable(true);
                child.setOnClickListener(v -> {
                    String tag = (String) v.getTag();
                    if (tag != null) {
                        showShapeGifDialog(tag);
                    } else {
                        Toast.makeText(ShapesActivity.this, "Tag not found!", Toast.LENGTH_SHORT).show();
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

    // Show popup dialog with the GIF when a shape is clicked
    private void showShapeGifDialog(String shapeName) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_shape_gif);

        GifImageView gifImageView = dialog.findViewById(R.id.shapeGifView);
        ImageButton speakImageButton = dialog.findViewById(R.id.speakImageButton); // Reference the speak button

        // Fetch the corresponding GIF resource based on the shape tag
        int gifResId = getResources().getIdentifier(shapeName.toLowerCase() + "_gif", "drawable", getPackageName());

        // Check if the GIF resource exists, and set the image in the GifImageView
        if (gifResId != 0) {
            gifImageView.setImageResource(gifResId);
        } else {
            Toast.makeText(this, "GIF not found for " + shapeName, Toast.LENGTH_SHORT).show();
            gifImageView.setImageResource(R.drawable.default_gif);
        }

        // Set up the speak button to read out the animal name
        speakImageButton.setOnClickListener(v -> {
            if (textToSpeech != null) {
                textToSpeech.speak(shapeName, TextToSpeech.QUEUE_FLUSH, null, null); // Speak the animal's name
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
