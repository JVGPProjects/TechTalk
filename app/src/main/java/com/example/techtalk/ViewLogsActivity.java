package com.example.techtalk;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewLogsActivity extends AppCompatActivity {

    private ListView listViewLogs;
    private ArrayList<String> logs;
    private DatabaseReference logsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_logs);
        FirebaseApp.initializeApp(this);


        listViewLogs = findViewById(R.id.listViewLogs);
        logs = new ArrayList<>();
        logsRef = FirebaseDatabase.getInstance().getReference("logs");

        // Retrieve logs from Firebase
        logsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                logs.clear();
                for (DataSnapshot logSnapshot : dataSnapshot.getChildren()) {
                    String logMessage = logSnapshot.getValue(String.class);
                    if (logMessage != null) {
                        logs.add(logMessage);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ViewLogsActivity.this, android.R.layout.simple_list_item_1, logs);
                listViewLogs.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ViewLogsActivity.this, "Failed to load logs: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
