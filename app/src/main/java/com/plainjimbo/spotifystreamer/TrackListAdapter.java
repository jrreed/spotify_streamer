package com.plainjimbo.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by jamesreed on 7/11/15.
 */
public class TrackListAdapter extends ArrayAdapter<Track> {
    public static final int TRACK_LAYOUT = R.layout.list_item_track;
    public static final int PREFERRED_IMAGE_DIM = 200;

    public TrackListAdapter(Context context) {
        super(context, TRACK_LAYOUT);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        Track track = getItem(position);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(TRACK_LAYOUT, null);
        }
        setAlbumPhoto(view, track.album);
        setAlbumTitle(view, track.album);
        setTrackName(view, track);
        return view;
    }

    private void setAlbumPhoto(View view, AlbumSimple album) {
        ImageView photoImageView = (ImageView) view.findViewById(R.id.list_item_track_album_photo);
        OptimalImageFinder imageFinder = new OptimalImageFinder(album.images);
        Image image = imageFinder.closestTo(PREFERRED_IMAGE_DIM);
        if (image != null) {
            Picasso.with(getContext())
                    .load(image.url)
                    .resize(PREFERRED_IMAGE_DIM, PREFERRED_IMAGE_DIM)
                    .centerCrop()
                    .into(photoImageView);
        } else {
            photoImageView.setImageDrawable(null);
        }
    }

    private void setAlbumTitle(View view, AlbumSimple album) {
        TextView albumTextView = (TextView) view.findViewById(R.id.list_item_track_album_title);
        String albumTitle = "";
        if (album != null) {
            albumTitle = album.name;
        }
        albumTextView.setText(albumTitle);
    }

    private void setTrackName(View view, Track track) {
        TextView trackTextView = (TextView) view.findViewById(R.id.list_item_track_title);
        trackTextView.setText(track.name);
    }
}
