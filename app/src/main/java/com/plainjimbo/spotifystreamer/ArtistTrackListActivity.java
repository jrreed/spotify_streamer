package com.plainjimbo.spotifystreamer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by jamesreed on 7/11/15.
 */
public class ArtistTrackListActivity extends AppCompatActivity {
    public static final String EXTRA_ARTIST = "artist";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_track_list);
        initSubtitle();
    }

    private void initSubtitle() {
        ArtistListItem artist = getIntent().getParcelableExtra(EXTRA_ARTIST);
        getSupportActionBar().setSubtitle(artist.getName());
    }
}
