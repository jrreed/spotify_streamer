package com.plainjimbo.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

public class ArtistTrackListActivity extends AppCompatActivity implements ArtistTrackListFragment.OnTrackSelected {
    public static final String EXTRA_ARTIST = "artist";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_track_list);
        if (savedInstanceState == null) {
            ArtistTrackListFragment fragment = ArtistTrackListFragment.create(getArtist());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.artist_track_list_container, fragment)
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

    private ArtistListItem getArtist() {
        return getIntent().getParcelableExtra(EXTRA_ARTIST);
    }

    @Override
    public void onTrackSelected(ArtistListItem artist, ArrayList<TrackListItem> trackList, int position) {
        Intent intent = new Intent(this, TrackPlayerActivity.class);
        intent.putExtra(TrackPlayerActivity.EXTRA_ARTIST, artist);
        intent.putExtra(TrackPlayerActivity.EXTRA_TRACK_INDEX, position);
        intent.putExtra(TrackPlayerActivity.EXTRA_TRACK_LIST, trackList);
        startActivity(intent);
    }
}
