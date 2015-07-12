package com.plainjimbo.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.HashMap;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.RetrofitError;

/**
 * Created by jamesreed on 7/11/15.
 */
public class ArtistTrackListFragment extends Fragment {
    public static final String ARTIST_ID = "artist_id";
    private TrackListAdapter mTrackListAdapter = null;

    public ArtistTrackListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artist_track_list, container, false);
        initTrackListAndAdapter(rootView);

        return rootView;
    }

    private void initTrackListAndAdapter(View rootView) {
        ListView trackListView = (ListView) rootView.findViewById(R.id.artist_track_list_list_view);
        mTrackListAdapter = new TrackListAdapter(getActivity());
        trackListView.setAdapter(mTrackListAdapter);

        Intent intent = getActivity().getIntent();
        String artist_id = intent.getStringExtra("artist_id");
        new TrackListFetchTask().execute(artist_id);
    }

    private class TrackListFetchTask extends AsyncTask<String, Void, List<Track>> {
        private final String LOG_TAG = TrackListFetchTask.class.getSimpleName();

        protected List<Track> doInBackground(String... artist_ids) {
            if (artist_ids.length == 0) {
                return null;
            }
            return getTrackList(artist_ids[0]);
        }

        protected void onPostExecute(List<Track> trackList) {
            if (trackList != null) {
                for (Track track : trackList) {
                    mTrackListAdapter.add(track);
                }
            }
        }

        private List<Track> getTrackList(String artist_id) {
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            HashMap<String, Object> queryParams = new HashMap<String, Object>();
            queryParams.put("country", "US");
            List<Track> trackList = null;
            try {
                trackList = spotify.getArtistTopTrack(artist_id, queryParams).tracks;
            } catch (RetrofitError error) {
                SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
                Log.e(LOG_TAG, spotifyError.getMessage(), spotifyError);
                spotifyError.printStackTrace();
            }
            return trackList;
        }
    }
}


