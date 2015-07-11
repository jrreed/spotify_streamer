package com.plainjimbo.spotifystreamer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistSearchFragment extends Fragment {

    public ArtistSearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artist_search, container, false);
        initSearchField(rootView);
        return rootView;
    }

    private void initSearchField(View rootView) {
        EditText searchField = (EditText)rootView.findViewById(R.id.artist_search_edit_text);
        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView view, int action, KeyEvent event) {
                boolean handled = false;
                if (action == EditorInfo.IME_ACTION_SEARCH) {
                    makeToast("Searching for: " + view.getText());
                    handled = true;
                }
                return handled;
            }
        });
    }

    private void makeToast(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }
}
