package com.example.techtalk;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import java.util.Arrays;
import java.util.List;

public class ImageGalleryActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private List<Integer> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery);

        viewPager = findViewById(R.id.viewPager);

        // List of 10 images for the tutorial
        images = Arrays.asList(
                R.drawable.image1,
                R.drawable.image2,
                R.drawable.image3,
                R.drawable.image4,
                R.drawable.image5,
                R.drawable.image6,
                R.drawable.image7,
                R.drawable.image8,
                R.drawable.image9,
                R.drawable.image10,
                R.drawable.image11,
                R.drawable.image12,
                R.drawable.image13
        );

        // Set up the adapter
        ImageAdapter adapter = new ImageAdapter(images, this);
        viewPager.setAdapter(adapter);

        // Detect when user reaches the last image and navigate to next activity
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                // If the user is on the last image (index 9), go to the next activity
                if (position == images.size() - 1) {
                    goToNextActivity();
                }
            }
        });
    }

    // Method to go to a different activity after the last image
    private void goToNextActivity() {
        Intent intent = new Intent(ImageGalleryActivity.this, WelcomeActivity.class);
        startActivity(intent);
        finish();  // Close the current gallery activity
    }
}

