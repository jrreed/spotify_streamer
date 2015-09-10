package com.plainjimbo.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by jamesreed on 7/12/15.
 */
public class TrackListItem extends Track implements Parcelable {
    public static final int PREFERRED_THUMBNAIL_IMAGE_DIM = 200;
    public static final int PREFERRED_WALLPAPER_IMAGE_DIM = 800;

    private String mAlbumName = null;
    private long mDuration = 0l;
    private String mId = null;
    private String mThumbnailImageUrl = null;
    private String mWallpaperImageUrl = null;
    private String mName = null;

    public TrackListItem(Track track) {
        mId = track.id;
        mDuration = track.duration_ms;
        if (track.album != null) {
            OptimalImageFinder imageFinder = new OptimalImageFinder(track.album.images);
            Image image = imageFinder.closestTo(PREFERRED_THUMBNAIL_IMAGE_DIM);
            if (image != null) {
                mThumbnailImageUrl = image.url;
            }
            image = imageFinder.closestTo(PREFERRED_WALLPAPER_IMAGE_DIM);
            if (image != null) {
                mWallpaperImageUrl = image.url;
            }
            mAlbumName = track.album.name;
        }
        mName = track.name;
    }

    public String getAlbumName() {
        return mAlbumName;
    }

    public long getDuration() {
        return mDuration;
    }

    public String getId() {
        return mId;
    }

    public String getThumbnailImageUrl() {
        return mThumbnailImageUrl;
    }

    public String getWallpaperImageUrl() {
        return mWallpaperImageUrl;
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
        outStream.writeString(mAlbumName);
        outStream.writeLong(mDuration);
        outStream.writeString(mId);
        outStream.writeString(mThumbnailImageUrl);
        outStream.writeString(mWallpaperImageUrl);
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
        mAlbumName = inStream.readString();
        mDuration = inStream.readLong();
        mId = inStream.readString();
        mThumbnailImageUrl = inStream.readString();
        mWallpaperImageUrl = inStream.readString();
        mName = inStream.readString();
    }
}
