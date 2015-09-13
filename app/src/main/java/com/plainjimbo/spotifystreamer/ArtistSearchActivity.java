package com.plainjimbo.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class ArtistSearchActivity extends AppCompatActivity implements ArtistSearchFragment.OnArtistSelected {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_search);
    }

    @Override
    public void onArtistSelected(ArtistListItem artist) {
        Intent intent = new Intent(this, ArtistTrackListActivity.class);
        intent.putExtra(ArtistTrackListActivity.EXTRA_ARTIST, artist);
        startActivity(intent);
    }
}
