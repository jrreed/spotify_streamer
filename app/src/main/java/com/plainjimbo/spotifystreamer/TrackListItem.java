package com.plainjimbo.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by jamesreed on 7/12/15.
 */
public class TrackListItem extends Track implements Parcelable {
    public static final int PREFERRED_IMAGE_DIM = 200;

    private String mAlbumName = null;
    private String mId = null;
    private String mImageUrl = null;
    private String mName = null;

    public TrackListItem(Track track) {
        mId = track.id;
        if (track.album != null) {
            OptimalImageFinder imageFinder = new OptimalImageFinder(track.album.images);
            Image image = imageFinder.closestTo(PREFERRED_IMAGE_DIM);
            if (image != null) {
                mImageUrl = image.url;
            }
            mAlbumName = track.album.name;
        }
        mName = track.name;
    }

    public String getAlbumName() {
        return mAlbumName;
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

    public static final Parcelable.Creator<TrackListItem> CREATOR = new Parcelable.Creator<TrackListItem>() {
        @Override
        public TrackListItem createFromParcel(Parcel inStream) {
            return new TrackListItem(inStream);
        }

        @Override
        public TrackListItem[] newArray(int size) {
            return new TrackListItem[size];
        }
    };

    /**
     * Initializes a TrackListItem from a Parcel input stream.
     */
    private TrackListItem(Parcel inStream) {
        mId = inStream.readString();
        mImageUrl = inStream.readString();
        mName = inStream.readString();
    }
}
