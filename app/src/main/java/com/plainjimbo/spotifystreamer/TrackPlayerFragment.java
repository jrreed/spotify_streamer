package com.plainjimbo.spotifystreamer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by jamesreed on 9/10/15.
 */
public class TrackPlayerFragment extends Fragment {
    public TrackPlayerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_track_player, container, false);

        Intent intent = getActivity().getIntent();
        if (intent.hasExtra(TrackPlayerActivity.EXTRA_ARTIST) && intent.hasExtra(TrackPlayerActivity.EXTRA_TRACK)) {
            ArtistListItem artist = intent.getParcelableExtra(TrackPlayerActivity.EXTRA_ARTIST);
            TrackListItem track = intent.getParcelableExtra(TrackPlayerActivity.EXTRA_TRACK);
            initTrackView(rootView, artist, track);
        }
        return rootView;
    }

    private void initTrackView(View rootView, ArtistListItem artist, TrackListItem track) {
        renderArtistName(rootView, artist.getName());
        renderAlbumName(rootView, track.getAlbumName());
        renderAlbumPhoto(rootView, track.getWallpaperImageUrl());
        renderTrackName(rootView, track.getName());
        renderTrackDuration(rootView, track.getDuration());
    }

    private void renderArtistName(View root, String artistName) {
        TextView artistNameView = (TextView)root.findViewById(R.id.artist_name);
        artistNameView.setText(artistName);
    }

    private void renderAlbumName(View root, String albumName) {
        TextView albumNameView = (TextView)root.findViewById(R.id.album_name);
        albumNameView.setText(albumName);
    }

    private void renderAlbumPhoto(View root, String imageUrl) {
        ImageView albumPhotoView = (ImageView)root.findViewById(R.id.album_photo);
        if (imageUrl != null) {
            albumPhotoView.setBackgroundColor(Color.TRANSPARENT);
            Picasso.with(getActivity())
                    .load(imageUrl)
                    .resize(TrackListItem.PREFERRED_WALLPAPER_IMAGE_DIM, TrackListItem.PREFERRED_WALLPAPER_IMAGE_DIM)
                    .centerInside()
                    .into(albumPhotoView);
        } else {
            albumPhotoView.setImageDrawable(null);
            albumPhotoView.setBackgroundColor(getActivity().getResources().getColor(R.color.medium_gray));
        }
    }

    private void renderTrackName(View root, String trackName) {
        TextView trackNameView = (TextView)root.findViewById(R.id.track_name);
        trackNameView.setText(trackName);
    }

    private void renderTrackDuration(View root, long duration) {
        TextView trackDurationView = (TextView)root.findViewById(R.id.track_duration);
        trackDurationView.setText(millisToTimeString(duration));
    }

    private String millisToTimeString(long millis) {
        long seconds = (millis / 10000) % 60;
        long minutes = (millis / (1000*60)) % 60;
        return String.format("%01d:%02d", minutes, seconds);
    }
}
