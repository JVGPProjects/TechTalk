package com.example.techtalk;

import android.app.Dialog;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class FamilyActivity extends AppCompatActivity {

    private ImageButton motherButton, fatherButton, brotherButton, sisterButton, grandparentsButton, friendsButton;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family);

        // Initialize buttons
        motherButton = findViewById(R.id.motherButton);
        fatherButton = findViewById(R.id.fatherButton);
        brotherButton = findViewById(R.id.brotherButton);
        sisterButton = findViewById(R.id.sisterButton);
        grandparentsButton = findViewById(R.id.grandparentsButton);
        friendsButton = findViewById(R.id.friendsButton);

        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US);
            }
        });

        // Set click listeners for each button
        motherButton.setOnClickListener(v -> showFamilyDialog(R.drawable.mother, "Mother", new String[]{
                "You're an amazing mom!",
                "I love you, mom!",
                "You're the best!"
        }));

        fatherButton.setOnClickListener(v -> showFamilyDialog(R.drawable.father, "Father", new String[]{
                "You're a great dad!",
                "I love you, dad!",
                "Thank you, dad!"
        }));

        brotherButton.setOnClickListener(v -> showFamilyDialog(R.drawable.brother, "Brother", new String[]{
                "You're a cool brother!",
                "Can we play?",
                "You're awesome, big bro!"
        }));

        sisterButton.setOnClickListener(v -> showFamilyDialog(R.drawable.sister, "Sister", new String[]{
                "You're a wonderful sister!",
                "You're awesome!",
                "I love you, sis!"
        }));

        grandparentsButton.setOnClickListener(v -> showFamilyDialog(R.drawable.grandparents, "Grandparents", new String[]{
                "I love you, grandma!",
                "I love you both!",
                "I love you, grandpa!"
        }));

        friendsButton.setOnClickListener(v -> showFamilyDialog(R.drawable.friends, "Friends", new String[]{
                "Let's play again!",
                "It's been a while!",
                "Let's play a lot today!"
        }));
    }

    // Back button click handler
    public void onBackButtonClick(View view) {
        // Close the current activity and return to the previous one
        onBackPressed();
    }

    private void showFamilyDialog(int imageResId, String familyMemberName, String[] praises) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_mood);

        ImageView familyImage = dialog.findViewById(R.id.emotionImage);
        ListView praisesListView = dialog.findViewById(R.id.praisesListView);

        familyImage.setImageResource(imageResId);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, praises) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                // Change the text color directly using the view
                ((TextView) view).setTextColor(getResources().getColor(R.color.black)); // Change to your desired color
                return view;
            }
        };
        praisesListView.setAdapter(adapter);

        praisesListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedPraise = praises[position];
            // Speak the selected praise
            textToSpeech.speak(selectedPraise, TextToSpeech.QUEUE_FLUSH, null, null);
        });

        dialog.show();
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
