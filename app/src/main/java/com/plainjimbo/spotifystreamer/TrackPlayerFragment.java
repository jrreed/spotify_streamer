package com.plainjimbo.spotifystreamer;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by jamesreed on 9/10/15.
 */
public class TrackPlayerFragment extends Fragment implements View.OnClickListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {
    private static final String BUNDLE_AUTO_PLAY = "autoPlay";
    private static final String BUNDLE_POSITION = "position";
    private static final String BUNDLE_TRACK_INDEX = "trackIndex";
    private ArtistListItem mArtist = null;
    private Toast mCurrentToast = null;
    private MediaPlayer mPlayer = null;
    private boolean mPlayerPrepared = false;
    private int mPositionMs = 0;
    private boolean mProgressRenderingAllowed = true;
    private boolean mShouldPlay = true;
    private TrackListItem mTrack = null;
    private int mTrackIndex = -1;
    private ArrayList<TrackListItem> mTrackList = null;
    private TrackPlayerMonitorTask mCurrentTask = null;

    // Track data views
    TextView artistNameView =null;
    TextView albumNameView = null;
    ImageView albumPhotoView = null;
    TextView trackNameView = null;
    TextView trackDurationView = null;

    // Player Controls
    private LinearLayout mPlayerControls = null;
    private RelativeLayout mPlayerStats = null;
    private ImageButton mPauseButton = null;
    private ImageButton mPlayButton = null;
    private ImageButton mPreviousButton = null;
    private ImageButton mNextButton = null;
    private TextView mLoadingText = null;
    private SeekBar mScrubber = null;
    private TextView mProgressText = null;

    public TrackPlayerFragment() {}

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
        renderPosition();
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
            setPositionMillis(mPlayer.getCurrentPosition());
        }
        savedInstanceState.putInt(BUNDLE_POSITION, mPositionMs);
        savedInstanceState.putBoolean(BUNDLE_AUTO_PLAY, mShouldPlay);
        savedInstanceState.putInt(BUNDLE_TRACK_INDEX, mTrackIndex);
    }

    @Override
    public void onPause() {
        super.onPause();
        boolean shouldPlayWas = mShouldPlay;
        if (playerPrepared()) {
            setPositionMillis(mPlayer.getCurrentPosition());
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
        setTrack(intent.getIntExtra(TrackPlayerActivity.EXTRA_TRACK_INDEX, -1), 0);
    }

    private void setTrack(int trackIndex, int positionMs) {
        mTrackIndex = trackIndex;
        mTrack = mTrackList.get(mTrackIndex);
        setPositionMillis(positionMs);
    }

    private void setPositionMillis(int positionMs) {
        mPositionMs = positionMs;
    }

    private boolean canRestoreInstanceState(Bundle savedInstanceState) {
        return savedInstanceState.containsKey(BUNDLE_TRACK_INDEX);
    }

    private void restoreInstanceState(Bundle savedInstanceState) {
        if (canRestoreInstanceState(savedInstanceState)) {
            // Optional data
            int positionMs = 0;
            if (savedInstanceState.containsKey(BUNDLE_POSITION)) {
                positionMs = savedInstanceState.getInt(BUNDLE_POSITION);
            }
            if (savedInstanceState.containsKey(BUNDLE_AUTO_PLAY)) {
                mShouldPlay = savedInstanceState.getBoolean(BUNDLE_AUTO_PLAY);
            }
            setTrack(savedInstanceState.getInt(TrackPlayerActivity.EXTRA_TRACK_INDEX, -1), positionMs);
        }
    }

    private void initPlayer() {
        mPlayerPrepared = false;
        if (mPlayer == null) {
            createPlayer();
        }
        else {
            resetPlayer();
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

    private void resetPlayer() {
        stopPlayerProgressMonitor();
        mPlayer.reset();
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
        renderDuration();
        togglePlayerControls(true);
        if (mPositionMs > 0) {
            mPlayer.seekTo(mPositionMs);
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
        stopPlayerProgressMonitor();
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        pause();
        togglePlayerControls(false);
    }

    private void initPlayerControls(View rootView) {
        mPlayerControls = (LinearLayout)rootView.findViewById(R.id.player_controls);
        mPlayerStats = (RelativeLayout)rootView.findViewById(R.id.player_stats);
        mLoadingText = (TextView)rootView.findViewById(R.id.loading_text);

        mPlayButton = (ImageButton)rootView.findViewById(R.id.player_play);
        mPlayButton.setOnClickListener(this);

        mPauseButton = (ImageButton)rootView.findViewById(R.id.player_pause);
        mPauseButton.setOnClickListener(this);

        mPreviousButton = (ImageButton)rootView.findViewById(R.id.player_previous);
        mPreviousButton.setOnClickListener(this);

        mNextButton = (ImageButton)rootView.findViewById(R.id.player_next);
        mNextButton.setOnClickListener(this);

        mScrubber = (SeekBar)rootView.findViewById(R.id.player_scrubber);
        mScrubber.setOnSeekBarChangeListener(this);
        mProgressText = (TextView)rootView.findViewById(R.id.player_progress);
    }

    private void renderPosition() {
        mProgressText.setText(millisToTimeString(mPositionMs));
        if (mProgressRenderingAllowed) {
            mScrubber.setProgress(mPositionMs);
        }
    }

    private void renderDuration() {
        mScrubber.setMax(mPlayer.getDuration());
        trackDurationView.setText(millisToTimeString(mPlayer.getDuration()));
    }

    private void togglePlayerControls(boolean enabled) {
        mScrubber.setEnabled(enabled);
        if (enabled) {
            mPlayerStats.setVisibility(View.VISIBLE);
            mPlayerControls.setVisibility(View.VISIBLE);
            mLoadingText.setVisibility(View.GONE);
        } else {
            mPlayerStats.setVisibility(View.GONE);
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

    private String millisToTimeString(int millis) {
        long seconds = (millis / 1000) % 60;
        long minutes = (millis / (1000*60)) % 60;
        return String.format("%01d:%02d", minutes, seconds);
    }

    private void play() {
        mShouldPlay = true;
        mPlayButton.setVisibility(View.GONE);
        mPauseButton.setVisibility(View.VISIBLE);
        if (playerPrepared()) {
            startPlayerProgressMonitor();
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
        changeTrack(mTrackIndex - 1);
    }

    private void next() {
        changeTrack(mTrackIndex + 1);
    }

    private void changeTrack(int trackIndex) {
        if (trackIndex <= -1) {
            trackIndex = mTrackList.size() - 1;
        }
        else if (trackIndex >= mTrackList.size()) {
            trackIndex = 0;
        }
        setTrack(trackIndex, 0);
        renderTrack();
        renderPosition();
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

    private void startPlayerProgressMonitor() {
        stopPlayerProgressMonitor();
        mCurrentTask = new TrackPlayerMonitorTask();
        mCurrentTask.execute();
    }

    private void stopPlayerProgressMonitor() {
        if (mCurrentTask != null) {
            mCurrentTask.cancel(true);
            mCurrentTask = null;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser && playerPrepared()) {
            mPlayer.seekTo(progress);
            setPositionMillis(progress);
            renderPosition();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mProgressRenderingAllowed = false;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mProgressRenderingAllowed = true;
    }

    private class TrackPlayerMonitorTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                while(true) {
                    if (mShouldPlay) {
                        publishProgress(mPlayer.getCurrentPosition());
                    }
                    Thread.sleep(250);
                }
            } catch (InterruptedException e) { /* Task Cancelled */ }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            if (progress.length == 0) {
                return;
            }
            setPositionMillis(progress[0]);
            renderPosition();
            return;
        }
    }
}
