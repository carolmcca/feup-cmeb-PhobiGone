package com.example.phobigone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import java.util.ArrayList;

public class BadgeListAdapter extends ArrayAdapter<Badge> {
    private static final String TAG = "BadgeListAdapter";
    private Context mContext;
    int mResource;

    public BadgeListAdapter(Context context, int resource, ArrayList<Badge> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String title = getItem(position).getTitle();
        String description = getItem(position).getDescription();
        String icon = getItem(position).getIcon();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);
        TextView titleTv = (TextView) convertView.findViewById(R.id.badge_title);
        TextView descriptionTv = (TextView) convertView.findViewById(R.id.badge_description);
        ImageView iconIv = (ImageView) convertView.findViewById(R.id.badge_logo);

        titleTv.setText(title);
        descriptionTv.setText(description);
        int resID = mContext.getResources().getIdentifier(icon, "drawable", mContext.getPackageName());
        iconIv.setImageResource(resID);
        return convertView;

    }
}
