package com.example.techtalk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BubbleChatAdapter extends ArrayAdapter<ChatMessage> {
    private final Context context;
    private final ArrayList<ChatMessage> messages;

    public BubbleChatAdapter(Context context, ArrayList<ChatMessage> messages) {
        super(context, R.layout.bubble_chat_item, messages);
        this.context = context;
        this.messages = messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate the bubble chat item layout
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.bubble_chat_item, parent, false);

        // Get the TextView for the message and timestamp
        TextView messageTextView = rowView.findViewById(R.id.messageTextView);
        TextView timestampTextView = rowView.findViewById(R.id.timestampTextView); // Ensure you have this TextView in your layout

        // Get the message for the current position
        ChatMessage chatMessage = messages.get(position);
        messageTextView.setText(chatMessage.getMessage());

        // Set the formatted timestamp
        String formattedTime = formatTimestamp(chatMessage.getTimestamp());
        timestampTextView.setText(formattedTime);

        return rowView;
    }

    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());
        Date date = new Date(timestamp);
        return sdf.format(date);
    }
}
