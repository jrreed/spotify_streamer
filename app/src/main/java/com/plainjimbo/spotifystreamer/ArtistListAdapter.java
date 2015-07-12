package com.plainjimbo.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;

/**
 * This class serves as an Adapter that can be used to render Artist information in a ListView.
 * Created by jamesreed on 7/11/15.
 */
public class ArtistListAdapter extends ArrayAdapter<Artist> {
    public static final int ARTIST_LAYOUT = R.layout.list_item_artist;
    public static final int PREFERRED_IMAGE_DIM = 200;

    public ArtistListAdapter(Context context) {
        super(context, ARTIST_LAYOUT);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        Artist artist = getItem(position);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(ARTIST_LAYOUT, null);
        }
        setPhoto(view, artist);
        setName(view, artist);
        return view;
    }

    private void setPhoto(View artistView, Artist artist) {
        ImageView photoImageView = (ImageView) artistView.findViewById(R.id.list_item_artist_photo);
        OptimalImageFinder imageFinder = new OptimalImageFinder(artist.images);
        Image image = imageFinder.closestTo(PREFERRED_IMAGE_DIM);
        if (image != null) {
            Picasso.with(getContext())
                    .load(image.url)
                    .resize(PREFERRED_IMAGE_DIM, PREFERRED_IMAGE_DIM)
                    .centerCrop()
                    .into(photoImageView);
        } else {
            photoImageView.setImageDrawable(null);
        }
    }

    private void setName(View artistView, Artist artist) {
        TextView nameTextView = (TextView) artistView.findViewById(R.id.list_item_artist_name);
        nameTextView.setText(artist.name);
    }
}
