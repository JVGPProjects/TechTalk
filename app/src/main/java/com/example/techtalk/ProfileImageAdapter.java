package com.example.techtalk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ProfileImageAdapter extends BaseAdapter {
    private Context context;
    private int[] images;

    public ProfileImageAdapter(Context context, int[] images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object getItem(int position) {
        return images[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.profile_image_item, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.profileImageView);
        imageView.setImageResource(images[position]);

        return convertView;
    }
}
