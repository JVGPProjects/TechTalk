package com.example.techtalk;

import android.view.View;
import android.widget.TextView;
import androidx.test.core.app.ApplicationProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
public class BubbleChatAdapterTest {

    private BubbleChatAdapter adapter;
    private ArrayList<ChatMessage> messages;

    @Before
    public void setUp() {
        // Initialize the message list with sample data
        messages = new ArrayList<>();
        messages.add(new ChatMessage("Hello, how are you?", 1633017600000L)); // Sample message
        messages.add(new ChatMessage("I'm good, thanks!", 1633017660000L));   // Another sample message

        // Initialize the adapter with the test context and sample messages
        adapter = new BubbleChatAdapter(ApplicationProvider.getApplicationContext(), messages);
    }

    @Test
    public void testGetView() {
        // Get the view for the first message
        View view = adapter.getView(0, null, null);

        // Check that the view is not null
        assertNotNull(view);

        // Check that the message and timestamp are set correctly
        TextView messageTextView = view.findViewById(R.id.messageTextView);
        TextView timestampTextView = view.findViewById(R.id.timestampTextView);

        assertNotNull(messageTextView);
        assertNotNull(timestampTextView);

        // Verify the message content
        assertEquals("Hello, how are you?", messageTextView.getText().toString());

        // Verify the formatted timestamp
        assertEquals("01/10/2021 00:00", timestampTextView.getText().toString());
    }
}
