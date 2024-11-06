package com.example.techtalk;

import android.app.Dialog;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class MoodActivity extends AppCompatActivity {

    private ImageButton happyButton;
    private ImageButton sadButton;
    private ImageButton angryButton;
    private ImageButton excitedButton;
    private ImageButton confusedButton;
    private ImageButton surprisedButton;
    private ImageButton scaredButton;
    private ImageButton sickButton;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood);

        // Initialize ImageButtons
        happyButton = findViewById(R.id.happyButton);
        sadButton = findViewById(R.id.sadButton);
        angryButton = findViewById(R.id.angryButton);
        excitedButton = findViewById(R.id.excitedButton);
        confusedButton = findViewById(R.id.confusedButton);
        surprisedButton = findViewById(R.id.surprisedButton);
        sickButton = findViewById(R.id.sickButton);
        scaredButton = findViewById(R.id.scaredButton);

        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US);
            }
        });

        // Set click listeners for each button
        happyButton.setOnClickListener(v -> showEmotionDialog(R.drawable.ic_happy, new String[]{
                "I'm so happy!",
                "I had so much fun!",
                "I love this!"
        }));

        sadButton.setOnClickListener(v -> showEmotionDialog(R.drawable.ic_sad, new String[]{
                "I feel a little sad.",
                "I'm sad.",
                "Can I have a hug?"
        }));

        angryButton.setOnClickListener(v -> showEmotionDialog(R.drawable.ic_angry, new String[]{
                "I’m really mad right now!",
                "I'm upset'.",
                "I don't like this!"
        }));

        excitedButton.setOnClickListener(v -> showEmotionDialog(R.drawable.ic_excited, new String[]{
                "Yay! I’m so excited!",
                "This is going to be fun!",
                "I can’t wait to play!"
        }));

        confusedButton.setOnClickListener(v -> showEmotionDialog(R.drawable.ic_confused, new String[]{
                "I'm a little confused.",
                "Can you help me understand?",
                "What does that mean?"
        }));

        surprisedButton.setOnClickListener(v -> showEmotionDialog(R.drawable.ic_surprised, new String[]{
                "Wow! That's surprising!",
                "I didn't see that coming!",
                "That surprises me!"
        }));

        sickButton.setOnClickListener(v -> showEmotionDialog(R.drawable.ic_sick, new String[]{
                "I don’t feel good.",
                "I feel a little sick today.",
                "I'm not feeling well.'"
        }));

        scaredButton.setOnClickListener(v -> showEmotionDialog(R.drawable.ic_scared, new String[]{
                "I’m so scared!",
                "Can you stay with me?",
                "That's so scary!"
        }));

    }

    private void showEmotionDialog(int imageResId, String[] praises) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_emotion);

        ImageView emotionImage = dialog.findViewById(R.id.emotionImage);
        ListView praisesListView = dialog.findViewById(R.id.praisesListView);

        emotionImage.setImageResource(imageResId);

        // Custom ArrayAdapter to change text color
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, praises) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                // Change the text color directly
                ((TextView) view).setTextColor(getResources().getColor(R.color.black)); // Replace with your color
                return view;
            }
        };

        praisesListView.setAdapter(adapter);

        praisesListView.setOnItemClickListener((parent, view, position, id) -> {
            String praise = praises[position];
            textToSpeech.speak(praise, TextToSpeech.QUEUE_FLUSH, null, null);
        });

        dialog.show();
    }


    // Back button click handler
    public void onBackButtonClick(View view) {
        // Close the current activity and return to the previous one
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
