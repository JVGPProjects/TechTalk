package com.example.techtalk;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import androidx.test.core.app.ApplicationProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28}, manifest = Config.NONE)
public class ProfileImageAdapterTest {

    private ProfileImageAdapter adapter;
    private Context context;
    private int[] images = {R.drawable.image1, R.drawable.image2, R.drawable.image3}; // Example images

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        adapter = new ProfileImageAdapter(context, images);
    }

    @Test
    public void testGetCount() {
        assertEquals(3, adapter.getCount());
    }

    @Test
    public void testGetItem() {
        assertEquals(R.drawable.image1, adapter.getItem(0));
        assertEquals(R.drawable.image2, adapter.getItem(1));
        assertEquals(R.drawable.image3, adapter.getItem(2));
    }

    @Test
    public void testGetView() {
        View view = adapter.getView(0, null, null);
        assertNotNull(view);

        ImageView imageView = view.findViewById(R.id.profileImageView);
        assertNotNull(imageView);
        assertEquals(R.drawable.image1, (int) images[0]);
    }
}
