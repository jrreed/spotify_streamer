package com.plainjimbo.spotifystreamer;


import java.util.List;

import kaaes.spotify.webapi.android.models.Image;

public class OptimalImageFinder {
    private List<Image> mImageList = null;

    /**
     * Must be initialized with a List<Image> that is sorted in descending order by image width.
     */
    public OptimalImageFinder(List<Image> imageList) {
        mImageList = imageList;
    }

    /**
     * Returns an image from the list for whose width is closest to the provided dimension without
     * going under. If no image has a width that is larger than or equal to the provided dimension
     * it will use the largest image available.
     */
    public Image closestTo(int preferredSize) {
        Image best = null;
        for (Image image : mImageList) {
            int distance = image.width - preferredSize;
            if (best == null || (distance > 0 && distance < (best.width - preferredSize))) {
                best = image;
            } else if (distance < 0) {
                break;
            }
        }
        return best;
    }
}
