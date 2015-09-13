package com.plainjimbo.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;


public class ArtistSearchActivity extends AppCompatActivity implements
        ArtistSearchFragment.OnArtistSelected, ArtistTrackListFragment.OnTrackSelected {

    private static final String TAG_ARTIST_TRACK_LIST_FRAGMENT = "artistTaskListFragment";
    private static final String BUNDLE_SUBTITLE = "subtitle";
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_search);
        if (findViewById(R.id.artist_track_list_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                Fragment fragment = new ArtistTrackListFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.artist_track_list_container, fragment, TAG_ARTIST_TRACK_LIST_FRAGMENT)
                        .commit();
            } else if (savedInstanceState.containsKey(BUNDLE_SUBTITLE)){
                setSubtitle(savedInstanceState.getString(BUNDLE_SUBTITLE));
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(BUNDLE_SUBTITLE, getSubtitle());
    }

    @Override
    public void onArtistSelected(ArtistListItem artist) {
        if (mTwoPane) {
            setSubtitle(artist.getName());
            Fragment fragment = ArtistTrackListFragment.create(artist);
            getSupportFragmentManager().beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.artist_track_list_container, fragment, TAG_ARTIST_TRACK_LIST_FRAGMENT)
                    .commit();

        } else {
            Intent intent = new Intent(this, ArtistTrackListActivity.class);
            intent.putExtra(ArtistTrackListActivity.EXTRA_ARTIST, artist);
            startActivity(intent);
        }
    }

    private void setSubtitle(String artistName) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(artistName);
        }
    }

    private String getSubtitle() {
        String subtitle = null;
        if (getSupportActionBar() != null && getSupportActionBar().getSubtitle() != null) {
            subtitle = getSupportActionBar().getSubtitle().toString();
        }
        return subtitle;
    }

    @Override
    public void onTrackSelected(ArtistListItem artist, ArrayList<TrackListItem> trackList, int position) {
        DialogFragment fragment = TrackPlayerFragment.create(artist, trackList, position);
        fragment.show(getSupportFragmentManager(), "dialog");
    }
}
