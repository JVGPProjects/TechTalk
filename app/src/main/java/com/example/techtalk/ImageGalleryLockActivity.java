package com.example.techtalk;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import java.util.Arrays;
import java.util.List;

public class ImageGalleryLockActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private List<Integer> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery);

        viewPager = findViewById(R.id.viewPager);

        // List of 10 images for the tutorial
        images = Arrays.asList(
                R.drawable.parentimage1,
                R.drawable.parentimage2,
                R.drawable.parentimage3,
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
        Intent intent = new Intent(ImageGalleryLockActivity.this, ParentHomeActivity.class);
        startActivity(intent);
        finish();  // Close the current gallery activity
    }
}

