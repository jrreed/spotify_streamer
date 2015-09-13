package com.plainjimbo.spotifystreamer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

public class TrackPlayerActivity extends AppCompatActivity {
    public static final String EXTRA_ARTIST = "artist";
    public static final String EXTRA_TRACK_INDEX = "trackIndex";
    public static final String EXTRA_TRACK_LIST = "trackList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_player);
        if (savedInstanceState == null) {
            TrackPlayerFragment fragment = TrackPlayerFragment.create(getArtist(), getTrackList(), getPosition());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.track_player_container, fragment)
                    .commit();
        }
    }

    private ArtistListItem getArtist() {
        return getIntent().getParcelableExtra(EXTRA_ARTIST);
    }

    private ArrayList<TrackListItem> getTrackList() {
        return getIntent().getParcelableArrayListExtra(EXTRA_TRACK_LIST);
    }

    private int getPosition() {
        return getIntent().getIntExtra(EXTRA_TRACK_INDEX, -1);
    }
}
