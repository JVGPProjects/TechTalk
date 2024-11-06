package com.example.techtalk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ApplicationProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28}, manifest = Config.NONE) // Updated annotation
public class ImageAdapterTest {

    private ImageAdapter adapter;
    private Context context;
    private List<Integer> images;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        images = Arrays.asList(R.drawable.image1, R.drawable.image2, R.drawable.image3);
        adapter = new ImageAdapter(images, context);
    }

    @Test
    public void testGetItemCount() {
        assertEquals(3, adapter.getItemCount());
    }

    @Test
    public void testOnCreateViewHolder() {
        RecyclerView recyclerView = new RecyclerView(context);
        RecyclerView.ViewHolder viewHolder = adapter.onCreateViewHolder(recyclerView, 0);
        assertNotNull(viewHolder);
    }

    @Test
    public void testOnBindViewHolder() {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, null);
        ImageAdapter.ImageViewHolder viewHolder = new ImageAdapter.ImageViewHolder(view);
        adapter.onBindViewHolder(viewHolder, 0);
        ImageView imageView = viewHolder.imageView;
        assertNotNull(imageView);
        assertEquals(R.drawable.image1, (int) images.get(0));
    }
}
