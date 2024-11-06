package com.example.techtalk;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class ParentActivity extends AppCompatActivity {
    private ListView reportListView;
    private BubbleChatAdapter reportAdapter;
    private ArrayList<ChatMessage> reports;
    private FirebaseFirestore db;
    private SwipeRefreshLayout swipeRefreshLayout;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);

        reportListView = findViewById(R.id.reportsContainer);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(this::onBackButtonClick);

        TextView headerTextView = findViewById(R.id.headerImageView);
        headerTextView.setText("Reports");

        reports = new ArrayList<>();
        reportAdapter = new BubbleChatAdapter(this, reports);
        reportListView.setAdapter(reportAdapter);

        swipeRefreshLayout.setOnRefreshListener(this::setupRealTimeListener);

        db = FirebaseFirestore.getInstance();
        setupRealTimeListener();
    }

    private void setupRealTimeListener() {
        String parentId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("reports")
                .whereEqualTo("parentId", parentId)
                .orderBy("timestamp")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        swipeRefreshLayout.setRefreshing(false);
                        return;
                    }

                    reports.clear();
                    if (snapshots != null) {
                        for (QueryDocumentSnapshot document : snapshots) {
                            String message = document.getString("message");
                            long timestamp = document.getLong("timestamp");
                            reports.add(new ChatMessage(message, timestamp));
                        }
                    }
                    reportAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                });
    }

    public void onBackButtonClick(View view) {
        onBackPressed();
    }
}
