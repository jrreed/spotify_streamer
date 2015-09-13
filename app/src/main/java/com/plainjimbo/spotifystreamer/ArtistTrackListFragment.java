package com.plainjimbo.spotifystreamer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

public class ArtistTrackListFragment extends Fragment implements AdapterView.OnItemClickListener {

    public interface OnTrackSelected {
        public void onTrackSelected(ArtistListItem artist, ArrayList<TrackListItem> trackList, int position);
    }

    private static final String BUNDLE_TRACK_LIST = "trackList";
    private static final String ARGS_ARTIST = "artist";
    private Toast mCurrentToast = null;
    private TrackListAdapter mTrackListAdapter = null;
    private TrackListFetchTask mCurrentTask = null;

    public ArtistTrackListFragment() {}

    public static ArtistTrackListFragment create(ArtistListItem artist) {
        ArtistTrackListFragment fragment = new ArtistTrackListFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARGS_ARTIST, artist);
        fragment.setArguments(args);
        return fragment;
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
        // If we have a current task then we weren't able to finish it. We have to cancel it so that
        // it doesn't try to post back to the UI of an Activity that has been destroyed. We don't
        // save any data to the bundle because the task didn't finish. We'll try to kick off the
        // task again in `onCreate(...)` if there isn't any data.
        if (mCurrentTask != null) {
            mCurrentTask.cancel(true);
        } else {
            savedInstanceState.putParcelableArrayList(BUNDLE_TRACK_LIST, mTrackListAdapter.getTrackList());
        }
    }

    private ArtistListItem getArtist() {
        return getArguments().getParcelable(ARGS_ARTIST);
    }

    private void initTrackListView(View rootView) {
        ListView trackListView = (ListView) rootView.findViewById(R.id.artist_track_list_list_view);
        trackListView.setAdapter(mTrackListAdapter);
        trackListView.setOnItemClickListener(this);
    }

    private void initTrackListAdapter(ArrayList<TrackListItem> trackList) {
        if (trackList == null) {
            mTrackListAdapter = new TrackListAdapter(getActivity());
            mCurrentTask = new TrackListFetchTask();
            mCurrentTask.execute(getArtist().getId());
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ((OnTrackSelected)getActivity()).onTrackSelected(getArtist(), mTrackListAdapter.getTrackList(), position);
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
                makeToast(getString(R.string.spotify_api_error_message));
            } else {
                if (trackList.size() == 0) {
                    makeToast(getString(R.string.empty_search_results_message));
                }
                for (Track track : trackList) {
                    mTrackListAdapter.add(new TrackListItem(track));
                }
            }
            mCurrentTask = null;
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


