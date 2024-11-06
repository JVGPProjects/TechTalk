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

public class ConversationActivity extends AppCompatActivity {

    private ImageButton meButton;
    private ImageButton greetingsButton;
    private ImageButton helpButton;
    private ImageButton toiletButton;
    private ImageButton dayButton;
    private ImageButton nightButton;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        // Initialize ImageButtons
        meButton = findViewById(R.id.meButton);
        greetingsButton = findViewById(R.id.greetingsButton);
        helpButton = findViewById(R.id.helpButton);
        toiletButton = findViewById(R.id.toiletButton);
        dayButton = findViewById(R.id.dayButton);
        nightButton = findViewById(R.id.nightButton);

        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US);
            }
        });

        // Set click listeners for each button

        meButton.setOnClickListener(v -> showConversationDialog(R.drawable.me, new String[]{
                "Hello!", "Nice to meet you.", "How are you?"
        }));

        greetingsButton.setOnClickListener(v -> showConversationDialog(R.drawable.greetings, new String[]{
                "Hello! How are you?", "I’m a little shy today.", "Can we be friends?"
        }));

        helpButton.setOnClickListener(v -> showConversationDialog(R.drawable.help, new String[]{
                "I need help!", "Can you help me?", "Can you show me how to do this?"
        }));

        toiletButton.setOnClickListener(v -> showConversationDialog(R.drawable.toilet, new String[]{
                "May I go to the bathroom?", "I need to use the bathroom.", "May I know where the bathroom is?"
        }));

        dayButton.setOnClickListener(v -> showConversationDialog(R.drawable.day, new String[]{
                "Good morning!", "Have a nice day ahead!", "Can we go outside and play?"
        }));

        nightButton.setOnClickListener(v -> showConversationDialog(R.drawable.night, new String[]{
                "Good night!", "I don’t want to sleep yet!", "Can you stay with me for a little bit?"
        }));

    }

    private void showConversationDialog(int imageResId, String[] phrases) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_conversation);

        ImageView conversationImage = dialog.findViewById(R.id.emotionImage);
        ListView phrasesListView = dialog.findViewById(R.id.praisesListView);

        conversationImage.setImageResource(imageResId);

        // Custom ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, phrases) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Get the default view
                View view = super.getView(position, convertView, parent);
                // Change the text color
                TextView textView = (TextView) view;
                textView.setTextColor(getResources().getColor(R.color.black)); // Change this to your desired color
                return view;
            }
        };

        phrasesListView.setAdapter(adapter);

        phrasesListView.setOnItemClickListener((parent, view, position, id) -> {
            String phrase = phrases[position];
            textToSpeech.speak(phrase, TextToSpeech.QUEUE_FLUSH, null, null);
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
