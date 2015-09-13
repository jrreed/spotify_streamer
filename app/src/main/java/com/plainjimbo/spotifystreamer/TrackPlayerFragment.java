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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by jamesreed on 9/10/15.
 */
public class TrackPlayerFragment extends Fragment implements View.OnClickListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    private static final String BUNDLE_AUTO_PLAY = "autoPlay";
    private static final String BUNDLE_POSITION = "position";
    private static final String BUNDLE_TRACK_INDEX = "trackIndex";
    private ArtistListItem mArtist = null;
    private Toast mCurrentToast = null;
    private MediaPlayer mPlayer = null;
    private boolean mPlayerPrepared = false;
    private int mPosition = 0;
    private boolean mShouldPlay = false;
    private TrackListItem mTrack = null;
    private int mTrackIndex = -1;
    private ArrayList<TrackListItem> mTrackList = null;

    // Track data views
    TextView artistNameView =null;
    TextView albumNameView = null;
    ImageView albumPhotoView = null;
    TextView trackNameView = null;
    TextView trackDurationView = null;

    // Player Controls
    private LinearLayout mPlayerControls = null;
    private ImageButton mPauseButton = null;
    private ImageButton mPlayButton = null;
    private ImageButton mPreviousButton = null;
    private ImageButton mNextButton = null;
    private TextView mLoadingText = null;

    public TrackPlayerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (canInitFragmentData()) {
            initFragmentData();
        }
        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_track_player, container, false);
        initPlayerControls(rootView);
        initTrackViews(rootView);
        renderTrack();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initPlayer();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (playerPrepared()) {
            mPosition = mPlayer.getCurrentPosition();
        }
        savedInstanceState.putInt(BUNDLE_POSITION, mPosition);
        savedInstanceState.putBoolean(BUNDLE_AUTO_PLAY, mShouldPlay);
        savedInstanceState.putInt(BUNDLE_TRACK_INDEX, mTrackIndex);
    }

    @Override
    public void onPause() {
        super.onPause();
        boolean shouldPlayWas = mShouldPlay;
        if (playerPrepared()) {
            mPosition = mPlayer.getCurrentPosition();
        }
        destroyPlayer();
        mShouldPlay = shouldPlayWas;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.player_play:
                play();
                break;
            case R.id.player_pause:
                pause();
                break;
            case R.id.player_previous:
                previous();
                break;
            case R.id.player_next:
                next();
                break;
        }
    }

    private boolean canInitFragmentData() {
        Intent intent = getActivity().getIntent();
        return intent.hasExtra(TrackPlayerActivity.EXTRA_ARTIST) &&
                intent.hasExtra(TrackPlayerActivity.EXTRA_TRACK_INDEX) &&
                intent.hasExtra(TrackPlayerActivity.EXTRA_TRACK_LIST);
    }

    private void initFragmentData() {
        Intent intent = getActivity().getIntent();
        mArtist = intent.getParcelableExtra(TrackPlayerActivity.EXTRA_ARTIST);
        mTrackList = intent.getParcelableArrayListExtra(TrackPlayerActivity.EXTRA_TRACK_LIST);
        setTrack(intent.getIntExtra(TrackPlayerActivity.EXTRA_TRACK_INDEX, -1));
    }

    private void setTrack(int trackIndex) {
        mTrackIndex = trackIndex;
        mTrack = mTrackList.get(mTrackIndex);
    }

    private boolean canRestoreInstanceState(Bundle savedInstanceState) {
        return savedInstanceState.containsKey(BUNDLE_TRACK_INDEX);
    }

    private void restoreInstanceState(Bundle savedInstanceState) {
        if (canRestoreInstanceState(savedInstanceState)) {
            // Optional data
            if (savedInstanceState.containsKey(BUNDLE_POSITION)) {
                mPosition = savedInstanceState.getInt(BUNDLE_POSITION);
            }
            if (savedInstanceState.containsKey(BUNDLE_AUTO_PLAY)) {
                mShouldPlay = savedInstanceState.getBoolean(BUNDLE_AUTO_PLAY);
            }
            setTrack(savedInstanceState.getInt(TrackPlayerActivity.EXTRA_TRACK_INDEX, -1));
        }
    }

    private void initPlayer() {
        mPlayerPrepared = false;
        if (mPlayer == null) {
            createPlayer();
        }
        else {
            mPlayer.reset();
        }
        try {
            mPlayer.setDataSource(mTrack.getPreviewUrl());
        } catch (IOException exc) {
            makeToast(getString(R.string.spotify_api_error_message));
        }
        mPlayer.prepareAsync();
        togglePlayerControls(false);
        if (!mShouldPlay) {
            pause();
        }
    }

    private void createPlayer() {
        mPlayer = new MediaPlayer();
        mPlayer.setOnErrorListener(this);
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    @Override
    public boolean onError(MediaPlayer player, int what, int extra) {
        makeToast(getString(R.string.spotify_api_error_message));
        destroyPlayer();
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer player) {
        mPlayerPrepared = true;
        togglePlayerControls(true);
        if (mPosition > 0) {
            mPlayer.seekTo(mPosition);
        }
        if (mShouldPlay) {
            play();
        }
    }

    @Override
    public void onCompletion(MediaPlayer player) {
        next();
    }

    private void destroyPlayer() {
        mPlayerPrepared = false;
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        pause();
        togglePlayerControls(false);
    }

    private void initPlayerControls(View rootView) {
        mPlayerControls = (LinearLayout)rootView.findViewById(R.id.player_controls);
        mLoadingText = (TextView)rootView.findViewById(R.id.loading_text);

        mPlayButton = (ImageButton)rootView.findViewById(R.id.player_play);
        mPlayButton.setOnClickListener(this);

        mPauseButton = (ImageButton)rootView.findViewById(R.id.player_pause);
        mPauseButton.setOnClickListener(this);

        mPreviousButton = (ImageButton)rootView.findViewById(R.id.player_previous);
        mPreviousButton.setOnClickListener(this);

        mNextButton = (ImageButton)rootView.findViewById(R.id.player_next);
        mNextButton.setOnClickListener(this);
    }

    private void togglePlayerControls(boolean enabled) {
        if (enabled) {
            mPlayerControls.setVisibility(View.VISIBLE);
            mLoadingText.setVisibility(View.GONE);
        } else {
            mPlayerControls.setVisibility(View.GONE);
            mLoadingText.setVisibility(View.VISIBLE);
        }
    }

    private void initTrackViews(View rootView) {
        artistNameView =(TextView)rootView.findViewById(R.id.artist_name);
        albumNameView = (TextView)rootView.findViewById(R.id.album_name);
        albumPhotoView = (ImageView)rootView.findViewById(R.id.album_photo);
        trackNameView = (TextView)rootView.findViewById(R.id.track_name);
        trackDurationView = (TextView)rootView.findViewById(R.id.track_duration);
    }

    private void renderTrack() {
        String imageUrl = mTrack.getWallpaperImageUrl();
        artistNameView.setText(mArtist.getName());
        albumNameView.setText(mTrack.getAlbumName());
        trackNameView.setText(mTrack.getName());
        trackDurationView.setText(millisToTimeString(mTrack.getDuration()));
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

    private String millisToTimeString(long millis) {
        long seconds = (millis / 10000) % 60;
        long minutes = (millis / (1000*60)) % 60;
        return String.format("%01d:%02d", minutes, seconds);
    }

    private void play() {
        mShouldPlay = true;
        mPlayButton.setVisibility(View.GONE);
        mPauseButton.setVisibility(View.VISIBLE);
        if (playerPrepared()) {
            mPlayer.start();
        }
    }

    private void pause() {
        mShouldPlay = false;
        mPauseButton.setVisibility(View.GONE);
        mPlayButton.setVisibility(View.VISIBLE);
        if (playerPrepared()) {
            mPlayer.pause();
        }
    }

    private void previous() {
        int previousIndex = mTrackIndex - 1;
        if (previousIndex <= -1) {
            previousIndex = mTrackList.size() - 1;
        }
        setTrack(previousIndex);
        renderTrack();
        initPlayer();
    }

    private void next() {
        int nextIndex = mTrackIndex + 1;
        if (nextIndex >= mTrackList.size()) {
            nextIndex = 0;
        }
        setTrack(nextIndex);
        renderTrack();
        initPlayer();
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
