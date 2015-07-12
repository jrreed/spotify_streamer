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
 * Created by jamesreed on 7/11/15.
 */
public class TrackListAdapter extends ArrayAdapter<HashMap<String,Object>> {
    public static final int TRACK_LAYOUT = R.layout.list_item_track;

    public TrackListAdapter(Context context) {
        super(context, TRACK_LAYOUT);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        HashMap<String, Object> track = getItem(position);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(TRACK_LAYOUT, null);
        }
        setAlbumPhoto(view, track);
        setAlbumTitle(view, track);
        setTrackName(view, track);
        return view;
    }

    private void setAlbumPhoto(View view, HashMap<String, Object> track) {
        ImageView albumPhoto = (ImageView)view.findViewById(R.id.list_item_track_album_photo);
        albumPhoto.setImageResource((int)track.get("albumImage"));
    }

    private void setAlbumTitle(View view, HashMap<String, Object> track) {
        TextView albumTitle = (TextView)view.findViewById(R.id.list_item_track_album_title);
        albumTitle.setText((String)track.get("albumTitle"));
    }

    private void setTrackName(View view, HashMap<String, Object> track) {
        TextView trackTitle = (TextView)view.findViewById(R.id.list_item_track_title);
        trackTitle.setText((String)track.get("title"));
    }
}
