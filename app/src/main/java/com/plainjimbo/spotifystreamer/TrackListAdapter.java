package com.plainjimbo.spotifystreamer;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by jamesreed on 7/11/15.
 */
public class TrackListAdapter extends ArrayAdapter<TrackListItem> {
    public static final int TRACK_LAYOUT = R.layout.list_item_track;
    public static final int PREFERRED_IMAGE_DIM = 200;

    public TrackListAdapter(Context context) {
        super(context, TRACK_LAYOUT);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        TrackListItem track = getItem(position);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(TRACK_LAYOUT, null);
        }
        setAlbumPhoto(view, track);
        setAlbumTitle(view, track);
        setTrackName(view, track);
        return view;
    }

    private void setAlbumPhoto(View view, TrackListItem track) {
        ImageView photoImageView = (ImageView) view.findViewById(R.id.list_item_track_album_photo);
        if (track.getImageUrl() != null) {
            photoImageView.setBackgroundColor(Color.TRANSPARENT);
            Picasso.with(getContext())
                    .load(track.getImageUrl())
                    .resize(PREFERRED_IMAGE_DIM, PREFERRED_IMAGE_DIM)
                    .centerInside()
                    .into(photoImageView);
        } else {
            photoImageView.setImageDrawable(null);
            photoImageView.setBackgroundColor(getContext().getResources().getColor(R.color.medium_gray));
        }
    }

    private void setAlbumTitle(View view, TrackListItem track) {
        TextView albumTextView = (TextView) view.findViewById(R.id.list_item_track_album_title);
        String albumTitle = "";
        if (track.getAlbumName() != null) {
            albumTitle = track.getAlbumName();
        }
        albumTextView.setText(albumTitle);
    }

    private void setTrackName(View view, TrackListItem track) {
        TextView trackTextView = (TextView) view.findViewById(R.id.list_item_track_title);
        trackTextView.setText(track.name);
    }
}
