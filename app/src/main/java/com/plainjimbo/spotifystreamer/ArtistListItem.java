package com.plainjimbo.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by jamesreed on 7/12/15.
 */
public class ArtistListItem extends Artist implements Parcelable {
    public static final int PREFERRED_IMAGE_DIM = 200;

    private String mId = null;
    private String mImageUrl = null;
    private String mName = null;

    public ArtistListItem(Artist artist) {
        mId = artist.id;
        OptimalImageFinder imageFinder = new OptimalImageFinder(artist.images);
        Image image = imageFinder.closestTo(PREFERRED_IMAGE_DIM);
        if (image != null) {
            mImageUrl = image.url;
        }
        mName = artist.name;
    }

    public String getId() {
        return mId;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getName() {
        return mName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel outStream, int flags) {
        outStream.writeString(mId);
        outStream.writeString(mImageUrl);
        outStream.writeString(mName);
    }

    public static final Parcelable.Creator<ArtistListItem> CREATOR = new Parcelable.Creator<ArtistListItem>() {
        @Override
        public ArtistListItem createFromParcel(Parcel inStream) {
            return new ArtistListItem(inStream);
        }

        @Override
        public ArtistListItem[] newArray(int size) {
            return new ArtistListItem[size];
        }
    };

    /**
     * Initializes a ArtistListItem from a Parcel input stream.
     */
    private ArtistListItem(Parcel inStream) {
        mId = inStream.readString();
        mImageUrl = inStream.readString();
        mName = inStream.readString();
    }
}
