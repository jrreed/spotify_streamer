package com.plainjimbo.spotifystreamer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class TrackPlayerActivity extends AppCompatActivity {
    public static final String EXTRA_ARTIST = "artist";
    public static final String EXTRA_TRACK_INDEX = "trackIndex";
    public static final String EXTRA_TRACK_LIST = "trackList";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_player);
    }
}
