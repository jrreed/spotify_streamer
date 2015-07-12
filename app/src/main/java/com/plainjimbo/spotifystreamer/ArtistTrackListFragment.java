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
import android.widget.Toast;

import java.util.ArrayList;
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
    public static final String ARTIST_ID = "artistId";
    private static final String BUNDLE_TRACK_LIST = "trackList";
    private TrackListAdapter mTrackListAdapter = null;
    private Toast mCurrentToast = null;

    public ArtistTrackListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<TrackListItem> trackList = null;
        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_TRACK_LIST)) {
            trackList = savedInstanceState.getParcelableArrayList(BUNDLE_TRACK_LIST);
        }
        initTrackListAdapter(trackList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artist_track_list, container, false);
        initTrackListView(rootView);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList(BUNDLE_TRACK_LIST, mTrackListAdapter.getTrackList());
    }

    private void initTrackListView(View rootView) {
        ListView trackListView = (ListView) rootView.findViewById(R.id.artist_track_list_list_view);
        trackListView.setAdapter(mTrackListAdapter);
    }

    private void initTrackListAdapter(ArrayList<TrackListItem> trackList) {
        if (trackList == null) {
            mTrackListAdapter = new TrackListAdapter(getActivity());
            Intent intent = getActivity().getIntent();
            String artist_id = intent.getStringExtra(ARTIST_ID);
            new TrackListFetchTask().execute(artist_id);
        } else {
            mTrackListAdapter = new TrackListAdapter(getActivity(), trackList);
        }
    }

    private void makeToast(String message) {
        if (mCurrentToast != null) {
            mCurrentToast.cancel();
        }
        mCurrentToast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
        mCurrentToast.show();
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
            if (trackList == null) {
                makeToast("Sorry, we weren't able to complete your request. Please check your " +
                          "internet connection and try again.");
            } else {
                if (trackList.size() == 0) {
                    makeToast("There are no tracks available for this artist");
                }
                for (Track track : trackList) {
                    mTrackListAdapter.add(new TrackListItem(track));
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


