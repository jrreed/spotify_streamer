package com.plainjimbo.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jamesreed on 7/11/15.
 */
public class ArtistTrackListFragment extends Fragment {

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
        String artist = intent.getStringExtra("artist");
        new TrackListFetchTask().execute(artist);
    }

    private class TrackListFetchTask extends AsyncTask<String, Void, ArrayList<HashMap<String,Object>>> {
        protected ArrayList<HashMap<String,Object>>  doInBackground(String... artist) {
            if (artist.length == 0) {
                return null;
            }
            return getTrackList(artist[0]);
        }

        protected void onPostExecute(ArrayList<HashMap<String,Object>> trackList) {
            if (trackList != null) {
                for (HashMap<String, Object> track : trackList) {
                    mTrackListAdapter.add(track);
                }
            }
        }

        private ArrayList<HashMap<String, Object>> getTrackList(String artist) {
            ArrayList<HashMap<String, Object>> trackList = new ArrayList<HashMap<String, Object>>();
            addTrack(trackList, "track-" + artist, R.mipmap.ic_launcher);
            return trackList;
        }

        private void addTrack(ArrayList trackList, String title, int imageResource) {
            HashMap<String, Object> track = new HashMap<String, Object>();
            track.put("albumImage", imageResource);
            track.put("albumTitle", "album-" + title);
            track.put("title", title);
            trackList.add(track);
        }
    }
}


