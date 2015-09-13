package com.plainjimbo.spotifystreamer;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

public class ArtistTrackListActivity extends AppCompatActivity {
    public static final String EXTRA_ARTIST = "artist";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_track_list);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.artist_track_list_container, new ArtistTrackListFragment())
                    .commit();
        }
        initSubtitle();
    }

    private void initSubtitle() {
        ArtistListItem artist = getIntent().getParcelableExtra(EXTRA_ARTIST);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null && artist != null) {
            getSupportActionBar().setSubtitle(artist.getName());
        }
    }
}
