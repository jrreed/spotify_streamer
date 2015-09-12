package com.plainjimbo.spotifystreamer;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
 * Created by jamesreed on 9/10/15.
 */
public class TrackPlayerFragment extends Fragment implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    private static final String BUNDLE_POSITION = "position";
    private ArtistListItem mArtist = null;
    private Toast mCurrentToast = null;
    private MediaPlayer mPlayer = null;
    private boolean mPlayerPrepared = false;
    private int mPosition = 0;
    private TrackListItem mTrack = null;

    private ImageButton mPauseButton = null;
    private ImageButton mPlayButton = null;
    private ImageButton mPreviousButton = null;
    private ImageButton mNextButton = null;

    public TrackPlayerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        if (intent.hasExtra(TrackPlayerActivity.EXTRA_ARTIST) && intent.hasExtra(TrackPlayerActivity.EXTRA_TRACK)) {
            mArtist = intent.getParcelableExtra(TrackPlayerActivity.EXTRA_ARTIST);
            mTrack = intent.getParcelableExtra(TrackPlayerActivity.EXTRA_TRACK);
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_POSITION)) {
            mPosition = savedInstanceState.getInt(BUNDLE_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_track_player, container, false);
        renderArtistName(rootView, mArtist.getName());
        renderAlbumName(rootView, mTrack.getAlbumName());
        renderAlbumPhoto(rootView, mTrack.getWallpaperImageUrl());
        renderTrackName(rootView, mTrack.getName());
        renderTrackDuration(rootView, mTrack.getDuration());
        initPlayButton(rootView);
        initPauseButton(rootView);
        initPreviousButton(rootView);
        initNextButton(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initPlayer();
    }

    private void initPlayer() {
        mPlayerPrepared = false;
        mPlayer = new MediaPlayer();
        mPlayer.setOnErrorListener(this);
        mPlayer.setOnPreparedListener(this);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mPlayer.setDataSource(mTrack.getPreviewUrl());
        } catch (IOException exc) {
            makeToast(getString(R.string.spotify_api_error_message));
        }
        mPlayer.prepareAsync();
        toggleButtonsEnabled(false);
        pause();
    }

    @Override
    public void onPrepared(MediaPlayer player) {
        mPlayerPrepared = true;
        toggleButtonsEnabled(true);
        if (mPosition > 0) {
            mPlayer.seekTo(mPosition);
        }
    }

    @Override
    public boolean onError(MediaPlayer player, int what, int extra) {
        makeToast(getString(R.string.spotify_api_error_message));
        destroyPlayer();
        return true;
    }

    private void destroyPlayer() {
        mPlayerPrepared = false;
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        pause();
        toggleButtonsEnabled(false);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (playerPrepared()) {
            mPosition = mPlayer.getCurrentPosition();
        }
        savedInstanceState.putInt(BUNDLE_POSITION, mPosition);
    }

    @Override
    public void onPause() {
        super.onPause();
        destroyPlayer();
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

    private void initPlayButton(View root) {
        mPlayButton = (ImageButton)root.findViewById(R.id.player_play);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play();
            }
        });
    }

    private void play() {
        mPlayButton.setVisibility(View.GONE);
        mPauseButton.setVisibility(View.VISIBLE);
        if (playerPrepared()) {
            mPlayer.start();
        }
    }

    private void initPauseButton(View root) {
        mPauseButton = (ImageButton)root.findViewById(R.id.player_pause);
        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pause();
            }
        });
    }

    private void pause() {
        mPauseButton.setVisibility(View.GONE);
        mPlayButton.setVisibility(View.VISIBLE);
        if (playerPrepared()) {
            mPlayer.pause();
        }
    }

    private void initPreviousButton(View root) {
        mPreviousButton = (ImageButton)root.findViewById(R.id.player_previous);
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previous();
            }
        });
    }

    private void previous() {
        // TODO
        makeToast("PREVIOUS BUTTON CLICKED");
    }

    private void initNextButton(View root) {
        mNextButton = (ImageButton)root.findViewById(R.id.player_next);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next();
            }
        });
    }

    private void next() {
        // TODO
        makeToast("NEXT BUTTON CLICKED");
    }

    private void toggleButtonsEnabled(boolean enabled) {
        mPauseButton.setEnabled(enabled);
        mPlayButton.setEnabled(enabled);
        mPreviousButton.setEnabled(enabled);
        mNextButton.setEnabled(enabled);
    }

    private boolean playerPrepared() {
        return mPlayer != null && mPlayerPrepared;
    }

    private void makeToast(String message) {
        if (mCurrentToast != null) {
            mCurrentToast.cancel();
        }
        mCurrentToast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
        mCurrentToast.show();
    }
}
