package com.plainjimbo.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Pager;
import retrofit.RetrofitError;


/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistSearchFragment extends Fragment implements TextView.OnEditorActionListener, AdapterView.OnItemClickListener {

    private ArtistListAdapter mArtistListAdapter = null;
    private Toast mCurrentToast = null;

    private static final String BUNDLE_ARTIST_LIST = "artistList";

    public ArtistSearchFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<ArtistListItem> artistList = null;
        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_ARTIST_LIST) ) {
            artistList = savedInstanceState.getParcelableArrayList(BUNDLE_ARTIST_LIST);
        }
        initArtistListAdapter(artistList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artist_search, container, false);
        initSearchField(rootView);
        initArtistListView(rootView);
        return rootView;
    }

    @Override public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList(BUNDLE_ARTIST_LIST, mArtistListAdapter.getArtistList());
    }

    @Override
    public boolean onEditorAction(TextView view, int action, KeyEvent event) {
        boolean handled = false;
        if (action == EditorInfo.IME_ACTION_SEARCH) {
            mArtistListAdapter.clear();
            new ArtistSearchTask().execute(view.getText().toString());
        }
        return handled;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ArtistListItem artist = mArtistListAdapter.getItem(position);
        Intent intent = new Intent(getActivity(), ArtistTrackListActivity.class);
        intent.putExtra(ArtistTrackListFragment.ARTIST_ID, artist.getId());
        startActivity(intent);
    }

    private void initSearchField(View rootView) {
        EditText searchField = (EditText) rootView.findViewById(R.id.artist_search_edit_text);
        searchField.setOnEditorActionListener(this);
    }

    private void initArtistListView(View rootView) {
        ListView artistListView = (ListView) rootView.findViewById(R.id.artist_search_list_view);
        artistListView.setAdapter(mArtistListAdapter);
        artistListView.setOnItemClickListener(this);
    }

    private void initArtistListAdapter(ArrayList<ArtistListItem> artistList) {
        if (artistList == null) {
            mArtistListAdapter = new ArtistListAdapter(getActivity());
        } else {
            mArtistListAdapter = new ArtistListAdapter(getActivity(), artistList);
        }
    }

    private void makeToast(String message) {
        if (mCurrentToast != null) {
            mCurrentToast.cancel();
        }
        mCurrentToast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
        mCurrentToast.show();
    }

    private class ArtistSearchTask extends AsyncTask<String, Void, Pager<Artist>> {
        private final String LOG_TAG = ArtistSearchTask.class.getSimpleName();

        protected Pager<Artist> doInBackground(String... query) {
            if (query.length == 0) {
                return null;
            }
            return getArtistList(query[0]);
        }

        protected void onPostExecute(Pager<Artist> artistPager) {
            if (artistPager == null) {
                makeToast("Sorry, we weren't able to complete your request. Please check your " +
                        "internet connection and try again.");
            } else {
                if (artistPager.items.size() == 0) {
                    makeToast("Your search did not match any artists");
                } else {
                    for (Artist artist : artistPager.items) {
                        mArtistListAdapter.add(new ArtistListItem(artist));
                    }
                    mArtistListAdapter.notifyDataSetChanged();
                }
            }
        }

        private Pager<Artist> getArtistList(String query) {
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            Pager<Artist> artistPager = null;
            try {
                artistPager = spotify.searchArtists(query).artists;
            } catch (RetrofitError error) {
                SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
                Log.e(LOG_TAG, spotifyError.getMessage(), spotifyError);
                spotifyError.printStackTrace();
            }
            return artistPager;
        }
    }
}
