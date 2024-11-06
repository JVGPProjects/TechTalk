package com.example.techtalk;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignLanguageActivity extends AppCompatActivity {

    private GridLayout alphabetGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_language);

        alphabetGrid = findViewById(R.id.alphabetGrid);

        // Set up click listeners for each image in the grid
        for (int i = 0; i < alphabetGrid.getChildCount(); i++) {
            View child = alphabetGrid.getChildAt(i);
            if (child instanceof ImageView) {
                child.setClickable(true); // Ensure the ImageView is clickable
                child.setOnClickListener(v -> {
                    String tag = (String) v.getTag();
                    if (tag != null) {
                        showLetterDialog(tag); // Pass the tag (letter) to the dialog
                    } else {
                        Toast.makeText(SignLanguageActivity.this, "Tag not found!", Toast.LENGTH_SHORT).show();
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

    // Show popup dialog when a letter is clicked
    private void showLetterDialog(String letter) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_letter); // Use the updated dialog layout

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView letterImageView = dialog.findViewById(R.id.letterImageView);

        // Fetch the corresponding image resource based on the letter tag
        int imageResId = getResources().getIdentifier(letter.toLowerCase() + "_popup", "drawable", getPackageName());

        // Check if the image resource exists, and set the image in the ImageView
        if (imageResId != 0) {
            letterImageView.setImageResource(imageResId); // Set the letter image
        } else {
            Toast.makeText(this, "Image not found for " + letter, Toast.LENGTH_SHORT).show();
            letterImageView.setImageResource(R.drawable.default_image); // Set a default image if not found
        }

        // Show the dialog
        dialog.show();
    }
}
