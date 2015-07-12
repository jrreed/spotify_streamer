package com.plainjimbo.spotifystreamer;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * This class serves as an Adapter that can be used to render ArtistListItem information in a ListView.
 * Created by jamesreed on 7/11/15.
 */
public class ArtistListAdapter extends ArrayAdapter<ArtistListItem> {
    public static final int ARTIST_LAYOUT = R.layout.list_item_artist;
    public static final int PREFERRED_IMAGE_DIM = 200;

    public ArtistListAdapter(Context context) {
        super(context, ARTIST_LAYOUT);
    }

    public ArtistListAdapter(Context context, ArrayList<ArtistListItem> artistList) {
        super(context, ARTIST_LAYOUT, artistList);
    }

    public ArrayList<ArtistListItem> getArtistList() {
        int artistCount = getCount();
        ArrayList<ArtistListItem> artistList = new ArrayList<ArtistListItem>();
        for (int index = 0; index < artistCount; index++) {
            artistList.add(getItem(index));
        }
        return artistList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ArtistListItem artist = getItem(position);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(ARTIST_LAYOUT, null);
        }
        setPhoto(view, artist);
        setName(view, artist);
        return view;
    }

    private void setPhoto(View artistView, ArtistListItem artist) {
        ImageView photoImageView = (ImageView) artistView.findViewById(R.id.list_item_artist_photo);
        if (artist.getImageUrl() != null) {
            photoImageView.setBackgroundColor(Color.TRANSPARENT);
            Picasso.with(getContext())
                    .load(artist.getImageUrl())
                    .resize(PREFERRED_IMAGE_DIM, PREFERRED_IMAGE_DIM)
                    .centerInside()
                    .into(photoImageView);
        } else {
            photoImageView.setImageDrawable(null);
            photoImageView.setBackgroundColor(getContext().getResources().getColor(R.color.medium_gray));
        }
    }

    private void setName(View artistView, ArtistListItem artist) {
        TextView nameTextView = (TextView) artistView.findViewById(R.id.list_item_artist_name);
        nameTextView.setText(artist.getName());
    }
}
