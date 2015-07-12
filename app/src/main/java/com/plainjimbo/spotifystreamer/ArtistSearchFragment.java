package com.plainjimbo.spotifystreamer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistSearchFragment extends Fragment implements TextView.OnEditorActionListener, AdapterView.OnItemClickListener {

    private ArtistListAdapter mArtistListAdapter = null;

    public ArtistSearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artist_search, container, false);
        initSearchField(rootView);
        initArtistListAndAdapter(rootView);
        return rootView;
    }

    @Override
    public boolean onEditorAction(TextView view, int action, KeyEvent event) {
        boolean handled = false;
        if (action == EditorInfo.IME_ACTION_SEARCH) {
            new ArtistSearchTask().execute(view.getText().toString());
        }
        return handled;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String, Object> artist = mArtistListAdapter.getItem(position);
        Toast.makeText(getActivity(), (String)artist.get("name"), Toast.LENGTH_SHORT).show();
    }

    private void initSearchField(View rootView) {
        EditText searchField = (EditText)rootView.findViewById(R.id.artist_search_edit_text);
        searchField.setOnEditorActionListener(this);
    }

    private void initArtistListAndAdapter(View rootView) {
        ListView artistListView = (ListView)rootView.findViewById(R.id.artist_search_list_view);
        mArtistListAdapter = new ArtistListAdapter(getActivity());
        artistListView.setAdapter(mArtistListAdapter);
        artistListView.setOnItemClickListener(this);
    }

    private class ArtistSearchTask extends AsyncTask<String, Void, ArrayList<HashMap<String,Object>>> {
        protected ArrayList<HashMap<String,Object>> doInBackground(String... query) {
            if (query.length == 0) {
                return null;
            }
            // TODO execute artist search
            return getArtistList(query[0]);
        }

        protected void onPostExecute(ArrayList<HashMap<String,Object>> artistList) {
            if (artistList != null) {
                for(HashMap<String,Object> artist : artistList) {
                    mArtistListAdapter.add(artist);
                }
                mArtistListAdapter.notifyDataSetChanged();
            }
        }

        private ArrayList<HashMap<String,Object>> getArtistList(String query) {
            ArrayList<HashMap<String,Object>> artistList = new ArrayList<HashMap<String,Object>>();
            addArtist(artistList, query, R.mipmap.ic_launcher);
            return artistList;
        }

        private void addArtist(ArrayList artistList, String name, int imageResource) {
            HashMap<String,Object> artist =  new HashMap<String,Object>();
            artist.put("name", name);
            artist.put("image", imageResource);
            artistList.add(artist);
        }
    }
}
