package com.example.techtalk;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.view.View;
import android.content.Context;

import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Locale;

public class ReportAdapter extends ArrayAdapter<Report> {

    public ReportAdapter(Context context, ArrayList<Report> reports) {
        super(context, 0, reports);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Report report = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.report_item, parent, false);
        }

        // Lookup views for data population
        TextView reportMessageView = convertView.findViewById(R.id.reportTextView);


        // Populate the data into the template view using the Report object
        reportMessageView.setText(report.getMessage());

        // Lookup view for data population
        TextView reportTextView = convertView.findViewById(R.id.reportTextView);

        // Populate the data into the template view using the Report object
        reportTextView.setText(report.getMessage());
        reportTextView.setTextColor(Color.BLACK);
        // Set text color to black
        reportTextView.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));

        // Format and display timestamp


        // Return the completed view to render on screen
        return convertView;
    }

    // Helper method to format the timestamp (implement as needed)
    private String formatTimestamp(long timestamp) {
        // Format the timestamp to a readable date string (example)
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}

