package com.plainjimbo.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

/**
 * This class serves as an Adapter that can be used to render Artist information in a ListView.
 * Created by jamesreed on 7/11/15.
 */
public class ArtistListAdapter extends ArrayAdapter<HashMap<String,Object>> {
    public static final int ARTIST_LAYOUT = R.layout.list_item_artist;

    public ArtistListAdapter(Context context) {
        super(context, ARTIST_LAYOUT);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        HashMap<String,Object> artistMap = getItem(position);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(ARTIST_LAYOUT, null);
        }
        setPhoto(view, artistMap);
        setName(view, artistMap);
        return view;
    }

    private void setPhoto(View artistView, HashMap<String,Object> artistMap) {
        ImageView photoImageView = (ImageView)artistView.findViewById(R.id.list_item_artist_photo);
        photoImageView.setImageResource((int)artistMap.get("image"));
    }

    private void setName(View artistView, HashMap<String,Object> artistMap) {
        TextView nameTextView = (TextView)artistView.findViewById(R.id.list_item_artist_name);
        nameTextView.setText((String)artistMap.get("name"));
    }
}
